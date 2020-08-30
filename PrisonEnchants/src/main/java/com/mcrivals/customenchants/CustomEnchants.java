package com.mcrivals.customenchants;

import com.mcrivals.customenchants.listeners.EnchantListeners;
import com.mcrivals.customenchants.listeners.EnchantMenuListeners;
import com.mcrivals.prisoncore.PrisonCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomEnchants extends JavaPlugin {
	private PrisonCore core;

	public PrisonCore getCore() {
		return core;
	}

	@Override
	public void onEnable() {
		core = (PrisonCore) Bukkit.getPluginManager().getPlugin("MCRivalsPrisonCore");
		if (core == null) {
			getLogger().severe(" --- Core is not enabled ---");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		Enchant.registerEnchants();
		getCommand("enchant").setExecutor(new EnchantCommand(this));
		Bukkit.getPluginManager().registerEvents(new EnchantListeners(), this);
		Bukkit.getPluginManager().registerEvents(new EnchantMenuListeners(this), this);
		// Always active enchants
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Enchant.getArmorEnchants(player).forEach(enchants -> enchants.forEach(Enchant::alwaysActive));
			}
		}, 10 * 20L, 10 * 20L);
	}
}
