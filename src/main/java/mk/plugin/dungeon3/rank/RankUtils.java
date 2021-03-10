package mk.plugin.dungeon3.rank;

import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.statistic.DStatistic;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.main.MainDungeon3;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RankUtils {
	
	public static boolean equalOrBetter(Rank r, Rank r2) {
		return r.getValue() >= r2.getValue();
	}
	
	public static void showRank(Player player, Rank r) {
		new BukkitRunnable() {
			int i = -1;
			@Override
			public void run() {
				i++;
				Rank rank = Rank.values()[i];
				player.sendTitle(rank.getColor() + rank.name(), "", 0, 100, 0);
				if (rank == r) {
					this.cancel();
					Bukkit.getScheduler().runTaskLaterAsynchronously(MainDungeon3.get(), () -> {
						player.sendTitle(rank.getColor() + "§l" + rank.name(), rank.getColor() + "§oChiến thắng dungeon", 0, 100, 0);
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 1, 1);
					}, 5);
				}
			}
		}.runTaskTimerAsynchronously(MainDungeon3.get(), 0, 2);

	}
	
	public static Rank getRank(String id, DStatistic s) {
		Rank result = Rank.F;
		for (Rank r : Rank.values()) {
			if (testRank(id, s, r)) result = r;
		}
		return result;
	}
	
	public static boolean testRank(String id, DStatistic s, Rank r) {

		Dungeon d = DDataUtils.getDungeon(id);
		int killRequired = new Double(DGameUtils.countMobs(d) * r.getKillPercent() * 0.01).intValue();
		if (s.getMobKilled() < killRequired) return false;
		
		int saveRequired = new Double(DGameUtils.countSlaves(d) * r.getSavePercent() * 0.01).intValue();
		if (s.getSlaveSaved() < saveRequired) return false;
		
		int timeRequired = new Double(d.getOption().getMaxTime() *  r.getTimePercent() * 0.01).intValue();
		if (s.getTimeSurvived() > timeRequired) return false;
		
		int deadRequired = new Double(d.getRule().getRespawnTime() * r.getDeadPercent() * 0.01).intValue();
		if (s.getDead() > deadRequired) return false;
		
		return true;
		
	}
	
}
