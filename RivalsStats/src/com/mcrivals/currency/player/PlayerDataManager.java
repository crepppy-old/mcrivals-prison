package com.mcrivals.currency.player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mcrivals.stats.RivalStats;

public class PlayerDataManager implements Listener {

	private RivalStats main;
	private static Set<PlayerData> players = new HashSet<>();

	public PlayerDataManager(RivalStats main) {
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
				data.setBlocksBroken(cfg.getInt("players." + uuid.toString() + ".blocks-broken"));
				data.setDeaths(cfg.getInt("players." + uuid.toString() + ".deaths"));
				data.setFishCaught(cfg.getInt("players." + uuid.toString() + ".fish-caught"));
				data.setKills(cfg.getInt("players." + uuid.toString() + ".kills"));
				data.setPlayTimeMins(cfg.getInt("players." + uuid.toString() + ".playtime-mins"));

			}
		}
	}

	@EventHandler
	public void registerNewPlayer(PlayerJoinEvent e) {
		registerNewPlayer(e.getPlayer().getUniqueId());
	}

}
