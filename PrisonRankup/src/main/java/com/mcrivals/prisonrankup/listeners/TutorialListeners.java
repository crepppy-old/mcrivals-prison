package com.mcrivals.prisonrankup.listeners;

import com.mcrivals.prisonrankup.PrisonRankup;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class TutorialListeners implements Listener {
	private final PrisonRankup plugin;

	public TutorialListeners(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMessage(AsyncPlayerChatEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer())) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You cannot chat whilst you are in a tutorial!");
		} else {
			for (Player p : plugin.getPlayerManager().getInTutorial().keySet()) {
				e.getRecipients().remove(p.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer())
				&& plugin.getPlayerManager().getInTutorial().get(e.getPlayer()).getLocation() != null) {
			if(e.getFrom().distance(e.getTo()) != 0) e.setTo(e.getFrom());
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if (plugin.getPlayerManager().getInTutorial().containsKey(e.getPlayer())
				&& plugin.getPlayerManager().getInTutorial().get(e.getPlayer()).getLocation() != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (plugin.getPlayerManager().getInTutorial().containsKey(player)
				&& plugin.getPlayerManager().getInTutorial().get(player).getLocation() != null) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockEvent(EntityChangeBlockEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		Player player = (Player) e.getEntity();
		if (plugin.getPlayerManager().getInTutorial().containsKey(player)
				&& plugin.getPlayerManager().getInTutorial().get(player).getLocation() != null) {
			e.setCancelled(true);
		}
	}
}
