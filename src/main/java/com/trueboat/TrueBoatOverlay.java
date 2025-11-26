package com.trueboat;

import java.awt.*;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TrueBoatOverlay extends Overlay
{
    private final Client client;
    private final TrueBoatPlugin plugin;


    @Inject
    private TrueBoatOverlay(Client client, TrueBoatPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(PRIORITY_LOW);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if(plugin.destinationTile != null && plugin.showBackSquare)
        {
            DrawSquare(graphics, plugin.destinationTile,
                    plugin.backBorderColor,
                    plugin.backFillColor,
                    plugin.backBorderWidth);
        }

        if(plugin.directionTile != null){

            if(plugin.showFrontSquare)
            {
                DrawSquare(graphics, plugin.directionTile,
                        plugin.frontBorderColor,
                        plugin.frontFillColor,
                        plugin.frontBorderWidth);
            }
            if(plugin.showDirectionTile){
                renderTile(graphics, plugin.directionTile,
                        plugin.directionBorderColor,
                        plugin.directionFillColor,
                        plugin.directionBorderWidth);
            }

        }

        return null;
    }

    //Draw a 3x3 square with a given reference tile as the center
    private void DrawSquare(Graphics2D graphics, WorldPoint referenceTile, Color borderColor, Color fillColor, float borderWidth){
        int boatSize = 3;
        WorldPoint drawTileOffset = referenceTile.dx(-1).dy(-1);
        final LocalPoint lp = LocalPoint.fromWorld(client, drawTileOffset.getX(), drawTileOffset.getY());
        if (lp != null)
        {
            final LocalPoint centerLp = new LocalPoint(
                    lp.getX() + Perspective.LOCAL_TILE_SIZE * (boatSize - 1) / 2,
                    lp.getY() + Perspective.LOCAL_TILE_SIZE * (boatSize - 1) / 2);
            final Polygon poly = Perspective.getCanvasTileAreaPoly(client, centerLp, boatSize);
            renderPoly(graphics, borderColor, fillColor, borderWidth, poly);
        }
    }

    //Renders a multi tile square on the scene
    private void renderPoly(Graphics2D graphics, Color borderColor, Color fillColor, float borderWidth, Shape polygon)
    {
        if (polygon != null)
        {
            graphics.setColor(borderColor);
            graphics.setStroke(new BasicStroke(borderWidth));
            graphics.draw(polygon);
            graphics.setColor(fillColor);
            graphics.fill(polygon);
        }
    }

    //Renders a singular tile on the scene
    private void renderTile(Graphics2D graphics, WorldPoint tile, Color tileBorderColor,Color tileFillColor,float borderWidth)
    {
        LocalPoint lp;
        if((lp = LocalPoint.fromWorld(client, tile)) == null){
            return;
        }

        Polygon poly = Perspective.getCanvasTilePoly(client, lp);
        if (poly != null)
        {
             OverlayUtil.renderPolygon(graphics, poly, tileBorderColor,tileFillColor, new BasicStroke(borderWidth));
        }
    }

}