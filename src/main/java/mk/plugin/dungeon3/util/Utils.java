package mk.plugin.dungeon3.util;

import com.earth2me.essentials.spawn.EssentialsSpawn;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class Utils {
	
	public static Entity getNearest(Location l, double r, EntityType type) {
		double min = Double.MAX_VALUE;
		Entity entity = null;
		for (Entity e : l.getWorld().getNearbyEntities(l, r, r, r)) {
			if (e.getType() == type)  {
				double d = e.getLocation().distance(l);
				if (d < min) {
					min = d;
					entity = e;
				}
			}
		}
		return entity;
	}
	
	
	public static int countNearBy(Location l, double r, EntityType type) {
		int c = 0;
		for (Entity e : l.getWorld().getNearbyEntities(l, r, r, r)) {
			if (e.getType() == type) c++;
		}
		return c;
	}
	
	public static String formatMinute(long miliTime) {
		return (miliTime / 60000) + "m " + ((miliTime % 60000) / 1000) + "s";
	}
	
	public static String format(long miliTime) {
		return (miliTime / (3600000 * 24)) + "d " + ((miliTime % (3600000 * 24)) / 3600000) + "h " + ((miliTime % 3600000) / 60000) + "m " + ((miliTime % 60000) / 1000) + "s";
	}
	
	public static List<String> toList(String s, int length, String start) {
		List<String> result = new ArrayList<String>();
		if (s == null)
			return result;
		if (!s.contains(" ")) {
			result.add(s);
			return result;
		}

		String[] words = s.split(" ");
		int l = 0;
		String line = "";
		for (int i = 0; i < words.length; i++) {
			l += words[i].length();
			if (l > length) {
				result.add(line.substring(0, line.length() - 1));
				l = words[i].length();
				line = "";
				line += words[i] + " ";
			} else {
				line += words[i] + " ";
			}
		}

		if (!line.equalsIgnoreCase(" "))
			result.add(line);

		for (int i = 0; i < result.size(); i++) {
			result.set(i, start + result.get(i));
		}

		return result;
	}
	
	@SuppressWarnings("deprecation")
	public static short getColorDurability(DyeColor color, Material material) {
		if (material == Material.BANNER) return new Integer(15 - color.getWoolData()).shortValue();
		return color.getWoolData();
	}
	
	public static Location getRandomSpawnLocation(Location center, double radius) {
		if (radius == 0) return center;
		double x = center.getX() + new Random().nextInt(2 * new Double(radius).intValue()) - radius;
		double z = center.getZ() + new Random().nextInt(2 * new Double(radius).intValue()) - radius;
		Location newL = new Location(center.getWorld(), x, center.getY(), z);

		return getSafeLocation(newL);
	}
	
	public static Location getSafeLocation(Location newL) {
		while (newL.clone().add(0, -2, 0).getBlock().getType() != Material.AIR || newL.clone().getBlock().getType() != Material.AIR) {
			newL.setY(newL.getY() + 1);
		}
		return newL;
	}
	
	public static Location getPlayerSpawn() {
		return ((EssentialsSpawn) Bukkit.getPluginManager().getPlugin("EssentialsSpawn")).getSpawn("default");
	}
	
	public static String getFormat(int second) {
		String s = "";
		int minute = second / 60;
		s = minute < 10 ? "0" + minute : minute + "";
		s += ":";
		
		int du = second % 60;
		s += du < 10 ? "0" + du : du + "";
		return s;
	}
	
	public static Location random(Location loc, double radius) {
		Location cl = loc.clone();
		if (radius > 0) {
			cl.setX(cl.getX() + Utils.random(-1 * radius, radius));
			cl.setZ(cl.getZ() + Utils.random(-1 * radius, radius));
		}
		
		return cl;
	}
	
	public static double random(double min, double max) {
		return (new Random().nextInt(new Double((max - min) * 1000).intValue()) + min * 1000) / 1000;
	}
	
	public static boolean rate(double chance) {
		if (chance >= 100) return true;
		double rate = chance * 100;
		int random = new Random().nextInt(10000);
		if (random < rate) {
			return true;
		} else return false;
	}
	
	public static String c(String s) {
		return color(s);
	}
	
	public static String color(String s) {
		return s.replace("&", "§");
	}
	
	public static Object notNullDefault(Object checkO, Object defaultO) {
		return checkO == null ? defaultO : checkO;
	}
	
	public static List<String> from(String s, String split) {
		if (s.equals("")) return Lists.newArrayList();
		return Lists.newArrayList(s.split(split));
	}
	
	public static String to(List<String> list, String split) {
		String s = "";
		if (list.size() == 0) return s;
		for (String m : list) {
			s += m + split;
		}
		s = s.substring(0, s.length() - 1);
		return s;
	}
	
	public static void log(String log) {
		System.out.println(log);
	}
	
	
	public static void log(String log, Map<String, String> placeholders) {
		for (Entry<String, String> e : placeholders.entrySet()) {
			log = log.replace(e.getKey(), e.getValue());
		}
		log(log);
	}
	
}
