package com.example.overlay;

import lombok.Setter;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class HouseOverlay extends Overlay {
    private static final int DOOR_BEFORE = 51999;
    private static final int DOOR_AFTER = 51998;
    private final Client client;

    @Setter
    private Color overlayColor = Color.RED; // Default color, now with Lombok @Setter

    @Inject
    public HouseOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Tile[][] tiles = client.getScene().getTiles()[client.getPlane()];

        for (int x = 0; x < Constants.SCENE_SIZE; x++) {
            for (int y = 0; y < Constants.SCENE_SIZE; y++) {
                Tile tile = tiles[x][y];
                if (tile == null) {
                    continue;
                }

                WallObject wallObject = tile.getWallObject();
                if (wallObject != null) {
                    int id = wallObject.getId();
                    if (id == DOOR_BEFORE || id == DOOR_AFTER) {
                        drawWallObjectBox(graphics, wallObject, id == DOOR_BEFORE ? Color.red : Color.green);
                    }
                }
            }
        }
        return null;
    }



    private void drawWallObjectBox(Graphics2D graphics, WallObject wallObject, Color color) {
        Shape polygon = wallObject.getCanvasTilePoly();
        if (polygon != null) {
            // Set the color for the outline of the box
            graphics.setColor(color);

            // Set the stroke to define the thickness of the outline
            graphics.setStroke(new BasicStroke(2));

            // Draw the outline of the polygon
            graphics.draw(polygon);

            // Create a semi-transparent version of the color for the fill
            Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50); // 50 is the alpha value

            // Set the color for the fill
            graphics.setColor(fillColor);

            // Fill the polygon with the semi-transparent color
            graphics.fill(polygon);
        }
    }

}
