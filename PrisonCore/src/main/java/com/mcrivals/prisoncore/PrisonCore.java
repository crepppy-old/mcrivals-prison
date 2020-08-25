package com.mcrivals.prisoncore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PrisonCore extends JavaPlugin {
	private String prefix;
	private Connection connection;
	private Set<Function<Packet, Boolean>> packetListeners;

	public Connection getConnection() {
		return connection;
	}

	@Override
	public void onEnable() {
		saveResource("config.yml", false);
		this.prefix = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix"));
		this.packetListeners = new HashSet<>();
		try {
			ConfigurationSection dbConfig = getConfig().getConfigurationSection("database");
			connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s", dbConfig.getString("host"), dbConfig.getString("port")),
					dbConfig.getString("username"), dbConfig.getString("password"));
		} catch (SQLException e) {
			getLogger().severe("Incorrect database credentials");
		}

		Bukkit.getPluginManager().registerEvents(new PacketListener(this),this);
	}

	@Override
	public void onDisable() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addListener(Function<Packet, Boolean> function) {
		packetListeners.add(function);
	}

	public Set<Function<Packet, Boolean>> getPacketListeners() {
		return packetListeners;
	}

	public String getPrefix() {
		return prefix;
	}

	public void sendPrefixedMessage(String message, Player player) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + message));
	}

	public void sendPrefixedMessage(String message, Player player, Object... format) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + String.format(message, format)));
	}
}
