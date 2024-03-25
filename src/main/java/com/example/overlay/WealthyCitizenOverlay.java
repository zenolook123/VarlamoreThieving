package com.example.overlay;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.time.*;
import java.util.*;


import javax.inject.Inject;
import java.awt.*;

public class WealthyCitizenOverlay extends Overlay {
    private final Client client;
    private Map<NPC, Instant> npcTransitionTimestamps = new HashMap<>();

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
        trackedNpcs.removeIf(npc -> !isNpcInView(npc));
    }

    private boolean isNpcInView(NPC npc) {
        // Add your logic here to determine if the NPC is in view
        // This could be distance based, or checking if the NPC is in the client's viewport
        // For example:
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        LocalPoint npcLocation = npc.getLocalLocation();
        int distance = playerLocation.distanceTo(npcLocation);
        return distance <= 20 * Perspective.LOCAL_TILE_SIZE;
    }

    // In your render method or where you're actually drawing the tiles
    @Override
    public Dimension render(Graphics2D graphics) {
        for (Iterator<NPC> it = trackedNpcs.iterator(); it.hasNext(); ) {
            NPC npc = it.next();
            if (!isNpcInView(npc)) {
                it.remove(); // Remove the NPC if it's no longer in view
                continue;
            }
            renderNpcOverlay(graphics, npc); // Continue with rendering logic
        }
        return null;
    }




    private void renderNpcOverlay(Graphics2D graphics, NPC npc) {
        Color color = Color.red; // Default color
        boolean isInteracting = npc.isInteracting();
        Instant now = Instant.now();

        // Calculate the distance between the NPC and the player
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
        LocalPoint npcLocation = npc.getLocalLocation();
        int distance = playerLocation.distanceTo(npcLocation);

        // Check if the NPC is within a certain range
        if (distance > 20 * Perspective.LOCAL_TILE_SIZE) {
            // NPC is too far away, skip rendering its overlay
            return;
        }

        // Proceed with the color change logic only if the NPC is within range
        if (isInteracting && !npcTransitionTimestamps.containsKey(npc)) {
            // NPC has just transitioned out of the idle state, record the current time
            npcTransitionTimestamps.put(npc, now);
        }

        if (npcTransitionTimestamps.containsKey(npc)) {
            Instant transitionTime = npcTransitionTimestamps.get(npc);
            if (Duration.between(transitionTime, now).getSeconds() <= 22) {
                // If it's been 15 seconds or less since the transition, use a different color
                color = Color.green;
            } else {
                // More than 15 seconds have passed since the transition, remove the timestamp
                npcTransitionTimestamps.remove(npc);
                return; // Skip drawing the overlay for this NPC
            }
        }

        // Render the overlay with the determined color
        Polygon poly = Perspective.getCanvasTilePoly(client, npcLocation);
        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
    }

}




