package com.mcrivals.prisonrankup.events;

import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PlayerData;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class MineBlockBreakEvent extends BlockBreakEvent {
	private final Mine mine;
	private final PlayerData playerData;

	public MineBlockBreakEvent(Block theBlock, PlayerData playerData, Mine mine) {
		super(theBlock, playerData.getPlayer());
		this.mine = mine;
		this.playerData = playerData;
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	public Mine getMine() {
		return mine;
	}
}
