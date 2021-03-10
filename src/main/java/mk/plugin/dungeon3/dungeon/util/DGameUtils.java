package mk.plugin.dungeon3.dungeon.util;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.location.DLocation;
import mk.plugin.dungeon3.dungeon.rewardreq.DRewardReq;
import mk.plugin.dungeon3.dungeon.statistic.DStatistic;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.turn.DTurn;
import mk.plugin.dungeon3.dungeon.turn.TSMob;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.rank.RankUtils;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Blocks;
import net.minecraft.server.v1_12_R1.PacketPlayOutBlockAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DGameUtils {
	
	private static Map<String, DStatus> statuses = Maps.newHashMap();
	
	public static boolean isPlaying(String id) {
		return statuses.containsKey(id);
	}
	
	public static boolean canStart(String id) {
		return !isPlaying(id);
	}
	
	public static Set<String> getOnlineDungeons() {
		return statuses.keySet();
	}
	
	public static DStatus getStatus(String id) {
		return statuses.getOrDefault(id, null);
	}
	
	public static void setStatus(String id, DStatus status) {
		statuses.put(id, status);
	}
	
	public static void removeStatus(String id) {
		statuses.remove(id);
	}
	
	public static boolean isLastTurn(String id, int turn) {
		return turn == DDataUtils.getDungeon(id).getTurns().size();
	}
	
	public static DTurn getTurn(String id, int turn) {
		Dungeon dungeon = DDataUtils.getDungeon(id);
		return dungeon.getTurns().get(turn - 1);
	}
	
	public static void checkAndRemove(Entity e) {
		if (e.hasMetadata("NPC") && !e.hasMetadata("Dungeon3")) return;
		if (e instanceof Player) {
//			((Player) e).teleport(Utils.getPlayerSpawn());
			return;
		}
		if (e instanceof LivingEntity || e instanceof Item) e.remove();
	}
	
	public static void broadcast(String id, String mess) {
		DStatus status = getStatus(id);
		status.getPlayers().forEach(uuid -> {
			Player p = Bukkit.getPlayer(uuid);
			p.sendMessage(mess);
		});
	}
	
	public static Location getCheckpoint(String id, int priority) {
		Dungeon d = DDataUtils.getDungeon(id);
		return d.getLocation(d.getCheckPoints().get(priority - 1)).getLocation();
	}
	
	public static String getStandingCheckpoint(String id, Location l) {
		Dungeon d = DDataUtils.getDungeon(id);
		for (String cp : d.getCheckPoints()) {
			DLocation check = d.getLocation(cp);
			if (check.getLocation().getWorld() == l.getWorld()) {
				if (check.getLocation().distance(l) <= check.getRadius()) return cp;
			}
		}
		return null;
	}
	
	public static boolean canChangeCheckpoint(String id, String from, String to) {
		if (from == null) return true;
		if (to == null) return false;
		if (from.equals(to)) return false;
		Dungeon d = DDataUtils.getDungeon(id);
		for (String cp : d.getCheckPoints()) {
			if (cp.equals(from)) return true;
			if (cp.equals(to)) return false;
		}
		return false;
	}
	
	public static int countMobs(DTurn turn) {
		int sum = 0;
		for (TSMob m : turn.getSpawn().getMobs()) {
			sum += m.getAmount();
		}
		return sum;
 	}
	
	public static int countMobs(Dungeon d) {
		int sum = 0;
		for (DTurn turn : d.getTurns()) {
			for (TSMob m : turn.getSpawn().getMobs()) {
				sum += m.getAmount();
			}
		}

		return sum;
 	}
	
	public static int countSlaves(DTurn turn) {
		return turn.getSpawn().getSlaves().size();
	}
	
	public static int countSlaves(Dungeon d) {
		int sum = 0;
		for (DTurn turn : d.getTurns()) {
			sum += countSlaves(turn);
		}
		return sum;
	}
	
	public static boolean canGetReward(String id, DStatistic s, DRewardReq rr) {
		if (rr.getKillCount() > s.getMobKilled()) return false;
		if (rr.getMaxDead() < s.getDead()) return false;
		if (rr.getSave() > s.getSlaveSaved()) return false;
		if (!(s.getListKilled().containsAll(rr.getKills()))) return false;
		if (!RankUtils.equalOrBetter(RankUtils.getRank(id, s), rr.getRank())) return false;
		return true;
	}
	
	public static void teleport(Player player, Location location) {
		player.setMetadata("dungeon-teleport", new FixedMetadataValue(MainDungeon3.get(), ""));
		player.teleport(location);
	}
	
	public static boolean isDungeonTeleport(Player player) {
		return player.hasMetadata("dungeon-teleport");
	}
	
	public static boolean checkTeleport(Player player) {
		if (isDungeonTeleport(player)) {
			player.removeMetadata("dungeon-teleport", MainDungeon3.get());
			return true;
		}
		return false;
	}
	
	public static String checkLocation(String id, Location l) {
		Dungeon d = DDataUtils.getDungeon(id);
		for (Entry<String, DLocation> dl : d.getLocations().entrySet()) {
			if (dl.getValue().getLocation().getWorld() == l.getWorld() && dl.getValue().getLocation().getBlock().getLocation().equals(l.getBlock().getLocation())) {
				return dl.getKey();
			}
		}
		return null;
	}
	
	public static void sendChestOpen(Player player, Location l) {
		BlockPosition pos = new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ());
		PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(pos, Blocks.CHEST, 1, 1);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
}
