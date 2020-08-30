package com.mcrivals.customenchants.enchants;

import com.mcrivals.customenchants.Enchant;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FireAspectEnchant extends Enchant {
	public FireAspectEnchant() {
		super("Fire Aspect",
				25,
				ItemType.SWORD,
				new ItemStack(Material.BLAZE_POWDER),
				150000, 100000,
				"Sets the player alight for &e%s");
	}w

	@Override
	public String getStat(int level) {
		return String.format("%.1f seconds", level * .4);
	}

	@Override
	public boolean doActivate(int level) {
		return true;
	}

	@Override
	public void playerDamageEntity(EntityDamageByEntityEvent e, int level) {
		e.getEntity().setFireTicks((int) (level * .4 * 20L));
	}
}
