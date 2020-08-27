package com.mcrivals.currency.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;

public class GetTop implements CommandExecutor {

	public static List<PlayerData> getTop(String stat) {
		List<PlayerData> ar = new ArrayList<>(PlayerDataManager.getPlayers());

		Collections.sort(ar, new Sort(stat));
		Collections.reverse(ar);

		return ar;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("leaderboard")) {
				new StatCategoryInventory().createInventory(p);
			}
		}
		return true;
	}

}
