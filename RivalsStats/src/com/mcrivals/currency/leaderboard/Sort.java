package com.mcrivals.currency.leaderboard;

import java.util.Comparator;

import com.mcrivals.currency.player.PlayerData;

class Sort implements Comparator<PlayerData> {

	private String comparing;

	public Sort(String comparing) {
		this.comparing = comparing;
	}

	@Override
	public int compare(PlayerData a, PlayerData b) {
		if (this.comparing.equalsIgnoreCase("kills")) {
			return a.getKills() - b.getKills();
		}
		if (this.comparing.equalsIgnoreCase("deaths")) {
			return a.getDeaths() - b.getDeaths();
		}
		if (this.comparing.equalsIgnoreCase("fish")) {
			return a.getFishCaught() - b.getFishCaught();
		}
		if (this.comparing.equalsIgnoreCase("blocks")) {
			return a.getBlocksBroken() - b.getBlocksBroken();
		}
		if (this.comparing.equalsIgnoreCase("playtime")) {
			return a.getPlayTimeMins() - b.getPlayTimeMins();
		}
		return 0;
	}
}
