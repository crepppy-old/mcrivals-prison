package com.mcrivals.stats;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcrivals.currency.leaderboard.GetTop;
import com.mcrivals.currency.leaderboard.StatCategoryInventory;
import com.mcrivals.currency.leaderboard.TopPlayerInventory;
import com.mcrivals.currency.manager.StatsCounter;
import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;

public class RivalStats extends JavaPlugin {

	private PlayerDataManager dataManager;
	private StatsCounter statsCounter;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		this.statsCounter = new StatsCounter(this);
		dataManager = new PlayerDataManager(this);
		getServer().getPluginManager().registerEvents(dataManager, this);
		getServer().getPluginManager().registerEvents(statsCounter, this);
		getServer().getPluginManager().registerEvents(new StatCategoryInventory(), this);
		getServer().getPluginManager().registerEvents(new TopPlayerInventory(), this);

		statsCounter.schedulePlayTime();
		registerCommands();
		loadPlayers();
	}

	@Override
	public void onDisable() {
		unloadPlayers();
	}

	private void unloadPlayers() {
		for (PlayerData players : PlayerDataManager.getPlayers()) {
			String path = "players." + players.getUuid().toString();
			getConfig().set(path + ".blocks-broken", players.getBlocksBroken());
			getConfig().set(path + ".deaths", players.getDeaths());
			getConfig().set(path + ".fish-caught", players.getFishCaught());
			getConfig().set(path + ".kills", players.getKills());
			getConfig().set(path + ".playtime-mins", players.getPlayTimeMins());
		}
		saveConfig();
	}

	private void loadPlayers() {
		for (Player online : getServer().getOnlinePlayers()) {
			dataManager.registerNewPlayer(online.getUniqueId());
		}
		for (String uuids : getConfig().getConfigurationSection("players").getKeys(false)) {
			UUID uuid = UUID.fromString(uuids);
			dataManager.registerNewPlayer(uuid);
		}
	}

	private void registerCommands() {
		getCommand("leaderboard").setExecutor(new GetTop());
	}
}