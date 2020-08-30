package com.mcrivals.customenchants.enchants;

import com.mcrivals.customenchants.Enchant;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SharpnessEnchant extends Enchant {
	public SharpnessEnchant() {
		super("Sharpness",
				250,
				ItemType.SWORD,
				new ItemStack(Material.STONE_SWORD),
				5000,
				2000,
				"Increases your swords damage by &e%s");
	}

	@Override
	public String getStat(int level) {
		return String.format("+%.1f%% Damage", level * .4);
	}

	@Override
	public boolean doActivate(int level) {
		return true;
	}

	@Override
	public void playerDamageEntity(EntityDamageByEntityEvent e, int level) {

	}
}
