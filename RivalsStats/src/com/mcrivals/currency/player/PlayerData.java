package com.mcrivals.currency.player;

import java.util.UUID;

public class PlayerData {

	private UUID uuid;
	private int blocksBroken;
	private int kills;
	private int deaths;
	private int fishCaught;
	private int playTimeMins;

	public PlayerData(UUID uuid) {
		this.uuid = uuid;
		PlayerDataManager.getPlayers().add(this);
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getBlocksBroken() {
		return blocksBroken;
	}

	public void setBlocksBroken(int blocksBroken) {
		this.blocksBroken = blocksBroken;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getFishCaught() {
		return fishCaught;
	}

	public void setFishCaught(int fishCaught) {
		this.fishCaught = fishCaught;
	}

	public int getPlayTimeMins() {
		return playTimeMins;
	}

	public void setPlayTimeMins(int playTimeMins) {
		this.playTimeMins = playTimeMins;
	}

}
