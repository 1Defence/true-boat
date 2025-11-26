package com.trueboat;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.WorldEntity;
import net.runelite.api.WorldView;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@PluginDescriptor(
		name = "True Boat",
		description = "Track an estimation of the boats true-tile, intended for use in trials."
)
public class TrueBoatPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private TrueBoatConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private TrueBoatOverlay overlay;

	WorldPoint destinationTile;
	WorldPoint directionTile;

	int currentAngle = 0;
	int previousAngle = 0;
	WorldEntity boatEntity;

	/*<|Cached Configs*/
	boolean showBackSquare,
			showFrontSquare,
			showDirectionTile;

	Color backBorderColor,backFillColor,
			frontBorderColor,frontFillColor,
			directionBorderColor,directionFillColor;

	float backBorderWidth,
			frontBorderWidth,
			directionBorderWidth;


	@Getter(AccessLevel.PACKAGE)
	public static final Map<Integer, int[]> directions = new ConcurrentHashMap<>();

	//Direction bit, array of X,Y offset the directional tile should appear relative to destination tile.
	static {
		directions.put(0,    new int[] {  0, -2});//South
		directions.put(128,  new int[] { -1, -2});//South-SouthWest
		directions.put(256,  new int[] { -2, -2});//SouthWest
		directions.put(384,  new int[] { -2,  -1});//West-SouthWest
		directions.put(512,  new int[] { -2,  0});//West
		directions.put(640,  new int[] { -2,  1});//West-NorthWest
		directions.put(768,  new int[] { -2,  2});//NorthWest
		directions.put(896,  new int[] { -1,  2});//North-NorthWest
		directions.put(1024, new int[] {  0,  2});//North
		directions.put(1152, new int[] {  1,  2});//North-NorthEast
		directions.put(1280, new int[] {  2,  2});//NorthEast
		directions.put(1408, new int[] {  2,  1});//East-NorthEast
		directions.put(1536, new int[] {  2,  0});//East
		directions.put(1664, new int[] {  2, -1});//East-SouthEast
		directions.put(1792, new int[] {  2, -2});//SouthEast
		directions.put(1920, new int[] {  1, -2});//South-SouthEast
	}

	@Provides
	TrueBoatConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TrueBoatConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		CacheConfigs();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
		if (!configChanged.getGroup().equals(TrueBoatConfig.GROUP))
		{
			return;
		}

		CacheConfigs();
	}

	public void CacheConfigs(){

		showBackSquare = config.showBackSquare();
			showFrontSquare = config.showFrontSquare();
			showDirectionTile = config.showDirectionTile();

		backBorderColor = config.backBorderColor();
			backFillColor = config.backFillColor();
			frontBorderColor = config.frontBorderColor();
			frontFillColor = config.frontFillColor();
			directionBorderColor = config.directionBorderColor();
			directionFillColor = config.directionFillColor();

		backBorderWidth = (float)config.backBorderWidth();
			frontBorderWidth = (float)config.frontBorderWidth();
			directionBorderWidth = (float)config.directionBorderWidth();
	}


	//Sort through available world entities for local players boat, set it and generate the destination tile.
	@Subscribe
	public void onGameTick(GameTick event)
	{
		if(boatEntity == null)
		{
			WorldView wv = client.getTopLevelWorldView();
			for (WorldEntity entity : wv.worldEntities())
			{
				if (entity.getOwnerType() == WorldEntity.OWNER_TYPE_SELF_PLAYER)
				{
					boatEntity = entity;
				}
			}
		}

		if(boatEntity == null)
			return;

		destinationTile = GetBoatDestinationTile();
	}

	/*
	If updating this on the tick, it will take 2 ticks to detect direction change rather than one,
	client tick causes a slight flicker unfortunately but better than nothing.
	 */
	@Subscribe
	public void onClientTick(ClientTick event){
		if(boatEntity == null)
			return;

		currentAngle = boatEntity.getOrientation();
		int[] direction = getDirection(currentAngle,previousAngle);
		directionTile = new WorldPoint(destinationTile.getX()+direction[0],destinationTile.getY()+direction[1],destinationTile.getPlane());
		previousAngle = currentAngle;
	}

	//Convert Local location of the destination tile into a worldPoint, additionally shifting it down 1 plane to render on the correct view
	WorldPoint GetBoatDestinationTile(){
		if(boatEntity == null)
			return null;
		LocalPoint lp = boatEntity.getLocalLocation();
		//setting plane to 0 shifts the tile underneath the boats worldview. possibly grab current player plane and dz -1 instead
		return WorldPoint.fromScene(client.getTopLevelWorldView().getScene(),lp.getSceneX(),lp.getSceneY(),0);
	}


	/*
	Checks previous direction against current direction to predict the new angle this tick
	0-127 = south, the moment it reaches 128 it should be South-South-West rather than waiting till 256.
	There's a Varbit that updates direction as well however it's very slow/delayed.
	 */
	static int[] getDirection(int cAngle, int pAngle) {
		final boolean roundUp = cAngle > pAngle || (cAngle < 128 && pAngle > 1920);

		final boolean exactDirectionMatch = directions.containsKey(cAngle);

		int baseDir = (cAngle/128)*128;
		int predictedDir = roundUp ? baseDir+128 : baseDir;
		if(predictedDir == 2048){
			predictedDir = 0;
		}

		if(exactDirectionMatch){
			return directions.get(baseDir);
		}else{
			return directions.get(predictedDir);
		}
	}

}
