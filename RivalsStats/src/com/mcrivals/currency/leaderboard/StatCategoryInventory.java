package com.mcrivals.currency.leaderboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;

public class StatCategoryInventory implements InventoryHolder, Listener {

	private static Map<Integer, String> slotToStat = new HashMap<>();
	private Inventory inv;

	@Override
	public Inventory getInventory() {
		return inv;
	}

	public void createInventory(Player p) {
		inv = Bukkit.createInventory(this, 9 * 3, "§6§lLeaderboard");

		for (int i = 0; i < 9 * 3; i++) {
			inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}

		PlayerData data = PlayerDataManager.getByUuid(p.getUniqueId());
		inv.setItem(11, categoryItem(data.getKills(), "Kills", Material.DIAMOND_SWORD, 0));
		inv.setItem(12, categoryItem(data.getDeaths(), "Deaths", Material.SKULL_ITEM, 0));
		inv.setItem(13, categoryItem(data.getBlocksBroken(), "Blocks Broken", Material.STAINED_CLAY, 4));
		inv.setItem(14, categoryItem(data.getFishCaught(), "Fish Caught", Material.COOKED_FISH, 1));
		inv.setItem(15, categoryItem(data.getPlayTimeMins(), "Play Time", Material.WATCH, 0));
		slotToStat.put(11, "Kills");
		slotToStat.put(12, "Deaths");
		slotToStat.put(13, "Blocks");
		slotToStat.put(14, "Fish");
		slotToStat.put(15, "Playtime");

		p.openInventory(inv);
	}

	private ItemStack categoryItem(int playerAmount, String category, Material material, int data) {
		ItemStack item = new ItemStack(material, 1, (short) data);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§f§l" + category);
		meta.setLore(Arrays.asList("§6View Top For §f§n" + category, "§6Your Current: §f§n" + playerAmount));
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
					if (e.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
						new TopPlayerInventory().createInventory(p, slotToStat.get(e.getSlot()));
					}
				}
			}
		}
	}

}
