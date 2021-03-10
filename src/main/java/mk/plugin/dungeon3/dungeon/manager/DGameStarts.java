package mk.plugin.dungeon3.dungeon.manager;

import com.google.common.collect.Lists;
import mk.plugin.dungeon3.command.Command;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.block.DBlock;
import mk.plugin.dungeon3.dungeon.event.DungeonStartEvent;
import mk.plugin.dungeon3.dungeon.location.DLocation;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.task.DGameTask;
import mk.plugin.dungeon3.dungeon.task.DMobTask;
import mk.plugin.dungeon3.dungeon.task.DPlayerTask;
import mk.plugin.dungeon3.dungeon.task.DSlaveTask;
import mk.plugin.dungeon3.dungeon.turn.DTurn;
import mk.plugin.dungeon3.dungeon.turn.status.TStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class DGameStarts {
	
	public static void startDungeon(String id, List<UUID> players) {
		// Check offline
		Lists.newArrayList(players).forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) {
				players.remove(uuid);
				return;
			}
			player.setFlying(false);
		});
		
		// Init
		clearEntities(id);
		spawnBlocks(id);
		doChests(id);
		
		// Status
		BossBar bb = createTimingBar(id);
		createStatus(id, players, bb);
		
		// Teleport
		firstCheckpointTeleport(id);
		
		// Manager
		createTask(id);
		createPlayerTasks(id);
		
		// Turn
		startTurn(id, 1);
		
		// Event
		Bukkit.getPluginManager().callEvent(new DungeonStartEvent(id));
	}
	
	public static void startNextTurn(String id) {
		DStatus status = DGameUtils.getStatus(id);
		int turn = status.getTurn();
		
		// Check if end
		if (DGameUtils.isLastTurn(id, turn)) {
			DGameEnds.winDungeon(id);
		}
		else startTurn(id, turn + 1);
	}
	
	public static void startTurn(String id, int tI) {
		DTurn turn = DGameUtils.getTurn(id, tI);
		DStatus ds = DGameUtils.getStatus(id);
		ds.setTurn(tI);
		ds.setTurnStatus(new TStatus());
		
		// Run commands
		runStartCommands(id, tI);
		
		// Spawn
		Bukkit.getScheduler().runTaskLater(MainDungeon3.get(), () -> {
			if (!DGameUtils.isPlaying(id)) return;
			spawnBlockBreak(id, tI);
			spawnMobs(id, tI);
			spawnSlaves(id, tI);
		}, turn.getSpawn().getDelay());
	}
	
	
	// For dungeon
	
	public static void clearEntities(String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		d.getInfo().getWorlds().forEach(w -> {
			World world = Bukkit.getWorld(w);
			if (world != null) {
				world.getEntities().forEach(e -> {
					DGameUtils.checkAndRemove(e);
				});
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private static void spawnBlocks(String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		d.getBlocks().forEach((i, b) -> {
			Location l = b.getLocation();
			l.getBlock().setType(b.getMaterial());
			l.getBlock().setData((byte) b.getBlockDamage());
		});
	}
	
	private static void createStatus(String id, List<UUID> players, BossBar bb) {
		DStatus status = new DStatus(players, bb);
		TStatus ts = new TStatus();
		status.setTurnStatus(ts);
		status.setBossBar(bb);
		players.forEach(uuid -> {
			bb.addPlayer(Bukkit.getPlayer(uuid));
		});
		DGameUtils.setStatus(id, status);
	}
	
	private static void firstCheckpointTeleport(String id) {
		DStatus status = DGameUtils.getStatus(id);
		Dungeon d = DDataUtils.getDungeon(id);
		status.getPlayers().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			Location l = d.getLocation(d.getCheckPoints().get(0)).getLocation();
			DGameUtils.teleport(player, l);
		});
	}
	
	private static void createPlayerTasks(String id) {
		DStatus status = DGameUtils.getStatus(id);
		status.getPlayers().forEach(uuid -> {
			Player player = Bukkit.getPlayer(uuid);
			status.addTask(new DPlayerTask(player, id, status));
//			status.addTask(new DMoneyCoinTask(player));
		});
	}
	
	private static BossBar createTimingBar(String id) {
		Dungeon dungeon = DDataUtils.getDungeon(id);
		BossBar bb = Bukkit.createBossBar("§c§l" + dungeon.getInfo().getName() + " §f§l" + Utils.getFormat(dungeon.getOption().getMaxTime()), BarColor.GREEN, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
		return bb;
	}
	
	private static void createTask(String id) {
		DStatus status = DGameUtils.getStatus(id);
		BukkitRunnable br = new DGameTask(id, DGameUtils.getStatus(id), System.currentTimeMillis());
		status.addTask(br);
	}
	
	// For turn
	
	private static void runStartCommands(String id, int turn) {
		DTurn t = DGameUtils.getTurn(id, turn);
		DStatus status = DGameUtils.getStatus(id);
		t.getCommand().getStarts().forEach(s -> {
			status.getPlayers().forEach(uuid -> {
				Player player = Bukkit.getPlayer(uuid);
				new Command(s).execute(player);
			});
		});
	}
	
	private static void spawnBlockBreak(String id, int turn) {
		DTurn t = DGameUtils.getTurn(id, turn);
		Dungeon d = DDataUtils.getDungeon(id);
		t.getSpawn().getBlockBreaks().forEach(bid -> {
			DBlock b = d.getBlocks().get(bid);
			b.getLocation().getBlock().setType(Material.AIR);
			b.getLocation().getWorld().spawnParticle(Particle.EXPLOSION_LARGE, b.getLocation(), 1, 0, 0, 0, 0);
			b.getLocation().getWorld().playSound(b.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
		});
	}
	
	private static void spawnMobs(String id, int turn) {
		DTurn t = DGameUtils.getTurn(id, turn);
		DStatus status = DGameUtils.getStatus(id);
		Dungeon d = DDataUtils.getDungeon(id);
		t.getSpawn().getMobs().forEach(m -> {
			String mob = m.getMob();
			int amount = m.getAmount();
			
			int c = 0;
			while (c < amount) {
				c++;
				DLocation dl = d.getLocation(m.getLocation());
				Location l = Utils.random(dl.getLocation(), dl.getRadius());
				BukkitRunnable br = new DMobTask(id, mob, l, DGameUtils.getStatus(id));
				status.addTask(br);
			}
		});
	}
	
	private static void spawnSlaves(String id, int turn) {
		DTurn t = DGameUtils.getTurn(id, turn);
		DStatus status = DGameUtils.getStatus(id);
		Dungeon d = DDataUtils.getDungeon(id);
		t.getSpawn().getSlaves().forEach(slv -> {
			DLocation dl = d.getLocation(slv.getLocation());
			Location l = dl.getLocation();
			BukkitRunnable br = new DSlaveTask(slv.getSlave(), id, l, DGameUtils.getStatus(id));
			status.addTask(br);
		});
	}
	
	private static void doChests(String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		d.getTurns().forEach(turn -> {
			turn.getChests().forEach(chest -> {
				d.getLocation(chest.getLocation()).getLocation().getBlock().setType(Material.AIR);
			});
		});
	}
	
}
