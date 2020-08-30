package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PrestigeCommand implements CommandExecutor {
	private final PrisonRankup plugin;
	private final Set<UUID> confirming;

	public PrestigeCommand(PrisonRankup plugin) {
		this.plugin = plugin;
		confirming = new HashSet<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		PlayerData playerData = plugin.getPlayerManager().getPlayer(player);
		if (!playerData.getMine().getName().equalsIgnoreCase("Z"))
			plugin.getPrisonCore().sendPrefixedMessage(ChatColor.RED + "You must be at Mine Z before you can prestige!", player);
		else if (playerData.getPrestige() == 30) {
			plugin.getPrisonCore().sendPrefixedMessage(ChatColor.RED + "You are already at max prestige", player);
		} else if (playerData.getPrestigeCooldown() > System.currentTimeMillis()) {
			plugin.getPrisonCore().sendPrefixedMessage(ChatColor.RED + "You already tried to prestige recently. You can do this again in %dh %dm",
					player, playerData.getPrestigeCooldown() / 1000 / 60 / 60, playerData.getPrestigeCooldown() / 1000 / 60 % 60);
		} else {
			if (confirming.contains(player.getUniqueId())) {
				if (!plugin.getPlayerManager().tryPrestige(playerData)) {
					plugin.getPrisonCore().sendPrefixedMessage(ChatColor.RED + "You cannot currently prestige", player);
				}
				confirming.remove(player.getUniqueId());
				return true;
			}
			player.spigot().sendMessage(
					new ComponentBuilder(plugin.getPrisonCore().getPrefix())
							.append("Are you sure you want to prestige? Click here or run /prestige again to confirm")
							.color(net.md_5.bungee.api.ChatColor.DARK_RED).bold(true)
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/prestige"))
							.create()
			);
			confirming.add(player.getUniqueId());
			// Player will have to confirm again after 20s
			Bukkit.getScheduler().runTaskLater(plugin, () -> confirming.remove(player.getUniqueId()), 20 * 20L);
		}
		return true;
	}
}
