package com.harley.mcrivals.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.harley.mcrivals.RivalGlobalQuests;

public class PlayerDataManager implements Listener {

	private RivalGlobalQuests main;
	private static Set<PlayerData> players = new HashSet<>();

	public PlayerDataManager(RivalGlobalQuests main) {
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
				int blocks = cfg.getInt("players." + uuid.toString() + ".blocks-progress");
				int mobs = cfg.getInt("players." + uuid.toString() + ".mobs-progress");
				int fish = cfg.getInt("players." + uuid.toString() + ".fish-progress");

				data.setFishProgress(fish);
				data.setMobsProgress(mobs);
				data.setBlocksProgress(blocks);
			}
		}
	}

	@EventHandler
	public void registerNewPlayer(PlayerJoinEvent e) {
		registerNewPlayer(e.getPlayer().getUniqueId());
	}

}
