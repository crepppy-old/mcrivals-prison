package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PrisonRankup;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MineCommand implements CommandExecutor {
	private static final Pattern rankupPattern = Pattern.compile("\"?([^\"]+)\"? \"?([^\"]+)\"?");
	private final PrisonRankup plugin;
	private final PrisonCore core;

	public MineCommand(PrisonRankup plugin) {
		this.plugin = plugin;
		this.core = plugin.getPrisonCore();
	}

	private void msg(String message, Player player, Object... format) {
		core.sendPrefixedMessage(message, player, format);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		if (!sender.hasPermission("prisonrankup.mineadmin")) {
			sender.sendMessage(Messages.NO_PERMISSION);
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) {
			msg(Messages.NOT_ENOUGH_ARGS, player, "/mine <create|delete|addblock|removeblock|rankup|list>");
			return true;
		}
		String subcommand = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);
		if (args.length == 0) {
			if (subcommand.equalsIgnoreCase("list")) {
				msg("Currently available mines: ", player);
				for(Mine m : plugin.getMines()) {
					player.sendMessage(ChatColor.GRAY + "  - " + m.getName());
				}
			} else if (subcommand.equalsIgnoreCase("create")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine create <name> <gold>");
			} else if (subcommand.equalsIgnoreCase("delete")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine delete <name>");
			} else if (subcommand.equalsIgnoreCase("addblock")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine addblock <block> <percentage> <name>");
			} else if (subcommand.equalsIgnoreCase("removeblock")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine removeblock <block> <mine>");
			} else if (subcommand.equalsIgnoreCase("rankup")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine rankup <mine> <rankup mine>");
			} else {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine <create|delete|addblock|removeblock|rankup>");
			}
		} else {
			if (subcommand.equalsIgnoreCase("create")) {
				if (args.length < 2) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine create <name> <gold>");
				} else {
					String costStr = args[args.length - 1];
					if (!isNumeric(costStr)) {
						msg(ChatColor.RED + costStr + " is not a valid number of gold", player);
						return true;
					}
					float cost = Float.parseFloat(costStr);
					String name = String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1));
					if (plugin.getMineByName(name).isPresent()) {
						msg(ChatColor.RED + "A mine with this name already exists", player);
						return true;
					}
					WorldEditPlugin worldEdit = plugin.getWorldEditPlugin();
					try {
						Region selection = worldEdit.getSession(player).getSelection(new BukkitWorld(player.getWorld()));
						Mine m = new Mine(name, cost,
								vectorToLocation(selection.getMinimumPoint(), player.getWorld()),
								vectorToLocation(selection.getMaximumPoint(), player.getWorld()));
						plugin.addMine(m);
						player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.5F);
						msg("Created mine '%s' with your worldedit selection", player, m.getName());
					} catch (IncompleteRegionException e) {
						msg(ChatColor.RED + "Please select the region of the mine with WorldEdit", player);
					}
				}
			} else if (subcommand.equalsIgnoreCase("delete")) {
				String name = String.join(" ", args);
				if (plugin.getMineByName(name).isPresent()) {
					msg("Deleted mine '%s'", player, name);
				} else {
					msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, name);
				}
			} else if (subcommand.equalsIgnoreCase("addblock")) {
				if (args.length < 3) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine addblock <block> <percentage> <mine>");
				} else {
					Mine.BlockType block = Mine.BlockType.fromString(args[0]);
					if (block.getMaterial() == null) {
						msg(ChatColor.RED + "This block doesn't exist!", player);
						return true;
					}
					Integer percentage = Integer.parseInt(args[1]);
					String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					Optional<Mine> mineOptional = plugin.getMineByName(name);
					if (mineOptional.isPresent()) {
						mineOptional.get().addBlock(block, percentage);
						msg("Set %s percentage to %d for mine '%s'", player, block.toString(), percentage, name);
					} else {
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, name);
					}
				}
			} else if (subcommand.equalsIgnoreCase("removeblock")) {
				if (args.length < 2) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine removeblock <block> <mine>");
				} else {
					Mine.BlockType block = Mine.BlockType.fromString(args[0]);
					if (block.getMaterial() == null) {
						msg(ChatColor.RED + "This block doesn't exist!", player);
						return true;
					}
					String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					Optional<Mine> mineOptional = plugin.getMineByName(name);
					if (mineOptional.isPresent()) {
						msg("Removed %s from mine '%s'", player, block.toString(), name);
					} else {
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, name);
					}
				}
			} else if (subcommand.equalsIgnoreCase("rankup")) {
				if (args.length < 2) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine removeblock <block> <mine>");
				} else {
					Matcher match = rankupPattern.matcher(String.join(" ", args));
					Optional<Mine> startMine = plugin.getMineByName(match.group(1));
					Optional<Mine> rankupMine = plugin.getMineByName(match.group(2));

					if (!startMine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, match.group(1));
					else if (!rankupMine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, match.group(2));
					else {
						int indexOfRankup = plugin.getMines().indexOf(rankupMine.get());
						plugin.getMines().remove(startMine.get());
						plugin.getMines().add(indexOfRankup - 1, startMine.get());
						msg("Rankup order changed!", player);
					}
				}
			} else {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine <create|delete|addblock|removeblock|rankup>");
			}
		}

		return true;
	}

	private boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c) || c == '.') return false;
		}
		return true;
	}

	private Location vectorToLocation(Vector vector, World world) {
		return new Location(world, vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
	}

	private String helpCommand() {
		return plugin.getPrisonCore().getPrefix() + "List of commands:\n" +
		ChatColor.DARK_RED + "/mine create <name> <gold>" + ChatColor.GRAY + " - Creates a new mine with your worldguard selection\n" +
		ChatColor.DARK_RED + "/mine delete <name>" + ChatColor.GRAY + " - Deletes the mine specified\n" +
		ChatColor.DARK_RED + "/mine addblock <block> <percentage> <name>" + ChatColor.GRAY + " - Add a block to the mine\n" +
		ChatColor.DARK_RED + "/mine removeblock <block> <mine>" + ChatColor.GRAY + " - Remove a block from the mine\n" +
		ChatColor.DARK_RED + "/mine rankup <mine> <rankup mine>" + ChatColor.GRAY + " - Changes the rankup order of the mines\n";
		//todo
	}
}
