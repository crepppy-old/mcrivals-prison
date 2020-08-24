package com.mcrivals.prisonrankup.listeners;

import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import com.mcrivals.prisonrankup.events.MineBlockBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MineBreakListener implements Listener {
	private final PrisonRankup plugin;

	public MineBreakListener(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void mineBlockBreak(BlockBreakEvent e) {
		PlayerData player = plugin.getPlayerManager().getPlayer(e.getPlayer());
		for (Mine m : plugin.getMines()) {
			if (m.isInMine(e.getBlock().getLocation())) {
				Bukkit.getPluginManager().callEvent(new MineBlockBreakEvent(e.getBlock(), player, m));
			}
		}
	}
}
