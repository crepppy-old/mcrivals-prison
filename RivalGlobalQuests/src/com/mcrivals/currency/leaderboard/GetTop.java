package com.mcrivals.currency.leaderboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.harley.mcrivals.player.PlayerData;
import com.harley.mcrivals.player.PlayerDataManager;

public class GetTop {

	public static List<PlayerData> getTop(String stat) {
		List<PlayerData> ar = new ArrayList<>(PlayerDataManager.getPlayers());

		Collections.sort(ar, new Sort(stat));
		Collections.reverse(ar);

		return ar;
	}

}
