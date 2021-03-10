package mk.plugin.dungeon3.dungeon.task;

import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.constant.DConstant;
import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DGameUtils;
import mk.plugin.dungeon3.dungeon.util.DSlaveUtils;
import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.slave.Slave;
import mk.plugin.dungeon3.slave.Slaves;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class DSlaveTask extends BukkitRunnable {
	
	private String slvID;
	private String dungeon;
	private LivingEntity slave;
	private Location loc;
	private DStatus status;
	private boolean isSpawned;
	private long lastScream;
	
	public DSlaveTask(String slvID, String dungeon, Location loc, DStatus status) {
		this.slvID = slvID;
		this.dungeon = dungeon;
		this.isSpawned = false;
		this.status = status;
		this.loc = loc;
		this.lastScream = System.currentTimeMillis();
		this.runTaskTimer(MainDungeon3.get(), 0, 5);
	}
	
	@Override
	public void run() {
		checkSpawn();
		checkLocation();
		checkValid();
		checkScream();
		playerLook();
	}
	
	public void checkScream() {
		if (System.currentTimeMillis() - lastScream < DConstant.SCREAM_COOLDOWN) return;
		lastScream = System.currentTimeMillis();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() == loc.getWorld()) {
				if (player.getLocation().distance(loc) < DConstant.SCREAM_RADIUS) {
					Lang.DUNGEON_SLAVE_SCREAM.send(player);
					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1, 1);
				}
			}
		}
	}
	
	public void checkSpawn() {
		if (isSpawned) return;
		boolean hasPlayerNear = false;
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld() == loc.getWorld()) {
				if (player.getLocation().distance(loc) < 30) {
					hasPlayerNear = true;
					break;
				}
			}
		}
		if (hasPlayerNear) {
			spawn();
		}
	}
	
	public void spawn() {
		Villager slave = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
		this.slave = slave;
		this.isSpawned = true;
		status.getTurnStatus().addSlaveToSave(slave);
		
		Slave slv = Slaves.get(this.slvID);
		slave.setAI(false);
		slave.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);;
		slave.setCustomName(slv.getName().replace("&", "§"));
		slave.setCustomNameVisible(true);
		slave.setProfession(Profession.values()[slv.getColor() - 1]);
		slave.setMetadata("NPC", new FixedMetadataValue(MainDungeon3.get(), "shit"));
		slave.setMetadata("Dungeon3", new FixedMetadataValue(MainDungeon3.get(), ""));
		
		Dungeon d = DDataUtils.getDungeon(dungeon);
		if (d.getOption().isMobGlow()) slave.setGlowing(true);
	}
	
	public void checkValid() {
		if (!isSpawned) return;
		if (!slave.isValid()) {
			status.getAllStatistic().addSlaveSaved(1);
			status.getTurnStatus().getStatistic().addSlaveSaved(1);
			UUID saver = DSlaveUtils.getSaver(slave);
			if (saver != null) {
				status.getStatistic(saver).addSlaveSaved(1);;
			}
			this.cancel();
		}
	}
	
	public void checkLocation() {
		if (!isSpawned) return;
		if (isFar()) slave.teleport(loc);
	}
	
	public boolean isFar() {
		if (!isSpawned) return false;
		if (DSlaveUtils.isSaved(this.slave)) return false;
		if (slave.getLocation().getWorld() != loc.getWorld()) return true;
		if (slave.getLocation().distance(loc) > 1) return true;
		return false;
	}
	
	public void playerLook() {
		if (!isSpawned) return;
		DStatus status = DGameUtils.getStatus(this.dungeon);
		UUID pn = null;
		double min = 9999;
		for (UUID uuid : status.getPlayers()) {
			Player p = Bukkit.getPlayer(uuid);
			if (p.getWorld() != this.loc.getWorld()) continue;
			if (p.getLocation().distanceSquared(loc) < min) {
				min = p.getLocation().distanceSquared(loc);
				pn = uuid;
			}
		}
		Player player = Bukkit.getPlayer(pn);
		Location l = slave.getLocation();
		l.setDirection(player.getLocation().subtract(l).toVector().normalize());
		try {
			slave.teleport(l);
		}
		catch (IllegalArgumentException e) {}
	}
	
	
	
	
	
}
