package mk.plugin.dungeon3.dungeon.util;

import mk.plugin.dungeon3.lang.Lang;
import mk.plugin.dungeon3.main.MainDungeon3;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.UUID;

public class DSlaveUtils {
	
	public static boolean isSlave(Entity e) {
		if (e == null) return false;
		return (e.hasMetadata("NPC") && e.hasMetadata("Dungeon3"));
	}
	
	public static boolean isSaved(Entity e) {
		return e.hasMetadata("Dungeon3.slave-saved");
	}
	
	public static void setSaved(Entity e, UUID saver) {
		e.setMetadata("Dungeon3.slave-saved", new FixedMetadataValue(MainDungeon3.get(), saver.toString()));
	}
	
	public static UUID getSaver(Entity e) {
		if (!isSaved(e)) return null;
		return UUID.fromString(e.getMetadata("Dungeon3.slave-saved").get(0).asString());
	}
	
	public static void save(Player player, LivingEntity le) {
		setSaved(le, player.getUniqueId());
		Lang.DUNGEON_SLAVE_SAVED.send(player);
		player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
		new BukkitRunnable() {
			int c = 0;
			@Override
			public void run() {
				c++;
				if (c >= 5) {
					this.cancel();
					le.remove();
					return;
				}
				le.setAI(true);
				le.setVelocity(new Vector(0, 0.45, 0));
			}
		}.runTaskTimer(MainDungeon3.get(), 0, 15);
	}
	
}
