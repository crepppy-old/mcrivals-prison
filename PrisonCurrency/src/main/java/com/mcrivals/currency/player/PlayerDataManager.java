package com.mcrivals.currency.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mcrivals.currency.RivalCurrency;

public class PlayerDataManager implements Listener {

	private RivalCurrency main;
	private static Set<PlayerData> players = new HashSet<>();

	public PlayerDataManager(RivalCurrency main) {
		this.main = main;
	}

	public static Set<PlayerData> getPlayers() {
		return players;
	}

	public static PlayerData getByUuid(UUID uuid) {
		for (PlayerData all : players) {
			if (all.getUuid().equals(uuid)) {
				return all;
			}
		}
		return null;
	}

	public void registerNewPlayer(UUID uuid) {
		FileConfiguration cfg = main.getConfig();
		if (getByUuid(uuid) == null) {
			PlayerData data = new PlayerData(uuid);
			if (cfg.contains("players." + uuid.toString())) {
				long credits = cfg.getLong("players." + uuid.toString() + ".credits");
				long gold = cfg.getLong("players." + uuid.toString() + ".gold");
				long platinum = cfg.getLong("players." + uuid.toString() + ".platinum");
				long energy = cfg.getLong("players." + uuid.toString() + ".energy");
				long tokens = cfg.getLong("players." + uuid.toString() + ".tokens");
				long gauntlets = cfg.getLong("players." + uuid.toString() + ".gauntlet");
				long relics = cfg.getLong("players." + uuid.toString() + ".relics");

				data.setCredits(credits);
				data.setEnergy(energy);
				data.setGauntlets(gauntlets);
				data.setGold(gold);
				data.setPlatinum(platinum);
				data.setRelics(relics);
				data.setTokens(tokens);
			}
		}
	}

	@EventHandler
	public void registerNewPlayer(PlayerJoinEvent e) {
		registerNewPlayer(e.getPlayer().getUniqueId());
	}

}
