package com.mcrivals.prisonrankup;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RankupPlaceholderExpansion extends PlaceholderExpansion {
	private final PrisonRankup plugin;

	public RankupPlaceholderExpansion(PrisonRankup plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String onPlaceholderRequest(Player player, @NotNull String params) {
		if (player == null) return "";
		PlayerData data = plugin.getPlayerManager().getPlayer(player);
		if (params.equalsIgnoreCase("mine")) {
			return data.getMine().getName();
		}
		if (params.equalsIgnoreCase("prestige")) {
			return data.getPrestige() == 0 ? "" : String.valueOf(data.getPrestige());
		}
		if (params.equalsIgnoreCase("rank")) {
			return data.getPrestige() == 0 ? data.getMine().getName() : String.valueOf(data.getPrestige());
		}

		return null;
	}

	@Override
	public @NotNull String getIdentifier() {
		return "mcrivalsrankup";
	}

	@Override
	public @NotNull String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}
}
