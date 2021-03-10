package mk.plugin.dungeon3.dungeon.task;

import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.manager.DGamePlays;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DPlayerTask extends BukkitRunnable {
	
	private Player player;
	private String dungeon;
	private DStatus status;
	
	public DPlayerTask(Player player, String dungeon, DStatus status) {
		this.player = player;
		this.dungeon = dungeon;
		this.status = status;
		this.runTaskTimer(MainDungeon3.get(), 0, 5);
	}
	
	@Override
	public void run() {
		if (!checkResult()) return;
		if (!checkOnline()) return;
		if (!checkPlace()) return;
		if (!checkDead()) return;
		
		checkCheckpoint();
	}
	
	public boolean checkResult() {
		if (!status.isPlaying()) {
			this.cancel();
			return false;
		}
		return true;
	}
	
	public boolean checkOnline() {
		if (!player.isOnline()) {
			DGamePlays.kick(player, false);
			this.cancel();
			return false;
		}
		return true;
	}
	
	public void checkCheckpoint() {
		Location l = player.getLocation();
		String to = DGameUtils.getStandingCheckpoint(dungeon, l);
		String from = status.getCheckpoint();
		if (DGameUtils.canChangeCheckpoint(dungeon, from, to)) {
			status.setCheckpoint(to);
			Lang.DUNGEON_NEW_CHECKPOINT.broadcast(status.getPlayers());;
		}
	}
	
	public boolean checkPlace() {
		Dungeon d = DDataUtils.getDungeon(dungeon);
		if (!d.getInfo().getWorlds().contains(player.getWorld().getName())) {
			DGamePlays.kick(player, false);
			this.cancel();
			return false;
		}
		return true;
	}
	
	public boolean checkDead() {
		if (player.isDead()) {
			player.spigot().respawn();
			return false;
		}
		Dungeon d = DDataUtils.getDungeon(dungeon);
		Material m = player.getLocation().getBlock().getType();
		if (d.getRule().isLavaDead()) {
			if (m == Material.STATIONARY_LAVA || m == Material.LAVA) {
				DGamePlays.dead(player, true);
				return false;
			}
		}
		if (d.getRule().isWaterDead()) {
			if (m == Material.STATIONARY_WATER || m == Material.WATER) {
				DGamePlays.dead(player, true);
				return false;
			}
		}
		if (player.getLocation().getY() <= d.getRule().getYDead()) {
			DGamePlays.dead(player, true);
			return false;
		}
		
		
		return true;
	}

	
}
