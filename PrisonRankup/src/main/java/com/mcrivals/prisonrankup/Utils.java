package com.mcrivals.prisonrankup;

import org.bukkit.Bukkit;

import java.util.*;

public class Utils {
	public static void runCommandList(List<String> commands, PlayerData player) {
		commands.forEach(command -> {
			if (command.contains("%player%")) {
				// If the player is referenced assume to be console command
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getPlayer().getName()));
			} else {
				if(command.startsWith("thelp") && !player.isTutorialsEnabled()) return;
				player.getPlayer().performCommand(command);
			}
		});
	}

	public static Object weightedChoice(NavigableMap<Double, Object> map) {
		double count = map.keySet().stream().mapToDouble(i -> i).sum();
		return map.floorKey(new Random().nextDouble() * count);
	}
}
