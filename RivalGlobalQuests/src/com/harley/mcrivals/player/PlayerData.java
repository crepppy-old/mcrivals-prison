package com.harley.mcrivals.player;

import java.util.UUID;

public class PlayerData {

	private final UUID uuid;
	private int blocksProgress;
	private int mobsProgress;
	private int fishProgress;

	public PlayerData(UUID uuid) {
		this.uuid = uuid;
		PlayerDataManager.getPlayers().add(this);
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getBlocksProgress() {
		return blocksProgress;
	}

	public void setBlocksProgress(int blocksProgress) {
		this.blocksProgress = blocksProgress;
	}

	public int getMobsProgress() {
		return mobsProgress;
	}

	public void setMobsProgress(int mobsProgress) {
		this.mobsProgress = mobsProgress;
	}

	public int getFishProgress() {
		return fishProgress;
	}

	public void setFishProgress(int fishProgress) {
		this.fishProgress = fishProgress;
	}

}
