package com.mcrivals.prisonrankup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {
	private UUID playerUUID;
	private boolean tutorialsEnabled;
	private boolean autoRankup;
	private int prestige;
	private float resourceMultiplier;
	private Mine mine;

	public boolean isAutoRankup() {
		return autoRankup;
	}

	public boolean setAutoRankup(boolean autoRankup) {
		this.autoRankup = autoRankup;
		return autoRankup;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}

	public void setPrestige(int prestige) {
		this.prestige = prestige;
	}

	public int getPrestige() {
		return prestige;
	}

	public PlayerData(UUID playerUUID, boolean tutorialsEnabled, boolean autoRankup, Mine mine, int prestige, int resourceMultiplier) {
		this.playerUUID = playerUUID;
		this.tutorialsEnabled = tutorialsEnabled;
		this.autoRankup = autoRankup;
		this.mine = mine;
		this.prestige = prestige;
		this.resourceMultiplier = resourceMultiplier;
		//todo save on change
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	void setTutorialsEnabled(boolean tutorialsEnabled) {
		this.tutorialsEnabled = tutorialsEnabled;
	}

	void setMine(Mine mine) {
		this.mine = mine;
	}

	public boolean isTutorialsEnabled() {
		return tutorialsEnabled;
	}

	public Mine getMine() {
		return mine;
	}

}
