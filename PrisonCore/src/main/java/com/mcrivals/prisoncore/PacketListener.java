package com.mcrivals.prisoncore;

import io.netty.channel.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.function.Function;

public class PacketListener implements Listener {
	private final PrisonCore plugin;

	public PacketListener(PrisonCore plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		listenPlayerPackets(e.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Channel channel = getPlayerChannel(e.getPlayer());
		channel.eventLoop().submit(() -> {
			channel.pipeline().remove(e.getPlayer().getName());
			return null;
		});
	}

	private void listenPlayerPackets(Player player) {
		ChannelDuplexHandler handler = new ChannelDuplexHandler() {
			@Override
			public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
				for (Function<Packet, Boolean> func : plugin.getPacketListeners()) {
					if (func.apply(new Packet(msg, player, true))) return;
				}
				super.write(ctx, msg, promise);
			}

			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				for (Function<Packet, Boolean> func : plugin.getPacketListeners()) {
					if (func.apply(new Packet(msg, player, false))) return;
				}
				super.channelRead(ctx, msg);
			}
		};

		ChannelPipeline pipeline = getPlayerChannel(player).pipeline();
		pipeline.addBefore("packet_handler", player.getName(), handler);
	}

	private Channel getPlayerChannel(Player player) {
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = handle.getClass().getField("playerConnection").get(handle);
			Object netMan = connection.getClass().getField("networkManager").get(connection);
			return (Channel) netMan.getClass().getField("channel").get(netMan);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
