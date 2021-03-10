package mk.plugin.dungeon3.dungeon.manager;

import mk.plugin.dungeon3.command.Command;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.event.DungeonFinishEvent;
import mk.plugin.dungeon3.dungeon.rewardreq.DRewardReq;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.status.DungeonResult;
import mk.plugin.dungeon3.dungeon.turn.DTurn;
import mk.plugin.dungeon3.dungeon.turn.status.TStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.queue.DQueues;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DGameEnds {
	
	public static void winTurn(String id, int turn, BukkitRunnable task) {
		Dungeon d = DDataUtils.getDungeon(id);
		DStatus ds = DGameUtils.getStatus(id);
		DTurn t = DGameUtils.getTurn(id, turn);
		ds.setTurnStatus(new TStatus());
		
		// Chests
		t.getChests().forEach(chest -> {
			if (!Utils.rate(chest.getChance())) return;
			Location l = d.getLocation(chest.getLocation()).getLocation();
			l.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, l, 1, 0, 0, 0, 0);
			l.getWorld().playSound(l, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
			l.getBlock().setType(Material.CHEST);
		});
		
		// Win commands
		t.getCommand().getWins().forEach(cmd -> {
			ds.getPlayers().forEach(uuid -> {
				Player player = Bukkit.getPlayer(uuid);
				new Command(cmd).execute(player);
			});
		});
		
		// Check next
		if (DGameUtils.isLastTurn(id, turn)) {
			task.cancel();
			winDungeon(id);
		}
		else DGameStarts.startNextTurn(id);
	}
	
	public static void winDungeon(String id) {
		DStatus status = DGameUtils.getStatus(id);
		Dungeon d = DDataUtils.getDungeon(id);
		
		Lang.DUNGEON_WIN.broadcast();
		
		// Check players
		status.getPlayers().forEach(uuid -> {
//			Rank r = RankUtils.getRank(id, status.getStatistic(uuid));
			Player player = Bukkit.getPlayer(uuid);
			
			// Check requirements
			DRewardReq rr = d.getRewardReq();
			if (DGameUtils.canGetReward(id, status.getStatistic(uuid), rr)) {
				d.getRewards().forEach(s -> {
					new Command(s).execute(player);
				});
			}
		});
		
		// Do Finish
		doFinish(id, DungeonResult.WIN);
	}
	
	public static void loseTurn(String id, int turn) {
		DStatus ds = DGameUtils.getStatus(id);
		DTurn t = DGameUtils.getTurn(id, turn);
		
		// Lose commands
		t.getCommand().getLoses().forEach(cmd -> {
			ds.getPlayers().forEach(uuid -> {
				Player player = Bukkit.getPlayer(uuid);
				new Command(cmd).execute(player);
			});
		});
		
		// Lose dungeon
		loseDungeon(id); 
	}
	
	public static void loseDungeon(String id) {
		doFinish(id, DungeonResult.LOSE);
	}
	
	public static void doFinish(String id, DungeonResult result) {
		DStatus status = DGameUtils.getStatus(id);
		
		// Call event
		Bukkit.getPluginManager().callEvent(new DungeonFinishEvent(id, status, result));
		
		// Clear
		status.getBossBar().removeAll();
		status.getTasks().forEach(br -> {
			if (!br.isCancelled()) br.cancel();
		});
		List<UUID> remainPlayers = status.getPlayers();
		
		// Log
		System.out.println("[Dungeon3] Dungeon " + id + " finished (" + result.name() + ")");
		
		// Quitting task
		long start = System.currentTimeMillis();
		new BukkitRunnable() {
			@Override
			public void run() {
				List<Player> players = inDungeonFilter(remainPlayers, id);
				if (System.currentTimeMillis() - start >= 60000 || players.size() == 0) {
					DGameStarts.clearEntities(id);
					players.forEach(player -> {
						player.teleport(Utils.getPlayerSpawn());
					});
					DGameUtils.removeStatus(id);
					DQueues.newQueue(id);
					this.cancel();
					return;
				}
				int remain = new Long(60 - (System.currentTimeMillis() - start) / 1000 - 1).intValue();
				players.forEach(player -> {
					player.sendActionBar(Lang.DUNGEON_30_SECOUNDS_OUT.get().replace("%seconds%", remain + ""));
				});
			}
		}.runTaskTimer(MainDungeon3.get(), 0, 5);
	}
	
	private static List<Player> inDungeonFilter(List<UUID> uuids, String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		return uuids.stream()
				.filter(uuid -> d.getInfo().getWorlds().contains(Bukkit.getPlayer(uuid).getWorld().getName()))
				.map(Bukkit::getPlayer)
				.collect(Collectors.toList());
	}
	
	
	
}
