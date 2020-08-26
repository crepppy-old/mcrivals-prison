package com.mcrivals.prisonrankup.commands;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisonrankup.PlayerData;
import com.mcrivals.prisonrankup.PrisonRankup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleTutorialsCommand implements CommandExecutor {
	private final PrisonRankup plugin;

	public ToggleTutorialsCommand(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		PlayerData playerData = plugin.getPlayerManager().getPlayer((Player) sender);
		plugin.getPlayerManager().setTutorialsEnabled(playerData, !playerData.isTutorialsEnabled());
		plugin.getPrisonCore().sendPrefixedMessage(
				"You will %s see tutorials automatically when a feature is unlocked.%s",
				(Player) sender,
				playerData.isTutorialsEnabled() ? "now" : "no longer",
				playerData.isTutorialsEnabled() ? "" : " You can still run /thelp in order to manually see a tutorial");
		return true;
	}
}
