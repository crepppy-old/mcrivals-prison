package com.mcrivals.currency.leaderboard;

import java.util.Comparator;

import com.harley.mcrivals.player.PlayerData;

class Sort implements Comparator<PlayerData> {

	private String comparing;

	public Sort(String comparing) {
		this.comparing = comparing;
	}

	@Override
	public int compare(PlayerData a, PlayerData b) {
		if (this.comparing.equalsIgnoreCase("mobs")) {
			return a.getMobsProgress() - b.getMobsProgress();
		}
		if (this.comparing.equalsIgnoreCase("blocks")) {
			return a.getBlocksProgress() - b.getBlocksProgress();
		}
		if (this.comparing.equalsIgnoreCase("fish")) {
			return a.getFishProgress() - b.getFishProgress();
		}
		return 0;
	}
}
