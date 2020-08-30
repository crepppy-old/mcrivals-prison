package com.mcrivals.customenchants.listeners;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;
import com.mcrivals.customenchants.CustomEnchants;
import com.mcrivals.customenchants.Enchant;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class EnchantMenuListeners implements Listener {
	private final CustomEnchants plugin;

	public EnchantMenuListeners(CustomEnchants plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		Player player = (Player) e.getWhoClicked();
		if (inv.getTitle().startsWith("§4") && inv.getTitle().endsWith("Enchantments")) {
			e.setCancelled(true);
			NBTCompound compound = new NBTItem(e.getCurrentItem()).getCompound("ench");
			Enchant enchant = Enchant.getEnchantByName(compound.getString("name"));
			PlayerData data = PlayerDataManager.getByUuid(e.getWhoClicked().getUniqueId());
			int level = compound.getInteger("level");
			double cost = enchant.getUpgradeCost(level);
			if (data.getPlatinum() < cost) {
				plugin.getCore().sendPrefixedMessage("&cYou need &e%d&c more Platinum to buy this", player, ((int) cost - data.getPlatinum()));
			} else {
				plugin.getCore().sendPrefixedMessage("You upgraded %s to level %d", player, enchant.getName(), level);
				ItemStack newHand = Enchant.addEnchant(player.getItemInHand(), enchant, level);
				player.setItemInHand(newHand);
				player.openInventory(Enchant.generateInventory(
						Enchant.ItemType.valueOf(ChatColor.stripColor(inv.getTitle().split(" ")[0]).toUpperCase()), player));
			}
		}
	}
}
