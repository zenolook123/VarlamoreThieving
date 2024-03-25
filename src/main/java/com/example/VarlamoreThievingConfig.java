package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface VarlamoreThievingConfig extends Config
{
	@ConfigItem(
		keyName = "notification",
		name = "Notification Settings",
		description = "Enable or Disable Notifications"
	)
	default String greeting()
	{
		return "Hello";
	}
}
