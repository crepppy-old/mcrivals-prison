package com.mcrivals.prisonrankup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileManager {
	private final PrisonRankup plugin;

	public FileManager(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	public void loadMines() {
		File file = new File(plugin.getDataFolder(), "mines.yml");
		if (file.exists()) {
			FileConfiguration minesConfig = YamlConfiguration.loadConfiguration(file);
			for (String key : minesConfig.getConfigurationSection("mines").getKeys(false)) {
				// Mines file
				//   min: 15 15 15
				//   max: 20 20 20
				//   world: world
				//   cost: 10000
				//   blocks:
				//     - GOLD_ORE 95
				//     - STAINED_CLAY:4 5
				ConfigurationSection section = minesConfig.getConfigurationSection("mines." + key);
				World world = Bukkit.getWorld(section.getString("world"));
				String[] minSplit = section.getString("min").split(" ");
				String[] maxSplit = section.getString("min").split(" ");
				Location minLocation = new Location(world, Integer.parseInt(minSplit[0]), Integer.parseInt(minSplit[1]), Integer.parseInt(minSplit[2]));
				Location maxLocation = new Location(world, Integer.parseInt(maxSplit[0]), Integer.parseInt(maxSplit[1]), Integer.parseInt(maxSplit[2]));
				float cost = Float.parseFloat(section.getString("cost"));
				Map<Mine.BlockType, Integer> blocks = new HashMap<>();
				for (String b : section.getStringList("blocks")) {
					String[] split = b.split(" ");
					blocks.put(Mine.BlockType.fromString(split[0]), Integer.parseInt(split[1]));
				}
				plugin.addMine(new Mine(key, cost, minLocation, maxLocation, blocks, section.getStringList("commands")));
			}
		}
	}

	public void saveMines() {
		File file = new File(plugin.getDataFolder(), "mines.yml");
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			Bukkit.getLogger().severe("Error: Could not save mines.yml for " + plugin.getName());
			return;
		}
		FileConfiguration mineConfig = YamlConfiguration.loadConfiguration(file);
		Function<Location, String> translateLocation = (location) -> location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
		for (Mine m : plugin.getMines()) {
			String root = "mines." + m.getName() + ".";
			mineConfig.set(root + "min", translateLocation.apply(m.getMinimumPoint()));
			mineConfig.set(root + "max", translateLocation.apply(m.getMaximumPoint()));
			mineConfig.set(root + "world", m.getMaximumPoint().getWorld().getName());
			mineConfig.set(root + "cost", m.getCost());
			mineConfig.set(root + "blocks", m.getBlockPercentages().entrySet().stream().map(entry -> entry.getKey().toString() + " " + entry.getValue()).collect(Collectors.toList()));
			mineConfig.set(root + "commands", m.getCommands());
		}
		try {
			mineConfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadTutorials() {
		File file = new File(plugin.getDataFolder(), "tutorials.yml");
		if (file.exists()) {
			FileConfiguration tutorialsConfig = YamlConfiguration.loadConfiguration(file);
			for (String tutorial : tutorialsConfig.getConfigurationSection("tutorials").getKeys(false)) {
				ConfigurationSection section = tutorialsConfig.getConfigurationSection("tutorials." + tutorial);
				Location location = null;
				if (section.contains("location")) {
					String[] locationSplit = section.getString("location").split(" ");
					location = new Location(
							Bukkit.getWorld(locationSplit[0]),
							Integer.parseInt(locationSplit[1]),
							Integer.parseInt(locationSplit[2]),
							Integer.parseInt(locationSplit[3]));
				}
				plugin.getTutorials().add(new Tutorial(
						tutorial,
						location,
						section.getStringList("messages")
								.stream()
								.map(x -> ChatColor.translateAlternateColorCodes('&', x))
								.collect(Collectors.toList()),
						section.getInt("delay"),
						section.getString("placeholder")));
			}
		}
	}
}
