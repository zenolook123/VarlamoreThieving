package com.example;
import com.example.overlay.WealthyCitizenOverlay;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@PluginDescriptor(
		name = "Varlamore Thieving"
)
public class VarlamoreThievingPlugin extends Plugin {
	private static final String WEALTHY_CITIZEN_NAME = "Wealthy citizen";

	@Inject
	private Client client;

	@Inject
	private VarlamoreThievingConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WealthyCitizenOverlay wealthyCitizenOverlay;

	@Inject
	private ScheduledExecutorService executorService;

	@Override
	protected void startUp() throws Exception {
		VarlamoreThievingPlugin.log.info("Varlamore Thieving started!");
		overlayManager.add(wealthyCitizenOverlay);
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this::refreshNpcTracking, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void shutDown() throws Exception {
		VarlamoreThievingPlugin.log.info("Varlamore Thieving stopped!");
		overlayManager.remove(wealthyCitizenOverlay);
		executorService.shutdownNow();
	}


	private void refreshNpcTracking() {
		for (NPC npc : client.getNpcs()) {
			if (npc.getName() != null && npc.getName().contains(WEALTHY_CITIZEN_NAME)) {
				wealthyCitizenOverlay.addNpc(npc);
			} else {
				wealthyCitizenOverlay.removeNpc(npc);
			}
		}
	}

	@Provides
	VarlamoreThievingConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(VarlamoreThievingConfig.class);
	}
}
