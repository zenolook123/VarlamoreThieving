package com.varlamorethieving;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import com.google.inject.Provides;
import com.varlamorethieving.overlay.HouseOverlay;
import com.varlamorethieving.overlay.WealthyCitizenOverlay;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = "Varlamore Thieving"
)
public class VarlamoreThievingPlugin extends Plugin {
	private static final String WEALTHY_CITIZEN_NAME = "Wealthy citizen";

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WealthyCitizenOverlay wealthyCitizenOverlay;

	@Inject
	private HouseOverlay houseOverlay;

	@Inject
	private ScheduledExecutorService executorService;

	@Inject
	private VarlamoreThievingConfig config;




    @Override
	protected void startUp() throws Exception {
		VarlamoreThievingPlugin.log.info("Varlamore Thieving started!");
		overlayManager.add(wealthyCitizenOverlay);
		overlayManager.add(houseOverlay);
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(this::refreshNpcTracking, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void shutDown() throws Exception {
		VarlamoreThievingPlugin.log.info("Varlamore Thieving stopped!");
		overlayManager.remove(wealthyCitizenOverlay);
		overlayManager.remove(houseOverlay);
		executorService.shutdownNow();
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		String message = chatMessage.getMessage();
		if (config.enableNotifications()) {
			// Example: Check if the chat message contains a specific phrase
			if (message.contains("You hear someone outside spot the homeowner coming back!")) {
				notifier.notify("Homeowner spotted!");
			}

			if (message.contains("You notice an urchin distract a wealthy citizen nearby.")) {
				notifier.notify("Citizen Distracted!");
			}

			if (message.contains("You can't spot anything else worth taking from the jewellery case.")) {
				notifier.notify("Looting Stopped!");
			}
			if (message.contains("You can't spot anything else worth taking from the chest.")) {
				notifier.notify("Looting Stopped!");
			}
			if (message.contains("You can't spot anything else worth taking from the wardrobe.")) {
				notifier.notify("Looting Stopped!");
			}

		}
	if (config.enableChatMessages()) {
		if (message.contains("You feel very tired and can't quite grab as many valuables as you'd like.")) {
			String adviceMessage = "If you've used more than 5 keys without obtaining a new one, your thieving will be less successful. " +
					"Pickpocketing another House key from Wealthy citizens will resolve the issue.";

			// Inserting a custom message into the game's chatbox
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", adviceMessage, null);
		}
	}

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
