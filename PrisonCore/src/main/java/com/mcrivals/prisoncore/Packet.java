package com.mcrivals.prisoncore;

import org.bukkit.entity.Player;

public class Packet {
	private Object packet;
	private Player player;
	private boolean write;

	public boolean isWrite() {
		return write;
	}

	public Packet(Object packet, Player player, boolean write) {
		this.packet = packet;
		this.player = player;
		this.write = write;
	}

	public Object getPacket() {
		return packet;
	}

	public Player getPlayer() {
		return player;
	}

}
