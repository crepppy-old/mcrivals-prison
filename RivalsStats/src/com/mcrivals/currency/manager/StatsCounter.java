package com.mcrivals.currency.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;
import com.mcrivals.stats.RivalStats;

public class StatsCounter implements Listener {

	private RivalStats main;

	public StatsCounter(RivalStats main) {
		this.main = main;
	}

	@EventHandler
	public void onKill(PlayerDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		PlayerData killerData = PlayerDataManager.getByUuid(killer.getUniqueId());
		killerData.setKills(killerData.getKills() + 1);

		Player victim = e.getEntity();
		PlayerData victimData = PlayerDataManager.getByUuid(victim.getUniqueId());
		victimData.setDeaths(victimData.getDeaths() + 1);
	}

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if (e.getCaught() != null) {
			Player player = e.getPlayer();
			PlayerData data = PlayerDataManager.getByUuid(player.getUniqueId());
			data.setFishCaught(data.getFishCaught() + 1);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		PlayerData data = PlayerDataManager.getByUuid(player.getUniqueId());
		data.setBlocksBroken(data.getBlocksBroken() + 1);
	}

	public void schedulePlayTime() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
			for (PlayerData players : PlayerDataManager.getPlayers()) {
				Player player = Bukkit.getPlayer(players.getUuid());
				if (player.isOnline()) {
					players.setPlayTimeMins(players.getPlayTimeMins() + 1);
				}
			}
		}, 0, 20 * 60);
	}

}
