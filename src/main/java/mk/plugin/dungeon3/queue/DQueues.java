package mk.plugin.dungeon3.queue;

import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.manager.DGameStarts;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.ticket.Tickets;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DQueues {
	
	public static final long TIME_WAIT_MILIS = 120000;
	public static final long TIME_DELAY_MILIS = 2700000;
	
	private static Map<String, DQueue> joins = new HashMap<String, DQueue> ();
	private static Map<String, Long> waitTime = new HashMap<String, Long> ();
	
	private static Map<String, Map<String, Long>> delayedPlayers = new HashMap<String, Map<String, Long>> ();
	
	private static Map<String, Long> dungeonCooldown = Maps.newHashMap();
	
	public static boolean isInQueue(Player player) {
		for (DQueue pj : joins.values()) {
			if (pj.getPlayers().contains(player.getUniqueId())) return true;
		}
		return false;
	}
	
	
	public static void newQueue(String id) {
		Dungeon dungeon = DDataUtils.getDungeon(id);
		joins.put(id, new DQueue(dungeon.getOption().getPlayer().getMin(), dungeon.getOption().getPlayer().getMax()));
		waitTime.put(id, -1l);
	}
	
	public static boolean hasQueue(String id) {
		return joins.containsKey(id);
	}
	
	public static DQueue getQueue(String id) {
		return joins.get(id);
	}
	
	public static boolean isDelayed(String id, Player player) {
		return getRemainDelaySeconds(id, player) > 0;
	}
	
	public static boolean isFull(String id) {
		if (joins.containsKey(id)) {
			if (joins.get(id).getPlayers().size() == DDataUtils.getDungeon(id).getOption().getPlayer().getMax()) return true;
		}
		return false;
	}
	
	public static int getRemainDelaySeconds(String id, Player player) {
		int time = 0;
		
		// Dungeon cooldown
		if (dungeonCooldown.containsKey(id)) {
			if (dungeonCooldown.get(id) > System.currentTimeMillis()) time = new Long((dungeonCooldown.get(id)  - System.currentTimeMillis()) / 1000).intValue();
		}
		
		// Player cooldown
		if (delayedPlayers.containsKey(player.getName())) {
			Map<String, Long> map = delayedPlayers.get(player.getName());
			if (map.containsKey(id)) {
				long remainL = map.get(id) - System.currentTimeMillis();
				if (remainL <= 0) {
					time = Math.max(time, new Long(TIME_DELAY_MILIS / 1000 - -1 * remainL / 1000).intValue());
				}
			}
		}
	
		return time;
	}
	
	public static boolean canStart(String id) {
		if (!joins.containsKey(id)) return false;
		if (waitTime.get(id) < 0) return false;
		return System.currentTimeMillis() >= waitTime.get(id) && getQueue(id).canStart();
	}
	
	public static boolean canAdd(String id, Player player) {
		if (!joins.containsKey(id)) return false;
		Dungeon dungeon = DDataUtils.getDungeon(id);
		
		// Level check
		if (player.getLevel() > dungeon.getOption().getLevel().getMax() || player.getLevel() < dungeon.getOption().getLevel().getMin()) return false;
		
		// Slot check
		if (!joins.get(id).canAdd(player)) return false;
		
		// Delay check
		if (!player.hasPermission("sky2.*")) {
			if (isDelayed(id, player)) return false;
		}

		
		return true;
	}
	
	public static void add(String id, Player player) {
		if (!canAdd(id, player)) return;
		DQueue pj = getQueue(id);
		pj.add(player);
		
		// Start timing
		if (waitTime.get(id) < 0) {
			waitTime.put(id, System.currentTimeMillis() + TIME_WAIT_MILIS);
		}
	}
	
	public static void remove(String id, Player player) {
		DQueue pj = getQueue(id);
		pj.remove(player);
		
		// Remove timing
		if (pj.getPlayers().size() == 0) {
			waitTime.put(id, -1l);
		}
	}
	
	public static void remove(String id) {
		DQueue pj = getQueue(id);
		pj.getPlayers().forEach(uuid -> {
			pj.remove(Bukkit.getPlayer(uuid));
		});
		waitTime.put(id, -1l);
	}
	
	
	public static boolean isInQueue(String id, Player player) {
		DQueue pj = getQueue(id);
		if (pj == null) return false;
		return pj.getPlayers().contains(player.getUniqueId());
	}
	
	public static void checkAll() {
		Set<String> ids = new HashSet<String> (joins.keySet());
		ids.forEach(id -> {
			if (canStart(id)) {
				// Start
				Bukkit.getScheduler().runTask(MainDungeon3.get(), () -> {
					DGameStarts.startDungeon(id, getQueue(id).getPlayers());
					doStart(id);
				});
			}
		});
	}
	
	public static void doStart(String id) {
		// Delay player
		joins.get(id).getPlayers().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			Map<String, Long> map = new HashMap<String, Long> ();
			if (delayedPlayers.containsKey(player.getName())) {
				map = delayedPlayers.get(player.getName());
			}
			map.put(id, System.currentTimeMillis());
			delayedPlayers.put(player.getName(), map);
		});
		
		// Delay dungeon
		dungeonCooldown.put(id, System.currentTimeMillis() + DDataUtils.getDungeon(id).getOption().getCooldown() * 1000);
		
		// Remove others
		joins.remove(id);
		waitTime.remove(id);
	}
	
	public static int getSecondsRemain(String id) {
		int seconds = new Long((waitTime.get(id) - System.currentTimeMillis()) / 1000).intValue(); 
		return Math.max(seconds, 0);
	}
	
	public static void doPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Set<String> ids = new HashSet<String> (joins.keySet());
		for (String id : ids) {
			if (isInQueue(id, player)) {
				remove(id, player);
			}
		}
	}
	
	public static DQueueStatus getStatus(String id, Player player) {
		Dungeon d = DDataUtils.getDungeon(id);
		if (!hasQueue(id) && DGameUtils.isPlaying(id)) return DQueueStatus.PLAYING;
		if (canAdd(id, player)) {
			// Check ticket
			if (d.getOption().isTicketRequired() && !Tickets.contains(player, 1, id)) return DQueueStatus.CANT_JOIN;
			return DQueueStatus.CAN_JOIN;
		}
		if (isInQueue(id, player)) return DQueueStatus.WAITING;
		if (isDelayed(id, player)) return DQueueStatus.DELAY;
		return DQueueStatus.CANT_JOIN;
	}
	
}
