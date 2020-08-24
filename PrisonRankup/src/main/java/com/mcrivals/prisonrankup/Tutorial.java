package com.mcrivals.prisonrankup;

import org.bukkit.Location;

import java.util.List;

public class Tutorial {
	private final String name;
	private final Location location;
	private final List<String> messages;
	private final int delay;
	private final String placeholder;

	public Tutorial(String name, Location location, List<String> messages, int delay, String placeholder) {
		this.name = name;
		this.location = location;
		this.messages = messages;
		this.delay = delay;
		this.placeholder = placeholder;
	}

	public String getPlaceholder() {
		return placeholder;
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
