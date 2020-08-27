package com.mcrivals.currency.player;

import java.util.UUID;

public class PlayerData {

	private UUID uuid;
	private long credits;
	private long gold;
	private long platinum;
	private long energy;
	private long tokens;

	public PlayerData(UUID uuid) {
		this.uuid = uuid;
		PlayerDataManager.getPlayers().add(this);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public long getCredits() {
		return credits;
	}

	public void setCredits(long credits) {
		this.credits = credits;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public long getPlatinum() {
		return platinum;
	}

	public void setPlatinum(long platinum) {
		this.platinum = platinum;
	}

	public long getEnergy() {
		return energy;
	}

	public void setEnergy(long energy) {
		this.energy = energy;
	}

	public long getTokens() {
		return tokens;
	}

	public void setTokens(long tokens) {
		this.tokens = tokens;
	}

}
