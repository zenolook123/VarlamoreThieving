package com.varlamorethieving;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class VarlamoreThieving
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(VarlamoreThievingPlugin.class);
		RuneLite.main(args);
	}
}
