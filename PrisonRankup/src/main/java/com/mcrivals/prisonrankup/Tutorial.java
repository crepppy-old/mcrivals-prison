package com.mcrivals.prisonrankup;

import com.sun.org.apache.xpath.internal.operations.String;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class Tutorial {
	private final String name;
	private final Location location;
	private final List<String> messages;
	private final int delay;

	public Tutorial(String name, Location location, List<String> messages, int delay) {
		this.name = name;
		this.location = location;
		this.messages = messages;
		this.delay = delay;
	}

	public void execute(Player player) {
		if (location != null)  {

		}
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		return location;
	}

	public List<String> getMessages() {
		return messages;
	}

	public int getDelay() {
		return delay;
	}
}
