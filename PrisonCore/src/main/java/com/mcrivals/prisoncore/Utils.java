package com.mcrivals.prisoncore;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
	public static ItemStack createItem(Material material, String name, List<String> lore) {
		return createItem(new ItemStack(material), name, lore);
	}

	public static ItemStack createItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		if(name != null) meta.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name));
		if(lore != null) meta.setLore(lore.stream().map(x -> ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', x)).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material material, String name, String... lore) {
		return createItem(new ItemStack(material), name, Arrays.asList(lore));
	}
}
