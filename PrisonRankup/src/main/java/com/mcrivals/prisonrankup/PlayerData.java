package com.mcrivals.prisonrankup;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

public class PlayerData {
	private final UUID playerUUID;
	private transient final HashMap<Material, Float> multiplierDrops;
	private boolean tutorialsEnabled;
	private boolean autoRankup;
	private int prestige;
	private long prestigeCooldown;
	private float resourceMultiplier;
	private Mine mine;

	public PlayerData(UUID playerUUID, boolean tutorialsEnabled, boolean autoRankup, Mine mine, int prestige, int resourceMultiplier, int prestigeCooldown) {
		this.playerUUID = playerUUID;
		this.tutorialsEnabled = tutorialsEnabled;
		this.autoRankup = autoRankup;
		this.mine = mine;
		this.prestige = prestige;
		this.prestigeCooldown = prestigeCooldown;
		this.resourceMultiplier = resourceMultiplier;
		this.multiplierDrops = new HashMap<>();
	}

	public long getPrestigeCooldown() {
		return prestigeCooldown;
	}

	void setPrestigeCooldown(long prestigeCooldown) {
		this.prestigeCooldown = prestigeCooldown;
	}

	public boolean isAutoRankup() {
		return autoRankup;
	}

	void setAutoRankup(boolean autoRankup) {
		this.autoRankup = autoRankup;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(playerUUID);
	}

	public int getPrestige() {
		return prestige;
	}

	void setPrestige(int prestige) {
		this.prestige = prestige;
	}

	public float getResourceMultiplier() {
		return resourceMultiplier;
	}

	void setResourceMultiplier(float resourceMultiplier) {
		this.resourceMultiplier = resourceMultiplier;
	}

	/**
	 * Gets the amount of drops that should be given to the player once the resource
	 * multiplier has been applied
	 *
	 * @param material   The material to give the player (this is needed as the remainder of previous drops
	 *                   is stored)
	 * @param dropAmount The amount that should be multiplied by the resource multiplier
	 * @return The number of drops that should be given to the player
	 * (this could be the same as the original drop amount as the remainder is stored)
	 */
	public int getDrops(Material material, int dropAmount) {
		float extra = multiplierDrops.getOrDefault(material, 0f) + dropAmount * resourceMultiplier;
		multiplierDrops.put(material, extra % 1);
		return (int) extra;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public boolean isTutorialsEnabled() {
		return tutorialsEnabled;
	}

	void setTutorialsEnabled(boolean tutorialsEnabled) {
		this.tutorialsEnabled = tutorialsEnabled;
	}

	public Mine getMine() {
		return mine;
	}

	void setMine(Mine mine) {
		this.mine = mine;
	}
}
