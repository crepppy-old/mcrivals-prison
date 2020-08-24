package com.mcrivals.prisonrankup;

import com.mcrivals.prisoncore.PrisonCore;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class PlayerManager {
	private final PrisonRankup plugin;
	private final Set<PlayerData> players;
	private final PrisonCore core;

	public PlayerManager(PrisonRankup plugin) {
		this.plugin = plugin;
		this.core = plugin.getPrisonCore();
		this.players = new HashSet<>();
	}

	public void loadPlayers() {
		// todo sql stuffs
	}

	public PlayerData getPlayer(Player player) {
		Optional<PlayerData> pd = players.stream().filter(x -> player.getUniqueId().equals(x.getPlayerUUID())).findAny();
		if (pd.isPresent()) {
			return pd.get();
		} else {
			PlayerData playerData = new PlayerData(player.getUniqueId(), true, false, plugin.getMines().get(0), 0, 1);
			//todo save to sql
			return playerData;
		}
	}

	/**
	 * @param player The player to rankup
	 * @return <code>true</code> if the player successfully ranked up
	 */
	public boolean rankup(PlayerData player) {
		if (player.getMine().getName().equalsIgnoreCase("Z")) return false;
		int index = plugin.getMines().indexOf(player.getMine());
		player.setMine(plugin.getMines().get(index + 1));
		Arrays.stream(plugin.getPermissions().getGroups())
				.filter(x -> x.equalsIgnoreCase(plugin.getConfig().getString("group-prefix") + player.getMine().getName()))
				.findAny()
				.ifPresent(group -> plugin.getPermissions().playerAddGroup(player.getPlayer(), group));
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
		player.setPrestige(player.getPrestige() + 1);
		return true;
	}

	public Set<PlayerData> getPlayers() {
		return players;
	}
}

