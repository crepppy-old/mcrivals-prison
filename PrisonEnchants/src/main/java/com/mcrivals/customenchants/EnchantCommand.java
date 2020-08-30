package com.mcrivals.customenchants;

import com.mcrivals.prisoncore.Messages;
import com.mcrivals.prisoncore.Utils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {
	private final CustomEnchants plugin;

	public EnchantCommand(CustomEnchants plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Messages.NOT_PLAYER);
			return true;
		}
		Player player = (Player) sender;
		ItemStack inHand = player.getItemInHand();
		Enchant.ItemType type = Enchant.ItemType.getFromMaterial(inHand.getType());
		if (type == null) {
			plugin.getCore().sendPrefixedMessage("Please hold the item to enchant", player);
			return true;
		}
		player.openInventory(Enchant.generateInventory(type, player));
		return true;
	}
}
