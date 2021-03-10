package mk.plugin.dungeon3.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import mk.plugin.dungeon3.dungeon.util.DPlayerUtils;
import mk.plugin.dungeon3.dungeon.util.DSlaveUtils;
import mk.plugin.dungeon3.main.MainDungeon3;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class EntityListener implements Listener {
	
	/*
	 * Villager damaged
	 */
	@EventHandler
	public void onDamaged(EntityDamageEvent e) {
		if (e.getEntity() instanceof Villager) {
			if (DSlaveUtils.isSlave(e.getEntity())) e.setCancelled(true);
		}
	}
	
	/*
	 * Villager interact
	 */
	@EventHandler
	public void onInteractVillager(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked() instanceof Villager) {
			Player player = e.getPlayer();
			if (DPlayerUtils.isInDungeon(player)) {
				e.setCancelled(true);
				Bukkit.getScheduler().runTask(MainDungeon3.get(), () -> {
					player.closeInventory();
				});
			}
		}
	}
	
	/*
	 * Dungeon mobs target each other
	 */
	
	@EventHandler
	public void onSlaveTargeted(EntityTargetLivingEntityEvent e) {
		LivingEntity target = e.getTarget();
		Entity targeter = e.getEntity();
		if (target == null || targeter == null) return;
		if (target.hasMetadata("Dungeon3") && targeter.hasMetadata("Dungeon3")) e.setCancelled(true);
	}
	
	@EventHandler
	public void onSlaveDamaged(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof LivingEntity)) return;
		LivingEntity target = (LivingEntity) e.getEntity();
		if (DSlaveUtils.isSlave(target)) e.setCancelled(true);
	}
	
	/*
	 * Cancel spawn if:
	 * 1. Has not "Dungeon3" tag
	 * 2. Is not player
	 * 3. Is not Mythicmob
	 */
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntity() instanceof LivingEntity) {
			LivingEntity le = (LivingEntity) e.getEntity();
			DDataUtils.getDungeons().values().forEach(dungeon -> {
				dungeon.getInfo().getWorlds().forEach(w -> {
					World world = Bukkit.getWorld(w);
					if (world == null) return;
					if (le.getWorld() == world) {
						Bukkit.getScheduler().runTaskLater(MainDungeon3.get(), () -> {
							if (le instanceof Player) return;
							if (!le.hasMetadata("Dungeon3") && le.getType() != EntityType.PLAYER) {
								if (le.getPassengers().size() == 0) {
									if (MythicMobs.inst().getMobManager().getMythicMobInstance(le) == null) le.remove();
								}
							}
						}, 10);
						return;
					}
				});
			});
		}
	}
	
}
