package com.harley.mcrivals.progress;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;

import com.harley.mcrivals.player.PlayerData;
import com.harley.mcrivals.player.PlayerDataManager;

public class StatTracker implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		PlayerData data = PlayerDataManager.getByUuid(player.getUniqueId());
		data.setBlocksProgress(data.getBlocksProgress() + 1);
	}

	@EventHandler
	public void onFish(PlayerFishEvent e) {
		if (e.getCaught() != null) {
			Player player = e.getPlayer();
			PlayerData data = PlayerDataManager.getByUuid(player.getUniqueId());
			data.setFishProgress(data.getFishProgress() + 1);
		}
	}

	@EventHandler
	public void onMobDeath(EntityDeathEvent e) {
		Player killer = e.getEntity().getKiller();
		if (e.getEntity().getKiller() != null) {
			PlayerData data = PlayerDataManager.getByUuid(killer.getUniqueId());
			data.setMobsProgress(data.getMobsProgress() + 1);
		}
	}
}
