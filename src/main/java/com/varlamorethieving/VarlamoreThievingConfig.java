package com.varlamorethieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("varlamorethieving")
public interface VarlamoreThievingConfig extends Config
{
	@ConfigItem(
			keyName = "enableNotifications",
			name = "Enable Notifications",
			description = "Configure whether or not notifications should be shown."
	)
	default boolean enableNotifications()
	{
		return true;
	}
	@ConfigItem(
			keyName = "enableChatMessages",
			name = "Enable Chat Messages",
			description = "Configure whether or not chat messages should be shown."
	)
	default boolean enableChatMessages()
	{
		return true; // By default, chat messages are enabled.
	}
}
