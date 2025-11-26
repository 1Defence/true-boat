package com.trueboat;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup(TrueBoatConfig.GROUP)
public interface TrueBoatConfig extends Config
{
	String GROUP = "trueBoat";

	@ConfigItem(
			position = 0,
			keyName = "showBackSquare",
			name = "Show Back Square",
			description = "Render the back 3x3 section of your boat."
	)
	default boolean showBackSquare()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 1,
			keyName = "backBorderColor",
			name = "Back Border Color",
			description = "Border Color of the back section of your boat."
	)
	default Color backBorderColor()
	{
		return new Color(0,0,0,50);
	}

	@Alpha
	@ConfigItem(
			position = 2,
			keyName = "backFillColor",
			name = "Back Fill Color",
			description = "Fill Color of the back section of your boat."
	)
	default Color backFillColor()
	{
		return new Color(0,0,0,50);
	}

	@Range(min = 0, max = 5)
	@ConfigItem(
			position = 3,
			keyName = "backBorderWidth",
			name = "Back Border Width",
			description = "Border width of the back section of your boat."
	)
	default double backBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 4,
			keyName = "showFrontSquare",
			name = "Show Front Square",
			description = "Render the front 3x3 section of your boat."
	)
	default boolean showFrontSquare()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 5,
			keyName = "frontBorderColor",
			name = "Front Border Color",
			description = "Border Color of the front section of your boat."
	)
	default Color frontBorderColor()
	{
		return new Color(0,0,0,50);
	}

	@Alpha
	@ConfigItem(
			position = 6,
			keyName = "frontFillColor",
			name = "Front Fill Color",
			description = "Fill Color of the front section of your boat."
	)
	default Color frontFillColor()
	{
		return new Color(0,0,0,50);
	}

	@Range(min = 0, max = 5)
	@ConfigItem(
			position = 7,
			keyName = "frontBorderWidth",
			name = "Front Border Width",
			description = "Border width of the front section of your boat."
	)
	default double frontBorderWidth()
	{
		return 2;
	}

	@ConfigItem(
			position = 8,
			keyName = "showDirectionTile",
			name = "Show Direction Tile",
			description = "Render the current directional tile of your boat."
	)
	default boolean showDirectionTile()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			position = 9,
			keyName = "directionBorderColor",
			name = "Direction Border Color",
			description = "Border Color of the direction of your boat."
	)
	default Color directionBorderColor()
	{
		return Color.orange;
	}

	@Alpha
	@ConfigItem(
			position = 10,
			keyName = "directionFillColor",
			name = "Direction Fill Color",
			description = "Fill Color of the directional tile of your boat."
	)
	default Color directionFillColor()
	{
		return new Color(255,255,255,50);
	}

	@Range(min = 0, max = 5)
	@ConfigItem(
			position = 11,
			keyName = "directionBorderWidth",
			name = "Direction Border Width",
			description = "Border width of the directional tile of your boat."
	)
	default double directionBorderWidth()
	{
		return 2;
	}


}
