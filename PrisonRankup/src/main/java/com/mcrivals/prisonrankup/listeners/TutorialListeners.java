package com.mcrivals.prisonrankup.listeners;

import com.mcrivals.prisonrankup.PrisonRankup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TutorialListeners implements Listener {
	private final PrisonRankup plugin;

	public TutorialListeners(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMessage(AsyncPlayerChatEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer().getUniqueId())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot chat whilst you are in a tutorial!");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer().getUniqueId())) {
			plugin.getPlayerManager().getInTutorial().remove(e.getPlayer().getUniqueId());
			e.getPlayer().setFlying(false);
			e.getPlayer().setAllowFlight(false);
			Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(e.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer().getUniqueId())
				&& plugin.getPlayerManager().getInTutorial().get(e.getPlayer().getUniqueId()).getLocation() != null) {
			if (e.getFrom().distance(e.getTo()) != 0) {
				// Allow turning even if the player is trying to move
				Location loc = e.getFrom();
				loc.setPitch(e.getTo().getPitch());
				loc.setYaw(e.getTo().getYaw());
				e.setTo(loc);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer().getUniqueId())
				&& plugin.getPlayerManager().getInTutorial().get(e.getPlayer().getUniqueId()).getLocation() != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (plugin.getPlayerManager().getInTutorial().containsKey(player.getUniqueId())
				&& plugin.getPlayerManager().getInTutorial().get(player.getUniqueId()).getLocation() != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockEvent(EntityChangeBlockEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (plugin.getPlayerManager().getInTutorial().containsKey(player.getUniqueId())
				&& plugin.getPlayerManager().getInTutorial().get(player.getUniqueId()).getLocation() != null) {
			e.setCancelled(true);
		}
	}
}
