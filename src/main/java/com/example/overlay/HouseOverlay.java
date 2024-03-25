package com.example.overlay;

import lombok.Setter;
import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

public class HouseOverlay extends Overlay {
    private static final int DOOR_BEFORE_ID = 51999;
    private static final int DOOR_AFTER_ID = 51998;

    private static final int WINDOW_ID_IN = 52000;

    private static final int WINDOW_ID_OUT = 52001;

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
                    ObjectComposition comp = client.getObjectDefinition(wallObject.getId());

                    // Check if the object has morph ids
                    if (comp.getImpostorIds() != null) {
                        ObjectComposition morphedComp = comp.getImpostor();
                        if (morphedComp != null) {
                            int morphId = morphedComp.getId();

                            // Now you can check against the morphId
                            if (morphId == WINDOW_ID_IN || morphId == WINDOW_ID_OUT) {
                                drawWallObjectBox(graphics, wallObject, morphId == WINDOW_ID_OUT ? Color.yellow : Color.BLUE); // Highlight morphed window
                            }
                        }
                    }
                    int id = wallObject.getId();
                    if (id == DOOR_BEFORE_ID || id == DOOR_AFTER_ID) {
                        drawWallObjectBox(graphics, wallObject, id == DOOR_BEFORE_ID ? Color.red : Color.green);
                    }
                }
            }
        }
        return null;
    }


    public void highlightWindow(){

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
