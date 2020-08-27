package com.mcrivals.currency;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;

public class CheckCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You must be a player to use this command!");
			return true;
		}
		Player p = (Player) sender;
		PlayerData data = PlayerDataManager.getByUuid(p.getUniqueId());
		if (cmd.getName().equalsIgnoreCase("credits")) {
			p.sendMessage("§6You have §f§n" + data.getCredits() + "§6 credits");
		}
		if (cmd.getName().equalsIgnoreCase("gold")) {
			p.sendMessage("§6You have §f§n" + data.getGold() + "§6 gold");
		}
		if (cmd.getName().equalsIgnoreCase("platinum")) {
			p.sendMessage("§6You have §f§n" + data.getPlatinum() + "§6 platinum");
		}
		if (cmd.getName().equalsIgnoreCase("energy")) {
			p.sendMessage("§6You have §f§n" + data.getEnergy() + "§6 energy");
		}
		if (cmd.getName().equalsIgnoreCase("tokens")) {
			p.sendMessage("§6You have §f§n" + data.getTokens() + "§6 tokens");
		}
		return true;
	}

}
