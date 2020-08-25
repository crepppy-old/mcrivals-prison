package com.mcrivals.prisoncore;

import org.bukkit.entity.Player;

public class Packet {
	private final Object packet;
	private final Player player;
	private final boolean write;

	public Packet(Object packet, Player player, boolean write) {
		this.packet = packet;
		this.player = player;
		this.write = write;
	}

	public boolean isWrite() {
		return write;
	}

	public Object getPacket() {
		return packet;
	}

	public Player getPlayer() {
		return player;
	}

}
