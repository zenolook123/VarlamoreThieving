package com.varlamorethieving.overlay;

import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.Point;
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
                                drawHouseBox(graphics, wallObject, morphId == WINDOW_ID_OUT ? Color.yellow : Color.BLUE); // Highlight morphed window
                            }
                        }
                    }
                    int id = wallObject.getId();
                    if (id == DOOR_BEFORE_ID || id == DOOR_AFTER_ID) {
                        drawHouseBox(graphics, wallObject, id == DOOR_BEFORE_ID ? Color.red : Color.green);
                    }
                }
            }
        }
        return null;
    }


    private void drawHouseBox(Graphics2D graphics, WallObject wallObject, Color color) {
        Shape polygon = wallObject.getCanvasTilePoly();
        if (polygon != null) {
            // Drawing the box with the specified color
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 50); // Semi-transparent fill
            graphics.setColor(fillColor);
            graphics.fill(polygon);

            // Drawing text for red and green colors only
            if (color.equals(Color.red) || color.equals(Color.green)) {
                String text = color.equals(Color.green) ? "Away" : "Home";

                graphics.setFont(new Font("Arial", Font.BOLD, 9)); // Set the desired font
                FontMetrics metrics = graphics.getFontMetrics();
                int textWidth = metrics.stringWidth(text);
                int textHeight = metrics.getHeight();

                // Center the text on the polygon
                Point textLocation = Perspective.getCanvasTextLocation(client, graphics, wallObject.getLocalLocation(), text, 0);
                if (textLocation != null) {
                    int x = textLocation.getX() - (textWidth / 2);
                    int y = textLocation.getY() + (textHeight / 2);

                    // Draw text shadow for better visibility
                    graphics.setColor(Color.BLACK);
                    graphics.drawString(text, x + 1, y + 1); // Shadow
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(text, x, y); // Actual text
                }
            }

            if (color.equals(Color.yellow) || color.equals(Color.blue)) {
                String text = "Window";

                graphics.setFont(new Font("Arial", Font.BOLD, 9)); // Set the desired font
                FontMetrics metrics = graphics.getFontMetrics();
                int textWidth = metrics.stringWidth(text);
                int textHeight = metrics.getHeight();

                // Center the text on the polygon
                Point textLocation = Perspective.getCanvasTextLocation(client, graphics, wallObject.getLocalLocation(), text, 0);
                if (textLocation != null) {
                    int x = textLocation.getX() - (textWidth / 2);
                    int y = textLocation.getY() + (textHeight / 2);

                    // Draw text shadow for better visibility
                    graphics.setColor(Color.BLACK);
                    graphics.drawString(text, x + 1, y + 1); // Shadow
                    graphics.setColor(Color.WHITE);
                    graphics.drawString(text, x, y); // Actual text
                }
            }
        }
    }
}
