package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class RankupCommand implements CommandExecutor {
	private final PrisonRankup plugin;

	public RankupCommand(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	private void msg(String message, Player player, Object... format) {
		plugin.getPrisonCore().sendPrefixedMessage(message, player, format);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = plugin.getPlayerManager().getPlayer(player);
		Function<PlayerData, Boolean> playerCanRankup = (p) -> false;
		if (args.length > 1) {
			if (args[0].equalsIgnoreCase("auto")) {
				boolean enabled = playerData.setAutoRankup(!playerData.isAutoRankup());
				msg("Auto rankup is now " + (enabled ? "enabled" : "disabled"), player);
				return true;
			} else {
				if (args[0].equalsIgnoreCase("max")) {
					playerCanRankup = (p) -> plugin.getPlayerManager().rankup(p);
				} else if (plugin.getMineByName(String.join(" ", args)).isPresent()) {
					String mine = String.join(" ", args);
					playerCanRankup = (p) -> plugin.getPlayerManager().rankup(p) && !p.getMine().getName().equalsIgnoreCase(mine);
				}
			}
		}
		boolean success = plugin.getPlayerManager().rankup(playerData);
		if (!success) rankupReason(playerData);
		else {
			while (true) {
				// playerCanRankup function ranks the player up as a side effect
				if (!playerCanRankup.apply(playerData)) break;
			}
			msg("You just ranked up to Mine " + playerData.getMine().getName(), player);
		}

		return true;
	}

	public void rankupReason(PlayerData player) {
		if (player.getMine().getName().equalsIgnoreCase("Z")) {
			msg(ChatColor.RED + "You are already in Mine Z!", player.getPlayer());
		}
		msg(ChatColor.RED + "You don't have enough gold to rank up!", player.getPlayer()); //todo how much gold off
	}
}
