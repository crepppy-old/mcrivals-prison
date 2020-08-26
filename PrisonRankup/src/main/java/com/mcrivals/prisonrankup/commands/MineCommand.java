package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.Mine;
import com.mcrivals.prisonrankup.PrisonRankup;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.Vector;
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

public class MineCommand implements CommandExecutor {
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
		Player player = (Player) sender;
		if (!sender.hasPermission("prisonrankup.mineadmin")) {
			msg(Messages.NO_PERMISSION, player);
			return true;
		}
		if (args.length == 0) {
			player.sendMessage(helpCommand());
			return true;
		}
		String subcommand = args[0];
		args = Arrays.copyOfRange(args, 1, args.length);
		if (args.length == 0) {
			if (subcommand.equalsIgnoreCase("list")) {
				msg("Currently available mines: ", player);
				for (Mine m : plugin.getMines()) {
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
			} else if (subcommand.equalsIgnoreCase("addcommand")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine addcommand <mine> <command>");
			} else if (subcommand.equalsIgnoreCase("removecommand")) {
				msg(Messages.NOT_ENOUGH_ARGS, player, "/mine removecommand <mine> <command>");
			} else {
				player.sendMessage(helpCommand());
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
						com.sk89q.worldedit.world.World world = worldEdit.getSession(player).getSelectionWorld();
						if (world == null) {
							msg(ChatColor.RED + "Please select the region of the mine with WorldEdit", player);
							return true;
						}
						Region selection = worldEdit.getSession(player).getSelection(world);
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
					Optional<Mine> startMine = plugin.getMineByName(args[0]);
					Optional<Mine> rankupMine = plugin.getMineByName(args[1]);

					if (!startMine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, args[0]);
					else if (!rankupMine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, args[1]);
					else {
						int indexOfRankup = plugin.getMines().indexOf(rankupMine.get());
						plugin.getMines().remove(startMine.get());
						plugin.getMines().add(indexOfRankup - 1, startMine.get());
						msg("Rankup order changed!", player);
					}
				}
			} else if (subcommand.equalsIgnoreCase("addcommand")) {
				if (args.length < 2) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine addcommand <mine> <command>");
				} else {
					Optional<Mine> mine = plugin.getMineByName(args[0]);
					if (!mine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, args[0]);
					else {
						mine.get().addCommand(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
					}
				}
			} else if (subcommand.equalsIgnoreCase("removecommand")) {
				if (args.length < 2) {
					msg(Messages.NOT_ENOUGH_ARGS, player, "/mine removecommand <mine> <command>");
				} else {
					Optional<Mine> mine = plugin.getMineByName(args[0]);
					if (!mine.isPresent())
						msg(ChatColor.RED + "Couldn't find a mine with the name '%s'", player, args[0]);
					else {
						String targetCommand = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
						for (String mineCommand : mine.get().getCommands()) {
							if (mineCommand.toLowerCase().startsWith(targetCommand.toLowerCase())) {
								mine.get().getCommands().remove(mineCommand);
								msg("Command '/%s' has been removed", player, mineCommand);
								return true;
							}
						}
						msg(ChatColor.RED + "This mine has no command like '/%s'", player, targetCommand);
					}
				}
			} else {
				player.sendMessage(helpCommand());
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
				ChatColor.DARK_RED + "/mine addcommand <mine> <command>" + ChatColor.GRAY + " - Add a command to be run when a player ranks up to the mine\n" +
				ChatColor.DARK_RED + "/mine removecommand <mine> <command>" + ChatColor.GRAY + " - Remove a command that's run when a player ranks up\n" +
				ChatColor.DARK_RED + "/mine rankup <mine> <rankup mine>" + ChatColor.GRAY + " - Changes the rankup order of the mines\n" +
				ChatColor.DARK_RED + "/mine list" + ChatColor.GRAY + " - A list of all the current mines and their names\n";
	}
}
