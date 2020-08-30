package com.mcrivals.prisonrankup;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mine {
	private final String name;
	private final Location minimumPoint;
	private final Location maximumPoint;
	private final Map<BlockType, Integer> blockPercentages;
	private final float cost;
	private final List<String> commands;

	public Mine(String name, float cost, Location minimumPoint, Location maximumPoint, Map<BlockType, Integer> blockPercentages, List<String> commands) {
		this.name = name;
		this.minimumPoint = minimumPoint;
		this.maximumPoint = maximumPoint;
		this.cost = cost;
		this.blockPercentages = blockPercentages;
		this.commands = commands;
	}

	public Mine(String name, float cost, Location minimumPoint, Location maximumPoint) {
		this.name = name;
		this.minimumPoint = minimumPoint;
		this.maximumPoint = maximumPoint;
		this.cost = cost;
		this.blockPercentages = new HashMap<>();
		this.commands = new ArrayList<>();
	}

	public float getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public Location getMinimumPoint() {
		return minimumPoint;
	}

	public Location getMaximumPoint() {
		return maximumPoint;
	}

	public List<String> getCommands() {
		return commands;
	}

	public CuboidRegion getMineRegion() {
		return new CuboidRegion(Vector.toBlockPoint(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ()),
				Vector.toBlockPoint(maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ()));
	}

	public boolean isInMine(Location location) {
		return getMineRegion().contains(Vector.toBlockPoint(location.getX(), location.getY(), location.getZ()));
	}

	public Map<BlockType, Integer> getBlockPercentages() {
		return blockPercentages;
	}

	public void addBlock(BlockType material, Integer percentage) {
		blockPercentages.put(material, percentage);
	}

	public void addCommand(String command) {
		commands.add(command);
	}

	public static class BlockType {
		private final Material material;
		private final short data;

		public BlockType(Material material, short data) {
			this.material = material;
			this.data = data;
		}

		public static BlockType fromString(String block) {
			if (block.contains(":")) {
				String[] split = block.split(":");
				return new BlockType(Material.matchMaterial(split[0]), Short.parseShort(split[1]));
			} else return new BlockType(Material.matchMaterial(block), (short) 0);
		}

		@Override
		public String toString() {
			return material.toString() + (data == 0 ? "" : ":" + data);
		}

		public Material getMaterial() {
			return material;
		}

		public short getData() {
			return data;
		}
	}
}
