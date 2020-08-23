package com.mcrivals.prisoncore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonCore extends JavaPlugin {
	private String prefix;

	@Override
	public void onEnable() {
		saveResource("config.yml", false);

		this.prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"));
	}

	public String getPrefix() {
		return prefix;
	}

	public void sendPrefixedMessage(String message, Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}

	public void sendPrefixedMessage(String message, Player player, String... format) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + String.format(message, format)));
	}
}
