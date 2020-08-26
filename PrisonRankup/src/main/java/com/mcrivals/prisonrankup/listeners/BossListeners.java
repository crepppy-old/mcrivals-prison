package com.mcrivals.prisonrankup.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class BossListeners implements Listener {
	@EventHandler
	public void onBossHit(EntityDamageByEntityEvent e) {
		if(!e.getDamager().getWorld().getName().endsWith("prestige")) return;
	}
}
