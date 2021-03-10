package mk.plugin.dungeon3.dungeon.util;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import org.bukkit.entity.Player;

import java.util.Map;

public class DPlayerUtils {
	
	public static Map<String, Long> godList = Maps.newHashMap();
	
	public static boolean isGod(Player player) {
		if  (godList.containsKey(player.getName())) {
			boolean isGod = godList.get(player.getName()) > System.currentTimeMillis();
			if (!isGod) godList.remove(player.getName());
			return isGod;
		}
		return false;
	}
	
	public static void setGod(Player player, int tick) {
		godList.put(player.getName(), System.currentTimeMillis() + tick * 1000 / 20);
	}
	
	public static String getCurrentDungeon(Player player) {
		for (String id : DGameUtils.getOnlineDungeons()) {
			if (DGameUtils.getStatus(id).getPlayers().contains(player.getUniqueId())) return id;
		}
		return null;
	}
	
	public static DStatus getStatus(Player player) {
		return DGameUtils.getStatus(DPlayerUtils.getCurrentDungeon(player));
	}
	
	public static Map<String, String> getPlaceholders(Player player) {
		Map<String, String> map = Maps.newHashMap();
		map.put("%player%", player.getName());
		String id = DPlayerUtils.getCurrentDungeon(player);
		if (id != null) {
			map.put("%dungeon%", DDataUtils.getDungeon(id).getInfo().getName());
		}
		else map.put("%dungeon%", "dungeon");
		
		
		return map;
	}
	
	public static boolean isInDungeon(Player player) {
		return getCurrentDungeon(player) != null;
	}
	
	public static boolean isInDungeon(Player player, String id) {
		DStatus status = DGameUtils.getStatus(id);
		if (status == null) return false;
		return status.getPlayers().contains(player.getUniqueId());
	}
	
	public static boolean isInDungeonWorld(Player player) {
		for (Dungeon d : DDataUtils.getDungeons().values()) {
			if (d.getInfo().getWorlds().contains(player.getWorld().getName())) return true;
		}
		return false;
	}
	
	public static String getDungeonPlayerStandingOn(Player player) {
		for (String id : DDataUtils.getDungeons().keySet()) {
			Dungeon d = DDataUtils.getDungeon(id);
			if (d.getInfo().getWorlds().contains(player.getWorld().getName())) return id;
		}
		return null;
	}
	
}
