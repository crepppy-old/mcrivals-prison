package com.harley.mcrivals;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.harley.mcrivals.player.PlayerData;
import com.harley.mcrivals.player.PlayerDataManager;
import com.harley.mcrivals.progress.StatTracker;
import com.mcrivals.currency.leaderboard.GetTop;
import com.mcrivals.currency.leaderboard.StatCategoryInventory;
import com.mcrivals.currency.leaderboard.TopPlayerInventory;

public class RivalGlobalQuests extends JavaPlugin {

	private PlayerDataManager playerManager;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		scheduleQuestReset();

		playerManager = new PlayerDataManager(this);
		getServer().getPluginManager().registerEvents(new StatTracker(), this);
		getServer().getPluginManager().registerEvents(new StatCategoryInventory(), this);
		getServer().getPluginManager().registerEvents(new TopPlayerInventory(), this);
		getCommand("globalquests").setExecutor(this);
		loadPlayers();
	}

	@Override
	public void onDisable() {
		unloadPlayers();
	}

	private int timeUntilReset;

	private void scheduleQuestReset() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			if (getConfig().getInt("mins-until-quests-reset") == 0) {
				System.out.println("Resetting global quests...");
				getConfig().set("mins-until-quests-reset", 4320);
				saveConfig();
				for (Player online : Bukkit.getOnlinePlayers()) {
					PlayerData data = PlayerDataManager.getByUuid(online.getUniqueId());
					data.setBlocksProgress(0);
					data.setFishProgress(0);
					data.setMobsProgress(0);
					online.playSound(online.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 1);
				}
				Player winner = Bukkit
						.getPlayer(GetTop.getTop(getConfig().getString("current-quest")).get(0).getUuid());
				Bukkit.broadcastMessage("§6§l(!) §6Global challenges have been reset!");
				Bukkit.broadcastMessage("§6§l(!) §6The winner of the challenge was " + winner.getName());
				choseNewQuest();
			}
			getConfig().set("mins-until-quests-reset", getConfig().getInt("mins-until-quests-reset") - 1);

		}, 0, 20 * 60);

	}

	private void unloadPlayers() {
		for (PlayerData players : PlayerDataManager.getPlayers()) {
			String path = "players." + players.getUuid().toString();
			getConfig().set(path + ".blocks-progress", players.getBlocksProgress());
			getConfig().set(path + ".fish-progress", players.getFishProgress());
			getConfig().set(path + ".mobs-progress", players.getMobsProgress());
		}
		saveConfig();
	}

	private void loadPlayers() {
		for (Player online : getServer().getOnlinePlayers()) {
			playerManager.registerNewPlayer(online.getUniqueId());
		}
		for (String uuids : getConfig().getConfigurationSection("players").getKeys(false)) {
			UUID uuid = UUID.fromString(uuids);
			playerManager.registerNewPlayer(uuid);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			new StatCategoryInventory().createInventory((Player) sender);
		}
		return true;
	}

	private void processRewards() {
		String currentQuest = getConfig().getString("current-quest");
		for (String places : getConfig().getConfigurationSection("rewards").getKeys(false)) {
			int place = Integer.parseInt(places);
			for (String commands : getConfig().getConfigurationSection("rewards." + places).getKeys(false)) {
				Player placePlayer = Bukkit.getPlayer(GetTop.getTop(currentQuest).get(place - 1).getUuid());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("{player}", placePlayer.getName()));
			}
		}
	}

	public void choseNewQuest() {
		int random = new Random().nextInt(3);
		System.out.println(random);
		if (random == 0) {
			getConfig().set("current-quest", "blocks");
		}
		if (random == 1) {
			getConfig().set("current-quest", "mobs");
		}
		if (random == 2) {
			getConfig().set("current-quest", "fish");
		}
		saveConfig();
	}

}
