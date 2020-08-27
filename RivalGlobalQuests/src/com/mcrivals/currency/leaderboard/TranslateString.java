package com.mcrivals.currency.leaderboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.harley.mcrivals.RivalGlobalQuests;

public class TranslateString {

	private static RivalGlobalQuests main = RivalGlobalQuests.getPlugin(RivalGlobalQuests.class);

	public static String translateStr(String path) {
		return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(path));
	}

	public static List<String> translateList(String path) {
		List<String> translatedListed = new ArrayList<>();
		for (String lines : main.getConfig().getStringList(path)) {
			translatedListed.add(ChatColor.translateAlternateColorCodes('&', lines));
		}
		return translatedListed;

	}

}
