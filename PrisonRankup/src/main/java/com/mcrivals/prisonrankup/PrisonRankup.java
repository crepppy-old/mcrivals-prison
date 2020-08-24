package com.mcrivals.prisonrankup;

import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisonrankup.commands.MineCommand;
import com.mcrivals.prisonrankup.commands.RankupCommand;
import com.mcrivals.prisonrankup.commands.TutorialCommand;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrisonRankup extends JavaPlugin {
	private PrisonCore prisonCore;
	private LinkedList<Mine> mines;
	private PlayerManager playerManager;
	private WorldEditPlugin worldEditPlugin;
	private Permission permissions;
	private FileManager fileManager;

	public WorldEditPlugin getWorldEditPlugin() {
		return worldEditPlugin;
	}

	@Override
	public void onEnable() {
		saveResource("config.yml", false);
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

		mines = new LinkedList<>();
		this.prisonCore = (PrisonCore) Bukkit.getPluginManager().getPlugin("MCRivalsPrisonCore");
		getCommand("rankup").setExecutor(new RankupCommand(this));
		getCommand("thelp").setExecutor(new TutorialCommand(this));
		getCommand("mine").setExecutor(new MineCommand(this));

		fileManager = new FileManager(this);
		fileManager.loadMines();
		playerManager = new PlayerManager(this);
		playerManager.loadPlayers();

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
							new CuboidRegion(toVector(m.getMinimumPoint()), toVector(m.getMaximumPoint())), pattern);
				} catch (MaxChangedBlocksException e) {
					e.printStackTrace();
				}
				for (PlayerData data : playerManager.getPlayers()) {
					if (data.getMine().equals(m)) {
						prisonCore.sendPrefixedMessage(ChatColor.GOLD + "Mine " + m.getName() + " has just reset!", data.getPlayer());
					}
				}
			}, index * 20 * 20L, getConfig().getInt("mine-reset") * 20L);
			index++;
		}
	}

	private Vector toVector(Location loc) {
		return Vector.toBlockPoint(loc.getX(), loc.getY(), loc.getZ());
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

	public LinkedList<Mine> getMines() {
		return mines;
	}

	public void addMine(Mine mine) {
		mines.add(mine);
	}
}
