package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		return true;
	}
}
