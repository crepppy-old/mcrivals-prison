package com.mcrivals.currency;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;

public class RivalCurrency extends JavaPlugin {

	private PlayerDataManager dataManager;

	@Override
	public void onEnable() {
		saveDefaultConfig();

		dataManager = new PlayerDataManager(this);
		getServer().getPluginManager().registerEvents(dataManager, this);

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
			getConfig().set(path + ".credits", players.getCredits());
			getConfig().set(path + ".gold", players.getGold());
			getConfig().set(path + ".platinum", players.getPlatinum());
			getConfig().set(path + ".energy", players.getEnergy());
			getConfig().set(path + ".tokens", players.getTokens());
			getConfig().set(path + ".gauntlets", players.getGauntlets());
			getConfig().set(path + ".relics", players.getRelics());
		}
		saveConfig();
	}

	private void loadPlayers() {
		for (Player online : getServer().getOnlinePlayers()) {
			dataManager.registerNewPlayer(online.getUniqueId());
		}
	}

	private void registerCommands() {
		getCommand("credits").setExecutor(new CheckCommands());
		getCommand("gold").setExecutor(new CheckCommands());
		getCommand("platinum").setExecutor(new CheckCommands());
		getCommand("energy").setExecutor(new CheckCommands());
		getCommand("tokens").setExecutor(new CheckCommands());
		getCommand("gauntlet").setExecutor(new CheckCommands());
		getCommand("relics").setExecutor(new CheckCommands());
	}

}