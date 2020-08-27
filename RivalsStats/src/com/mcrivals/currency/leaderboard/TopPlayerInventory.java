package com.mcrivals.currency.leaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import org.bukkit.inventory.meta.SkullMeta;

import com.mcrivals.currency.player.PlayerData;
import com.mcrivals.currency.player.PlayerDataManager;
import com.mcrivals.stats.RivalStats;

public class TopPlayerInventory implements InventoryHolder, Listener {

	private RivalStats main = RivalStats.getPlugin(RivalStats.class);

	private Inventory inv;
	private final short GLASS_COLOR = (short) main.getConfig().getInt("glass-colour");
	private final String PLACEMENT_ITEM_NAME = TranslateString.translateStr("placement-item.name");
	private final List<String> PLACEMENT_ITEM_LORE = TranslateString.translateList("placement-item.lore");
	private final int[] slots = { 10, 11, 12, 13, 14, 15, 16, 21, 22, 23 };

	@Override
	public Inventory getInventory() {
		return inv;
	}

	public void createInventory(Player p, String stat) {
		inv = Bukkit.createInventory(this, 9 * 4, "§f§lLeaderboard §7§l> §f" + stat);

		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE));
		}

		for (int slots : slots) {
			inv.setItem(slots, new ItemStack(Material.AIR));
		}
		int count = 0;
		for (PlayerData top : PlayerDataManager.getPlayers()) {
			inv.setItem(slots[count], getPlacementItem(top.getUuid().toString(), count + 1, stat));
			count++;
		}
		inv.setItem(27, backItem());

		p.openInventory(inv);

	}

	private ItemStack getPlacementItem(String uuid, int place, String stat) {
		ItemStack item = new SkullCreator().itemFromUuid(UUID.fromString(uuid));
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		Player player = Bukkit.getPlayer(UUID.fromString(uuid));
		int amount = figureFromString(player, stat);

		meta.setDisplayName(PLACEMENT_ITEM_NAME.replace("{player}", player.getName()).replace("{position}", place + "")
				.replace("{broken}", amount + ""));
		List<String> lore = new ArrayList<>();
		for (String loreLines : PLACEMENT_ITEM_LORE) {
			lore.add(loreLines.replace("{broken}", amount + "").replace("{position}", place + "").replace("{player}",
					player.getName()));

		}
		meta.setLore(lore);
		meta.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
		item.setItemMeta(meta);
		return item;
	}

	private int figureFromString(Player p, String stat) {
		PlayerData data = PlayerDataManager.getByUuid(p.getUniqueId());
		if (stat.equalsIgnoreCase("deaths")) {
			return data.getDeaths();
		}
		if (stat.equalsIgnoreCase("kills")) {
			return data.getKills();
		}
		if (stat.equalsIgnoreCase("fish")) {
			return data.getFishCaught();
		}
		if (stat.equalsIgnoreCase("blocks")) {
			return data.getBlocksBroken();
		}
		if (stat.equalsIgnoreCase("playtime")) {
			return data.getPlayTimeMins();
		}
		return 0;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() instanceof TopPlayerInventory) {
			e.setCancelled(true);
			if (e.getCurrentItem() != null) {
				if (e.getCurrentItem().isSimilar(backItem())) {
					new StatCategoryInventory().createInventory((Player) e.getWhoClicked());
				}
			}
		}
	}

	private ItemStack backItem() {
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§c§lBACK");
		item.setItemMeta(meta);
		return item;
	}

}
