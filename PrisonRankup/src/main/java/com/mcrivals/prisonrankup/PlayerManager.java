package com.mcrivals.prisonrankup;

import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.events.RankupEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
	private final PrisonRankup plugin;
	private final HashMap<UUID, PlayerData> players;
	private final HashMap<Player, Tutorial> inTutorial;
	private final PrisonCore core;

	public PlayerManager(PrisonRankup plugin) {
		this.plugin = plugin;
		this.core = plugin.getPrisonCore();
		this.players = new HashMap<>();
		this.inTutorial = new HashMap<>();
	}

	public HashMap<Player, Tutorial> getInTutorial() {
		return inTutorial;
	}

	public void loadPlayers() {
		// todo sql stuffs
	}

	public PlayerData getPlayer(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			return players.get(player.getUniqueId());
		} else {
			PlayerData data = new PlayerData(player.getUniqueId(), true, false, plugin.getMines().get(0), 0, 1);
			players.put(player.getUniqueId(), data);
			return data;
		}
	}

	/**
	 * @param player The player to rankup
	 * @return <code>true</code> if the player successfully ranked up
	 */
	public boolean rankup(PlayerData player) {
		int index = plugin.getMines().indexOf(player.getMine());
		if (player.getMine().getName().equalsIgnoreCase("Z") && plugin.getMines().size() == index + 1) return false;
		Mine toMine = plugin.getMines().get(index + 1);
		RankupEvent event = new RankupEvent(player, toMine, 0);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		player.setMine(plugin.getMines().get(index + 1));
		player.setResourceMultiplier((float) (player.getResourceMultiplier() + plugin.getConfig().getDouble("resource-multiplier-increment")));
		Arrays.stream(plugin.getPermissions().getGroups())
				.filter(x -> x.equalsIgnoreCase(plugin.getConfig().getString("group-prefix") + player.getMine().getName()))
				.findAny()
				.ifPresent(group -> plugin.getPermissions().playerAddGroup(player.getPlayer(), group));
		toMine.getCommands().forEach(command -> {
			if (command.contains("%player%")) {
				// If the player is referenced assume to be console command
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getPlayer().getName()));
			} else {
				player.getPlayer().performCommand(command);
			}
		});
		//todo take money
		return true;
	}

	/**
	 * @param player The player to prestige
	 * @return <code>true</code> if the player successfully prestige
	 */
	public boolean prestige(PlayerData player) {
		if (!player.getMine().getName().equalsIgnoreCase("Z")) return false;
		if (player.getPrestige() == 30) return false;
		RankupEvent event = new RankupEvent(player, null, player.getPrestige() + 1);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		player.setPrestige(player.getPrestige() + 1);
		return true;
	}

	public void startTutorial(Player player, Tutorial tutorial) {
		inTutorial.put(player, tutorial);
		boolean flying = player.isFlying();
		Location location = player.getLocation();
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			inTutorial.remove(player);
			player.teleport(location);
			if (!flying && player.isFlying()) player.setFlying(false);
		}, tutorial.getDelay() * 20L);

		if (tutorial.getLocation() != null) {
			player.teleport(tutorial.getLocation());
			player.setFlying(true);
		}

		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.5F);
		for (String s : tutorial.getMessages()) {
			player.sendMessage(s);
		}
		//todo highlight scoreboard
	}

	public Collection<PlayerData> getPlayers() {
		return players.values();
	}
}

