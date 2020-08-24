package com.mcrivals.prisonrankup.events;

import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankupEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final PlayerData playerData;
	private final Mine fromMine;
	private final Mine toMine;
	private final int prestige;
	private boolean cancelled;

	public RankupEvent(PlayerData playerData, Mine toMine, int prestige) {
		this.playerData = playerData;
		this.fromMine = playerData.getMine();
		this.toMine = toMine;
		this.prestige = prestige;
		cancelled = false;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public int getPrestige() {
		return prestige;
	}

	public Mine getFromMine() {
		return fromMine;
	}

	public Mine getToMine() {
		return toMine;
	}

	public Player getPlayer() {
		return playerData.getPlayer();
	}

	public PlayerData getPlayerData() {
		return playerData;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}
}
