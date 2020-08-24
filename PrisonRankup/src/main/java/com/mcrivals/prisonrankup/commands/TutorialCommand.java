package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TutorialCommand implements CommandExecutor {
	private final PrisonRankup plugin;

	public TutorialCommand(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		PlayerData data = plugin.getPlayerManager().getPlayer(player);
		if(args.length == 0) {
			plugin.getPrisonCore().sendPrefixedMessage(Messages.NOT_ENOUGH_ARGS, player, "/thelp <tutorial>");
		} else {
			String tutorial = String.join(" ", args);

		}
		return true;
	}
}
