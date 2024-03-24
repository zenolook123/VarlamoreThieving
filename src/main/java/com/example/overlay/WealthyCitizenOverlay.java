package com.example.overlay;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;


import javax.inject.Inject;
import java.awt.*;

public class WealthyCitizenOverlay extends Overlay {
    private final Client client;
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


    @Override
    public Dimension render(Graphics2D graphics) {
        // Get the player's location for distance comparison
        LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();

        Iterator<NPC> iterator = trackedNpcs.iterator();
        while (iterator.hasNext()) {
            NPC npc = iterator.next();
            LocalPoint npcLocation = npc.getLocalLocation();

            // Calculate the distance between the NPC and the player
            int distance = playerLocation.distanceTo(npcLocation);

            // Check the distance and render or remove the NPC accordingly
            if (distance <= 15 * Perspective.LOCAL_TILE_SIZE) {
                // NPC is within range, render the overlay
                renderNpcOverlay(graphics, npc, Color.red);
            } else {
                // NPC is out of range, remove it from the set of tracked NPCs
                iterator.remove();
            }
        }
        return null;
    }


    private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color) {
        LocalPoint lp = actor.getLocalLocation();
        Polygon poly = Perspective.getCanvasTilePoly(client, lp);

        if (poly != null) {
            OverlayUtil.renderPolygon(graphics, poly, color);
        }
    }
}
