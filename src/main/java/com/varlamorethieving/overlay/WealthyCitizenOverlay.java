package com.varlamorethieving.overlay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class WealthyCitizenOverlay extends Overlay {
    private final Client client;
    private final Map<NPC, Instant> npcTransitionTimestamps = new HashMap<>();

    private final Set<NPC> trackedNpcs = new HashSet<>();

    @Inject
    private WealthyCitizenOverlay(Client client) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
    }

    public void addNpc(NPC npc) {
        trackedNpcs.add(npc);
    }

    public void removeNpc(NPC npc) {
        trackedNpcs.remove(npc);
    }


    @Subscribe
    public void onNpcDespawned(NpcDespawned npcDespawned) {
        trackedNpcs.remove(npcDespawned.getNpc());
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Check for NPCs that are out of view and remove them
        trackedNpcs.removeIf(this::isNpcInView);

    }

    private boolean isNpcInView(NPC npc) {
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        LocalPoint npcLocation = npc.getLocalLocation();
        int distance = playerLocation.distanceTo(npcLocation);
        return distance > 10 * Perspective.LOCAL_TILE_SIZE;
    }

    // In your render method or where you're actually drawing the tiles
    @Override
    public Dimension render(Graphics2D graphics) {
        List<NPC> toRemove = new ArrayList<>(); // Create a list to hold NPCs that need to be removed

        for (NPC npc : trackedNpcs) { // Use enhanced for loop for readability
            if (isNpcInView(npc)) {
                toRemove.add(npc); // Add the NPC to the removal list instead of removing it directly
            } else {
                renderNpcOverlay(graphics, npc); // Continue with rendering logic
            }
        }

        trackedNpcs.removeAll(toRemove); // Remove all NPCs that are no longer in view after the iteration

        return null;
    }




    private void renderNpcOverlay(Graphics2D graphics, NPC npc) {
        Color color;
        String text;
        boolean recentlyPunched = false;

        if (npc.getAnimation() == 422) {
            color = Color.YELLOW;
            text = "Guarded";
            recentlyPunched = true;
            npcTransitionTimestamps.put(npc, Instant.now()); // Record the punch time
        } else if (npcTransitionTimestamps.containsKey(npc)) {
            long secondsSincePunch = Duration.between(npcTransitionTimestamps.get(npc), Instant.now()).getSeconds();
            if (secondsSincePunch <= 5) {
                color = Color.YELLOW; // Maintain the yellow color for 5 seconds
                text = "Guarded";
                recentlyPunched = true;
            } else {
                npcTransitionTimestamps.remove(npc); // Remove after 5 seconds
                color = npc.isInteracting() ? Color.GREEN : Color.RED;
                text = npc.isInteracting() ? "Distracted" : "Aware";
            }
        } else {
            color = npc.isInteracting() ? Color.GREEN : Color.RED;
            text = npc.isInteracting() ? "Distracted" : "Aware";
        }

        // Calculate the distance between the NPC and the player
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        LocalPoint npcLocation = npc.getLocalLocation();
        int distance = playerLocation.distanceTo(npcLocation);

        // Check if the NPC is within a certain range
        if (distance > 15 * Perspective.LOCAL_TILE_SIZE) {
            // NPC is too far away, skip rendering its overlay
            return;
        }

        // Render the overlay with the determined color
        Polygon poly = Perspective.getCanvasTilePoly(client, npcLocation);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
            drawText(graphics, text, npcLocation, Color.WHITE);
    }

    private void drawText(Graphics2D graphics, String text, LocalPoint location, Color textColor) {
        graphics.setFont(new Font("Arial", Font.BOLD, 9));
        Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, location, text, 0);
        if (canvasTextLocation != null) {
            // Draw text shadow for better visibility
            FontMetrics metrics = graphics.getFontMetrics();
            int x = canvasTextLocation.getX() - metrics.stringWidth(text) / 2;
            int y = canvasTextLocation.getY() + metrics.getHeight() / 2;

            // Draw text shadow
            graphics.setColor(Color.BLACK);
            graphics.drawString(text, x + 1, y + 1); // Shadow

            // Draw text
            graphics.setColor(textColor);
            graphics.drawString(text, x, y); // Text
        }
    }

    }
