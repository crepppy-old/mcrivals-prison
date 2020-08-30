package com.mcrivals.prisonrankup.listeners;

import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import com.mcrivals.prisonrankup.Utils;
import com.mcrivals.prisonrankup.events.MineBlockBreakEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.TreeMap;
import java.util.stream.Collector;

public class MineBreakListener implements Listener {
	private final PrisonRankup plugin;

	public MineBreakListener(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void mineBlockBreak(BlockBreakEvent e) {
		if (e instanceof MineBlockBreakEvent) return;
		PlayerData player = plugin.getPlayerManager().getPlayer(e.getPlayer());
		for (Mine m : plugin.getMines()) {
			if (m.isInMine(e.getBlock().getLocation())) {
				Bukkit.getPluginManager().callEvent(new MineBlockBreakEvent(e.getBlock(), player, m));
			}
		}
	}

	@EventHandler
	public void onLuckyBlockBreak(MineBlockBreakEvent e) {
		if (e.getBlock().getType() != Material.SPONGE) return;
		TreeMap<Double, Object> commandsTreeMap = plugin.getConfig().getStringList("lucky-block-commands").stream()
				.map(x -> x.split(":", 2))
				.collect(Collector.of(TreeMap::new, (map, el) -> map.put(Double.parseDouble(el[0]), el[1]), (map1, map2) -> {
					map1.putAll(map2);
					return map1;
				}));
		String command = (String) Utils.weightedChoice(commandsTreeMap);
		if (command.contains("%player%")) {
			// If the player is referenced assume to be console command
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", e.getPlayer().getName()));
		} else {
			e.getPlayer().performCommand(command);
		}
	}
}
