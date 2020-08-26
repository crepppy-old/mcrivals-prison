package com.mcrivals.prisonrankup;

import com.google.gson.JsonObject;
import com.mcrivals.currency.player.PlayerDataManager;
import com.mcrivals.prisoncore.Packet;
import com.mcrivals.prisoncore.PrisonCore;
import com.mcrivals.prisoncore.ReflectionUtils;
import com.mcrivals.prisonrankup.events.RankupEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class PlayerManager {
	private final PrisonRankup plugin;
	private final HashMap<UUID, PlayerData> players;
	private final HashMap<UUID, Tutorial> inTutorial;
	private final PrisonCore core;

	public PlayerManager(PrisonRankup plugin) {
		this.plugin = plugin;
		this.core = plugin.getPrisonCore();
		this.players = new HashMap<>();
		this.inTutorial = new HashMap<>();
	}

	public HashMap<UUID, Tutorial> getInTutorial() {
		return inTutorial;
	}

	public void loadPlayers() {
		try {
			if (core.getConnection() == null || core.getConnection().isClosed()) return;

			core.getConnection().createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS prisonrankup");
			core.getConnection().createStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS prisonrankup.players (" +
							"uuid VARCHAR(36) PRIMARY KEY," +
							"tutorials BOOLEAN DEFAULT TRUE," +
							"rankup BOOLEAN DEFAULT FALSE," +
							"prestige INTEGER DEFAULT 0," +
							"cooldown INTEGER DEFAULT 0," +
							"resource_multiplier FLOAT DEFAULT 1.0," +
							"mine VARCHAR(255) NOT NULL)");
			ResultSet playerRs = core.getConnection().createStatement().executeQuery("SELECT * FROM prisonrankup.players");
			if (playerRs.isBeforeFirst()) {
				while (playerRs.next()) {
					Mine mine = plugin.getMineByName(playerRs.getString("mine")).get();
					UUID uuid = UUID.fromString(playerRs.getString("uuid"));
					players.put(uuid, new PlayerData(uuid, playerRs.getBoolean("tutorials"),
							playerRs.getBoolean("rankup"), mine, playerRs.getInt("prestige"),
							playerRs.getInt("resource_multiplier"), playerRs.getInt("cooldown")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PlayerData getPlayer(Player player) {
		if (players.containsKey(player.getUniqueId())) {
			return players.get(player.getUniqueId());
		} else {
			PlayerData data = new PlayerData(player.getUniqueId(), true, false, plugin.getMines().get(0), 0, 1, 0);
			players.put(player.getUniqueId(), data);
			try {
				if (!core.getConnection().isClosed()) {
					PreparedStatement statement = core.getConnection().prepareStatement(
							"INSERT INTO prisonrankup.players(uuid, mine) VALUES (?, ?)");
					statement.setString(1, player.getUniqueId().toString());
					statement.setString(2, data.getMine().getName());
					statement.executeUpdate();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			return data;
		}
	}

	/**
	 * @param player The player to rankup
	 * @return <code>true</code> if the player successfully ranked up
	 */
	public boolean rankup(PlayerData player) {
		int index = plugin.getMines().indexOf(player.getMine());
		if (player.getMine().getName().equalsIgnoreCase("Z") || plugin.getMines().size() == index + 1) return false;
		Mine toMine = plugin.getMines().get(index + 1);
		com.mcrivals.currency.player.PlayerData currencyData = PlayerDataManager.getByUuid(player.getPlayerUUID());
		if(currencyData.getGold() < toMine.getCost()) return false;
		// Check no other plugins want to cancel this event first
		RankupEvent event = new RankupEvent(player, toMine, 0);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		currencyData.setGold((long) (currencyData.getGold() - toMine.getCost()));
		setMine(player, plugin.getMines().get(index + 1));
		setResourceMultiplier(player, (float) (player.getResourceMultiplier() + plugin.getConfig().getDouble("resource-multiplier-increment")));
		Arrays.stream(plugin.getPermissions().getGroups())
				.filter(x -> x.equalsIgnoreCase(plugin.getConfig().getString("group-prefix") + player.getMine().getName()))
				.findAny()
				.ifPresent(group -> plugin.getPermissions().playerAddGroup(player.getPlayer(), group));
		Utils.runCommandList(toMine.getCommands(), player);
		// todo highlight scoreboard
		return true;
	}

	/**
	 * Increases the players prestige level by 1
	 *
	 * @param player The player to prestige
	 * @return <code>true</code> if the player successfully prestige
	 * @see PlayerManager#tryPrestige(PlayerData) tryPrestige to send player to arena
	 */
	public boolean prestige(PlayerData player) {
		RankupEvent event = new RankupEvent(player, null, player.getPrestige() + 1);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;

		int newPrestige = player.getPrestige() + 1;
		Utils.runCommandList(plugin.getConfig().getStringList("prestige-commands"), player);
		if (plugin.getConfig().contains("prestige." + newPrestige))
			Utils.runCommandList(plugin.getConfig().getStringList("prestige." + newPrestige), player);
		setPrestige(player, newPrestige);
		return true;
	}

	/**
	 * If the player meets the requirements, teleports the player to the prestige arena
	 *
	 * @param player The player to send
	 * @return Whether the player was successfully sent to the arena NOT whether the player was
	 * successful in killing the boss
	 */
	public boolean tryPrestige(PlayerData player) {
		if (!player.getMine().getName().equalsIgnoreCase("Z")) return false;
		if (player.getPrestige() == 30) return false;
		File worldFolder = new File(Bukkit.getWorldContainer(), plugin.getConfig().getString("prestige-world"));
		String prestigeWorldName = player.getPlayerUUID().toString() + "prestige";
		File prestigeWorldFolder = new File(Bukkit.getWorldContainer(), prestigeWorldName);
		try {
			try (Stream<Path> stream = Files.walk(worldFolder.toPath())) {
				stream.forEach(source -> {
					try {
						Files.copy(source, prestigeWorldFolder.toPath().resolve(worldFolder.toPath().relativize(source)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		World prestigeWorld = Bukkit.createWorld(new WorldCreator(prestigeWorldName));
		String[] locationSplit = plugin.getConfig().getString("prestige-world-spawn").split(" ");
		Location location = new Location(prestigeWorld,
				Integer.parseInt(locationSplit[0]),
				Integer.parseInt(locationSplit[1]),
				Integer.parseInt(locationSplit[2]));
		player.getPlayer().teleport(location);
		int l = player.getPrestige() + 1;
		switch (l / 5) {
			case 0:
				// zombie
				break;
			case 1:
				// snowman
				break;
			case 2:
				// wither skeleton
				break;
			case 3:
				// iron golem
				break;
			case 4:
				// wither
				break;
			case 5:
				// horseman
				break;
		}
		return true;
	}

	public void startTutorial(Player player, Tutorial tutorial) {
		inTutorial.put(player.getUniqueId(), tutorial);
		boolean flying = player.isFlying();
		boolean allowed = player.getAllowFlight();
		Location location = player.getLocation();

		if (tutorial.getLocation() != null) {
			player.teleport(tutorial.getLocation());
			player.setAllowFlight(true);
			player.setFlying(true);
		}
		// Craft title packet
		try {
			Class<?> actionEnum = ReflectionUtils.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0];
			Class<?> baseComponent = ReflectionUtils.getNMSClass("IChatBaseComponent");
			Constructor<?> constructor = ReflectionUtils.getNMSClass("PacketPlayOutTitle").getConstructor(actionEnum, baseComponent);

			JsonObject titleJson = new JsonObject();
			titleJson.addProperty("text", "New Feature!");
			titleJson.addProperty("color", "dark_red");
			Object titleMessage = baseComponent.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null,
					titleJson.toString());
			Object titlePacket = constructor.newInstance(actionEnum.getField("TITLE").get(null), titleMessage);

			JsonObject subtitleJson = new JsonObject();
			subtitleJson.addProperty("text", tutorial.getName());
			subtitleJson.addProperty("color", "gray");
			Object subtitleMessage = baseComponent.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null,
					subtitleJson.toString());
			Object subtitlePacket = constructor.newInstance(actionEnum.getField("SUBTITLE").get(null), subtitleMessage);

			Object timingPacket = ReflectionUtils.getNMSClass("PacketPlayOutTitle").getConstructor(int.class, int.class, int.class).newInstance(5, 40, 5);

			ReflectionUtils.sendPacket(player, timingPacket);
			ReflectionUtils.sendPacket(player, titlePacket);
			ReflectionUtils.sendPacket(player, subtitlePacket);
		} catch (ReflectiveOperationException ex) {
			ex.printStackTrace();
		}

		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1.5F);
		player.sendMessage(String.join("\n", tutorial.getMessages()));
		Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
		player.spigot().sendMessage(
				new ComponentBuilder("\nDon't want to see these tutorials? Click here to disable them")
						.color(ChatColor.DARK_RED)
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/toggletutorials"))
						.create());
		Function<Packet, Boolean> func = p -> {
			if (p.getPlayer().getUniqueId() != player.getUniqueId()) return false;
			if (!p.getPacket().getClass().getSimpleName().equalsIgnoreCase("PacketPlayOutChat")) return false;
			try {
				Object component = p.getPacket().getClass().getField("a").get(p.getPacket());
				System.out.println((String) component.getClass().getMethod("getText").invoke(component));
			} catch (ReflectiveOperationException ignored) {
			}
			return true;
		};
		core.addListener(func);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			core.getPacketListeners().remove(func);
			if (player.isOnline()) {
				inTutorial.remove(player.getUniqueId());
				player.teleport(location);
				Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
				if (!flying) player.setFlying(false);
				if (!allowed) player.setAllowFlight(false);
			}
		}, tutorial.getDelay() * 20L);
	}

	public Collection<PlayerData> getPlayers() {
		return players.values();
	}

	public void setPrestige(PlayerData player, int prestige) {
		player.setPrestige(prestige);
		updatePropertySQL(player, "prestige", prestige);
	}

	public void setResourceMultiplier(PlayerData player, float resourceMultiplier) {
		player.setResourceMultiplier(resourceMultiplier);
		updatePropertySQL(player, "resource_multiplier", resourceMultiplier);
	}

	public void setAutoRankup(PlayerData player, boolean autoRankup) {
		player.setAutoRankup(autoRankup);
		updatePropertySQL(player, "rankup", autoRankup);
	}

	public void setPrestigeCooldown(PlayerData player, long prestigeCooldown) {
		player.setPrestigeCooldown(prestigeCooldown);
		updatePropertySQL(player, "cooldown", prestigeCooldown);
	}

	public void setTutorialsEnabled(PlayerData player, boolean tutorialsEnabled) {
		player.setTutorialsEnabled(tutorialsEnabled);
		updatePropertySQL(player, "tutorials", tutorialsEnabled);
	}

	public void setMine(PlayerData player, Mine mine) {
		player.setMine(mine);
		updatePropertySQL(player, "mine", mine.getName());
	}

	private void updatePropertySQL(PlayerData playerData, String column, Object value) {
		try {
			PreparedStatement statement = core.getConnection().prepareStatement(
					String.format("UPDATE prisonrankup.players SET %s = ? WHERE uuid = ?", column));
			statement.setObject(1, value);
			statement.setObject(2, playerData.getPlayerUUID().toString());
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}