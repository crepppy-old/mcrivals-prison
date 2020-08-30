package com.mcrivals.customenchants;

import com.mcrivals.customenchants.enchants.FireAspectEnchant;
import com.mcrivals.customenchants.enchants.SharpnessEnchant;
import com.mcrivals.prisoncore.Utils;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Enchant {
	private static final List<Enchant> registeredEnchants = new ArrayList<>();
	private final String name;
	private final int maxLevel;
	private final ItemType type;
	private final ItemStack menuItem;
	private final float initialCost;
	private final float upgradeCost;
	private final String permission;
	private final String description;
	private final Enchantment vanillaEnchant;

	public Enchant(String name, int maxLevel, ItemType type, ItemStack menuItem, float initialCost, float upgradeCost, String description) {
		this(name, maxLevel, type, menuItem, initialCost, upgradeCost, description, "", null);
	}

	public Enchant(String name, int maxLevel, ItemType type, ItemStack menuItem, float initialCost, float upgradeCost, String description, String permission, Enchantment vanillaEnchant) {
		this.name = name;
		this.maxLevel = maxLevel;
		this.type = type;
		this.menuItem = menuItem;
		this.initialCost = initialCost;
		this.upgradeCost = upgradeCost;
		this.permission = permission;
		this.description = description;
		this.vanillaEnchant = vanillaEnchant;
	}

	public static List<Enchant> getRegisteredEnchants() {
		return registeredEnchants;
	}

	public static ItemStack addEnchant(ItemStack item, Enchant enchant, int level) {
		if (item == null || item.getType() == Material.AIR) return null;
		if (item.getEnchantments().isEmpty()) {
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(Enchantment.LUCK, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey("prisonenchants")) {
			nbtItem.getCompound("prisonenchants").setInteger(enchant.getName(), level);
		} else {
			nbtItem.addCompound("prisonenchants").setInteger(enchant.getName(), level);
		}
		ItemStack newItem = nbtItem.getItem();
		ItemMeta meta = newItem.getItemMeta();
		if (enchant.getVanillaEnchant() != null)
			meta.addEnchant(enchant.getVanillaEnchant(), level, true);
		meta.setLore(new ArrayList<String>() {{
			nbtItem.getCompound("prisonenchants").getKeys().forEach(ench -> {
				int level = nbtItem.getCompound("prisonenchants").getInteger(ench);
				add(ChatColor.DARK_RED + ench + " " + level + ChatColor.GRAY + " -> " + ChatColor.YELLOW + getEnchantByName(ench).getStat(level));
			});
		}});
		newItem.setItemMeta(meta);
		return newItem;
	}

	public static Inventory generateInventory(ItemType type, Player player) {
		Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_RED + WordUtils.capitalizeFully(type.toString().replace("_", " ")) + " Enchantments");
		boolean armor = type == ItemType.HELMET || type == ItemType.CHESTPLATE || type == ItemType.LEGGINGS || type == ItemType.BOOTS;
		for (int i = 0; i < 54; i++)
			inventory.setItem(i, Utils.createItem(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7), "", null));
		for (int i = 1; i <= 2; i++)
			for (int j = 0; j < 5; j++)
				inventory.setItem(i * 9 + 2 + j, null);
		Enchant.getRegisteredEnchants().stream()
				.filter(x -> armor && x.getType() == ItemType.ARMOR || x.getType() == type)
				.filter(x -> x.getPermission().isEmpty() || player.hasPermission(x.getPermission()))
				.forEach(ench -> inventory.addItem(ench.getMenuItem(player.getItemInHand())));
		return inventory;
	}

	public static ItemStack addEnchant(ItemStack item, Enchant enchant) {
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey("prisonenchants")) {
			if (nbtItem.getCompound("prisonenchants").hasKey(enchant.getName())) {
				int level = nbtItem.getCompound("prisonenchants").getInteger(enchant.getName());
				if (enchant.getMaxLevel() == level) return item;
				return addEnchant(item, enchant, level + 1);
			}
		}
		return addEnchant(item, enchant, 1);
	}

	public static Map<Enchant, Integer> getEnchants(ItemStack item) {
		if (item == null || item.getType() == Material.AIR) return Collections.emptyMap();
		NBTItem nbtItem = new NBTItem(item);
		if (nbtItem.hasKey("prisonenchants")) {
			return nbtItem.getCompound("prisonenchants").getKeys().stream().collect(Collectors.toMap(
					Enchant::getEnchantByName,
					enchant -> nbtItem.getCompound("prisonenchants").getInteger(enchant)
			));
		} else return Collections.emptyMap();
	}

	public static List<Map<Enchant, Integer>> getArmorEnchants(Player player) {
		List<Map<Enchant, Integer>> enchantList = new ArrayList<>();
		for (ItemStack item : player.getInventory().getArmorContents())
			enchantList.add(getEnchants(item));
		return enchantList;
	}

	public static Enchant getEnchantByName(String enchant) {
		return registeredEnchants.stream().filter(x -> x.getName().equalsIgnoreCase(enchant)).findFirst().orElse(null);
	}

	public static void registerEnchants() {
		registeredEnchants.add(new SharpnessEnchant());
		registeredEnchants.add(new FireAspectEnchant());
	}

	public Enchantment getVanillaEnchant() {
		return vanillaEnchant;
	}

	public abstract String getStat(int level);

	public double getUpgradeCost(int level) {
		return Math.round(level == 1 ? initialCost : upgradeCost + (level - 1) * (upgradeCost * .75));
	}

	public String getPermission() {
		return permission;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Chooses whether an enchant should activate depending on the enchant level
	 * If required, the player's permissions should also be checked here
	 * (returning <code>false</code> if they do not have permission
	 *
	 * @param level The level of the enchant
	 * @return Whether the enchant should activate
	 */
	public abstract boolean doActivate(int level);

	public void playerTakenDamage(EntityDamageByEntityEvent e, int level) {
	}

	public void playerDamageEntity(EntityDamageByEntityEvent e, int level) {
	}

	public void playerBreakBlock(BlockBreakEvent e, int level) {
	}

	public void playerKillEntity(EntityDamageByEntityEvent e, int level) {
	}

	public void alwaysActive(int level) {
	}

	public String getName() {
		return name;
	}

	public int getMaxLevel() {
		return maxLevel;
	}

	public ItemType getType() {
		return type;
	}

	public ItemStack getMenuItem(ItemStack enchantedItem) {
		if (enchantedItem == null || enchantedItem.getType() == Material.AIR) return null;
		NBTCompound enchants = new NBTItem(enchantedItem).getCompound("prisonenchants");
		ItemStack item = menuItem.clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		int level = enchants != null && enchants.getKeys().contains(name) ? enchants.getInteger(name) + 1: 1;
		lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + ChatColor.translateAlternateColorCodes('&', String.format(description, getStat(level))));
		lore.add("");
		lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Cost: " + ChatColor.YELLOW + getUpgradeCost(level) + " Platinum");
		meta.setLore(lore);
		item.setItemMeta(meta);
		NBTItem nbtItem = new NBTItem(item);
		NBTCompound compound = nbtItem.addCompound("ench");
		compound.setString("name", name);
		compound.setInteger("level", level);
		return nbtItem.getItem();
	}

	public enum ItemType {
		PICKAXE, SWORD, BOW, ARMOR, CHESTPLATE, HELMET, BOOTS, LEGGINGS;

		public static ItemType getFromMaterial(Material material) {
			String materialName = material.toString();
			if (materialName.endsWith("PICKAXE")) return PICKAXE;
			if (materialName.endsWith("SWORD")) return SWORD;
			if (materialName.endsWith("BOW")) return BOW;
			if (materialName.endsWith("HELMET")) return HELMET;
			if (materialName.endsWith("CHESTPLATE")) return CHESTPLATE;
			if (materialName.endsWith("LEGGINGS")) return LEGGINGS;
			if (materialName.endsWith("BOOTS")) return BOOTS;
			return null;
		}
	}
}
