package com.mcrivals.prisoncore;

import org.bukkit.ChatColor;

public class Messages {
	public static final String NOT_PLAYER;
	public static final String NO_PERMISSION;

	static {
		NOT_PLAYER = colour("&cYou must be a player to use this command");
		NO_PERMISSION = colour("&cYou don't have permission to run this command");
	}

	private static String colour(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
