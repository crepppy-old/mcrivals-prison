package com.mcrivals.prisonrankup;

import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.commands.RankupCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class PrisonRankup extends JavaPlugin {
	private PrisonCore prisonCore;
	@Override
	public void onEnable() {
		if(!Bukkit.getPluginManager().isPluginEnabled("MCRivalsPrisonCore")) {
			getLogger().severe("-- MCRivalsPrisonCore is not loaded! --");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		this.prisonCore = (PrisonCore) Bukkit.getPluginManager().getPlugin("MCRivalsPrisonCore");
		getCommand("rankup").setExecutor(new RankupCommand());
	}
}
