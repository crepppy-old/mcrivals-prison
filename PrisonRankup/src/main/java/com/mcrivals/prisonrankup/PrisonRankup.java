package com.mcrivals.prisonrankup;

import com.mcrivals.currency.RivalCurrency;
import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.commands.*;
import com.mcrivals.prisonrankup.listeners.MineBreakListener;
import com.mcrivals.prisonrankup.listeners.TutorialListeners;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

public class PrisonRankup extends JavaPlugin {
	private PrisonCore prisonCore;
	private RivalCurrency currency;
	private List<Mine> mines;
	private List<Tutorial> tutorials;
	private PlayerManager playerManager;
	private WorldEditPlugin worldEditPlugin;
	private Permission permissions;
	private FileManager fileManager;

	public RivalCurrency getCurrency() {
		return currency;
	}

	public List<Tutorial> getTutorials() {
		return tutorials;
	}

	public WorldEditPlugin getWorldEditPlugin() {
		return worldEditPlugin;
	}

	@Override
	public void onEnable() {
		saveResource("config.yml", false);
		saveResource("tutorials.yml", false);
		if (!Bukkit.getPluginManager().isPluginEnabled("MCRivalsPrisonCore")) {
			getLogger().severe("-- MCRivalsPrisonCore is not loaded! --");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
			getLogger().severe("-- WorldEdit is not loaded! --");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		permissions = rsp.getProvider();

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			new RankupPlaceholderExpansion(this).register();
		}

		mines = new LinkedList<>();
		tutorials = new ArrayList<>();
		this.currency = (RivalCurrency) Bukkit.getPluginManager().getPlugin("RivalCurrency");
		this.prisonCore = (PrisonCore) Bukkit.getPluginManager().getPlugin("MCRivalsPrisonCore");

		fileManager = new FileManager(this);
		fileManager.loadMines();
		fileManager.loadTutorials();
		playerManager = new PlayerManager(this);
		playerManager.loadPlayers();

		getCommand("rankup").setExecutor(new RankupCommand(this));
		getCommand("thelp").setExecutor(new TutorialCommand(this));
		getCommand("mine").setExecutor(new MineCommand(this));
		getCommand("prestige").setExecutor(new PrestigeCommand(this));
		getCommand("toggletutorials").setExecutor(new ToggleTutorialsCommand(this));
		Bukkit.getPluginManager().registerEvents(new MineBreakListener(this), this);
		Bukkit.getPluginManager().registerEvents(new TutorialListeners(this), this);


		// Schedule mine reset
		Iterator<Mine> mineIterator = mines.iterator();
		int index = 0;
		while (mineIterator.hasNext()) {
			Mine m = mineIterator.next();
			Bukkit.getScheduler().runTaskTimer(this, () -> {
				// Refresh mine
				RandomFillPattern pattern = new RandomFillPattern(m.getBlockPercentages().entrySet()
						.stream()
						.map(bt -> new BlockChance(new BaseBlock(BlockType.lookup(bt.getKey().getMaterial().toString()).getID(), bt.getKey().getData()), bt.getValue()))
						.collect(Collectors.toList()));
				try {
					WorldEdit.getInstance().getEditSessionFactory().getEditSession(new BukkitWorld(m.getMinimumPoint().getWorld()), -1).setBlocks(
							m.getMineRegion(), pattern);
				} catch (MaxChangedBlocksException e) {
					e.printStackTrace();
				}
				for (PlayerData data : playerManager.getPlayers()) {
					if (data.getPlayer() == null || !data.getPlayer().isOnline()) continue;
					if (data.getMine().equals(m)) {
						prisonCore.sendPrefixedMessage(ChatColor.GOLD + "Mine " + m.getName() + " has just reset!", data.getPlayer());
					}
				}
			}, index * 20 * 20L, getConfig().getInt("mine-reset") * 20L);
			index++;
		}
	}

	public PrisonCore getPrisonCore() {
		return prisonCore;
	}

	public Optional<Mine> getMineByName(String name) {
		return mines.stream().filter(x -> x.getName().equalsIgnoreCase(name)).findAny();
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public Permission getPermissions() {
		return permissions;
	}


	@Override
	public void onDisable() {
		fileManager.saveMines();
	}

	public List<Mine> getMines() {
		return mines;
	}

	public void addMine(Mine mine) {
		mines.add(mine);
	}
}
