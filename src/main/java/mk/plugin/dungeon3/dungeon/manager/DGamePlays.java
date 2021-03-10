package mk.plugin.dungeon3.dungeon.manager;

import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.player.DPlayer;
import mk.plugin.dungeon3.dungeon.statistic.DStatistic;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DGamePlays {
	
	public static void kick(Player player, boolean toSpawn) {
		String id = DPlayerUtils.getCurrentDungeon(player);
		if (id == null) return;
		DStatus sd = DGameUtils.getStatus(id);
		sd.getBossBar().removePlayer(player);
		sd.removePlayer(player);
		if (toSpawn) player.teleport(Utils.getPlayerSpawn());
		Lang.DUNGEON_PLAYER_KICK.send(player);
	}
	
	public static void dead(Player player, boolean teleport) {
		String id = DPlayerUtils.getCurrentDungeon(player);
		Dungeon d = DDataUtils.getDungeon(id);
		if (id == null) return;
		DStatus sd = DGameUtils.getStatus(id);
		DStatistic pds = sd.getStatistic(player);
		
		pds.addDead(1);
		sd.getAllStatistic().addDead(1);
		sd.getTurnStatus().getStatistic().addDead(1);
		
		int maxDead = d.getRule().getRespawnTime() + DPlayer.from(player).getReviveBuff();
		if (pds.getDead() > maxDead) {
			sd.getBossBar().removePlayer(player);
			sd.removePlayer(player);
			if (teleport) player.teleport(Utils.getPlayerSpawn());
			sd.getPlayers().forEach(uuid -> {
				Player p = Bukkit.getPlayer(uuid);
				Lang.DUNGEON_PLAYER_DEAD_KICK_OTHER.send(p, "%player%", "" + player.getName());
			});
			Lang.DUNGEON_PLAYER_DEAD_KICK.send(player, "%dead_remain%", "" + (maxDead - pds.getDead()));
			return;
		}
		
		// God in 3s
		DPlayerUtils.setGod(player, 20 * 3);
		if (teleport) DGameUtils.teleport(player, d.getLocation(sd.getCheckpoint()).getLocation()); 
		Lang.DUNGEON_PLAYER_DEAD_RESPAWN.send(player, "%dead_remain%", "" + (maxDead - pds.getDead()));
		Lang.DUNGEON_PLAYER_GOD.send(player, "%second%", "3");
	}

	
}
