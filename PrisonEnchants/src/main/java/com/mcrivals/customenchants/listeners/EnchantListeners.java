package com.mcrivals.customenchants.listeners;

import com.mcrivals.customenchants.Enchant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Map;

public class EnchantListeners implements Listener {
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player) {
			Map<Enchant, Integer> enchants = Enchant.getEnchants(((Player) e.getDamager()).getItemInHand());
			enchants.forEach((ench, level) -> {
				if (ench.doActivate(level))
					ench.playerDamageEntity(e, level);
			});
			if (e.getEntity().isDead()) enchants.forEach((ench, level) -> {
				if (ench.doActivate(level))
					ench.playerKillEntity(e, level);
			});
		}
		if (e.getEntity() instanceof Player) {
			List<Map<Enchant, Integer>> enchants = Enchant.getArmorEnchants((Player) e.getEntity());
			enchants.forEach(itemEnchant -> itemEnchant.forEach((ench, level) -> {
				if (ench.doActivate(level))
					ench.playerTakenDamage(e, level);
			}));
		}
	}

	@EventHandler // todo change event to mine break event
	public void onBlockBreak(BlockBreakEvent e) {
		Enchant.getEnchants(e.getPlayer().getItemInHand()).forEach((ench, level) -> {
			if (ench.doActivate(level))
				ench.playerBreakBlock(e, level);
		});
	}
}
