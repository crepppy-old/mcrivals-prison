package com.mcrivals.prisoncore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ReflectionUtils {
	private static final HashMap<String, Class<?>> reflectionMap = new HashMap<>();
	private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

	public static void sendPacket(Player player, Object packet) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name) {
		if (reflectionMap.containsKey(name)) return reflectionMap.get(name);
		try {
			Class<?> nmsClass = Class.forName("net.minecraft.server." + version + "." + name);
			reflectionMap.put(name, nmsClass);
			return nmsClass;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}


}
