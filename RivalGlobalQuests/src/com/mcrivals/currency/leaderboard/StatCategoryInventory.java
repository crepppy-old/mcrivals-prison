package com.mcrivals.currency.leaderboard;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.harley.mcrivals.RivalGlobalQuests;
import com.harley.mcrivals.player.PlayerData;
import com.harley.mcrivals.player.PlayerDataManager;

public class StatCategoryInventory implements InventoryHolder, Listener {

	private RivalGlobalQuests main = RivalGlobalQuests.getPlugin(RivalGlobalQuests.class);
	private Inventory inv;

	@Override
	public Inventory getInventory() {
		return inv;
	}

	public void createInventory(Player p) {
		inv = Bukkit.createInventory(this, 9 * 3, "§6§lGlobal Quests Leaderboard");

		for (int i = 0; i < 9 * 3; i++) {
			inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}

		PlayerData data = PlayerDataManager.getByUuid(p.getUniqueId());
		if (main.getConfig().getString("current-quest").equalsIgnoreCase("mobs")) {
			inv.setItem(13, categoryItem(data.getMobsProgress(), "Mobs", Material.ROTTEN_FLESH, 0));
		}
		if (main.getConfig().getString("current-quest").equalsIgnoreCase("blocks")) {
			inv.setItem(13, categoryItem(data.getBlocksProgress(), "Blocks Broken", Material.IRON_ORE, 4));
		}
		if (main.getConfig().getString("current-quest").equalsIgnoreCase("fish")) {
			inv.setItem(13, categoryItem(data.getFishProgress(), "Fish Caught", Material.COOKED_FISH, 1));
		}
		inv.setItem(22, resetCounterItem());

		p.openInventory(inv);
	}

	private ItemStack categoryItem(int playerAmount, String category, Material material, int data) {
		ItemStack item = new ItemStack(material, 1, (short) data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§f§l" + category);
		meta.setLore(Arrays.asList("§6View Quest Top For §f§n" + category, "§6Your Current: §f§n" + playerAmount));
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof StatCategoryInventory) {
			e.setCancelled(true);
			if (e.getView().getTopInventory() == e.getClickedInventory()) {
				Player p = (Player) e.getWhoClicked();
				if (e.getCurrentItem() != null) {
					if (e.getSlot() == 13) {
						new TopPlayerInventory().createInventory(p, main.getConfig().getString("current-quest"));
					}
				}
			}
		}
	}

	private ItemStack resetCounterItem() {
		ItemStack item = new ItemStack(Material.WATCH);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§6§lQUEST RESET");
		int timeHours = main.getConfig().getInt("mins-until-quests-reset") / 60;
		int timeMins = main.getConfig().getInt("mins-until-quests-reset") % 60;
		meta.setLore(Arrays.asList("§fThis quest will reset in §6§n" + timeHours + "h§f and §6§n" + timeMins + "m"));
		item.setItemMeta(meta);
		return item;
	}

}
