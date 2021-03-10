package mk.plugin.dungeon3.dungeon.util;

import com.google.common.collect.Maps;
import io.lumine.xikage.mythicmobs.MythicMobs;
import kdvn.facrpg.shopitemgui.itemdatabase.ItemManager;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.drop.DDrop;
import mk.plugin.dungeon3.dungeon.turn.DTurn;
import mk.plugin.dungeon3.dungeon.turn.TSMob;
import mk.plugin.dungeon3.main.MainDungeon3;
import mk.plugin.dungeon3.slave.Slaves;
import mk.plugin.dungeon3.util.Utils;
import mk.plugin.niceshops.storage.ItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class DDataUtils {
	
	private static Map<String, Dungeon> dungeons = Maps.newHashMap();

	public static void loadAll(FileConfiguration config) {
		dungeons = Maps.newHashMap();
		File folder = new File(MainDungeon3.get().getDataFolder(), "dungeons");
		if (folder.exists()) {
			for (File file : folder.listFiles()) {
				String id = file.getName().replace(".yml", "");
				String path = "";
				FileConfiguration dc = YamlConfiguration.loadConfiguration(file);
				dungeons.put(id, new Dungeon(dc, path));
			}
		}
		else config.getConfigurationSection("dungeon").getKeys(false).forEach(id -> {
			String path = "dungeon." + id;
			dungeons.put(id, new Dungeon(config, path));
		});
	}
	
	public static void save(String id) {
		Dungeon d = getDungeon(id);
		if (d == null) return;
		File folder = new File(MainDungeon3.get().getDataFolder(), "dungeons");
		if (!folder.exists()) folder.mkdirs();
		
		File file = new File(MainDungeon3.get().getDataFolder() + "//dungeons//" + id + ".yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		d.save(config, "");
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveAll() {
		dungeons.keySet().forEach(id -> save(id));
	}
	
	public static Dungeon getDungeon(String id) {
		return dungeons.getOrDefault(id, null);
	}
	
	public static Map<String, Dungeon> getDungeons() {
		return dungeons;
	}
	
	public static boolean checkProblem(String id) {
		if (getDungeon(id) == null) {
			Utils.log("What the fuck dungeon \"" + id + "\"");
			return false;
		}
		return checkProblem(getDungeon(id));
	}
	
	public static boolean checkProblem(Dungeon d) {
		// Checkpoint right location ?
		for (String cp : d.getCheckPoints()) {
			if (!d.getLocations().containsKey(cp)) {
				Utils.log("[Dungeon3] Checkpoint \"" + cp + "\" is wrong!");
				return false;
			}
		}
		
		// Block break right block ?
		for (DTurn t : d.getTurns()) {
			for (String bb : t.getSpawn().getBlockBreaks()) {
				if (!d.getBlocks().containsKey(bb)) {
					Utils.log("[Dungeon3] Block break \"" + bb + "\" is wrong!");
					return false;
				}
			}
		}
		
		// Slave rigt id ?
		for (DTurn t : d.getTurns()) {
			for (String bb : t.getSpawn().getSlaves().stream().map(slv -> slv.getSlave()).collect(Collectors.toList())) {
				if (!Slaves.slaves.containsKey(bb)) {
					Utils.log("[Dungeon3] Slave spawn \"" + bb + "\" is wrong!");
					return false;
				}
			}
		}
		
		// Slave spawn right location ?
		for (DTurn t : d.getTurns()) {
			for (String bb : t.getSpawn().getSlaves().stream().map(slv -> slv.getLocation()).collect(Collectors.toList())) {
				if (!d.getLocations().containsKey(bb)) {
					Utils.log("[Dungeon3] Slave spawn \"" + bb + "\" is wrong!");
					return false;
				}
			}
		}
		// Mob spawn right location ?
		for (DTurn t : d.getTurns()) {
			for (TSMob tsm : t.getSpawn().getMobs()) {
				if (!d.getLocations().containsKey(tsm.getLocation())) {
					Utils.log("[Dungeon3] Mob spawn \"" + tsm.getLocation() + "\" is wrong!");
					return false;
				}
			}
		}
		
		// Mob spawn right id ?
		for (DTurn t : d.getTurns()) {
			for (TSMob tsm : t.getSpawn().getMobs()) {
				String id = tsm.getMob();
				if (!MythicMobs.inst().getMobManager().getMobNames().contains(id)) {
					Utils.log("[Dungeon3] Mob id \"" + tsm.getMob() + "\" is wrong!");
					return false;
				}
			}
		}
		
		// Drop right id and mob ?
		for (DDrop drop : d.getDrops()) {
			String item = drop.getItemID();
			String mob = drop.getMobID();
			if (!MythicMobs.inst().getMobManager().getMobNames().contains(mob) && !mob.equalsIgnoreCase("*")) {
				Utils.log("[Dungeon3] Mob id \"" + mob + "\" of drop is wrong!");
				return false;
			}
			
			// Hook
			if (Bukkit.getPluginManager().isPluginEnabled("Shops")) {
				if (!ItemManager.getItems().contains(item)) {
					Utils.log("[Dungeon3] Item id \"" + item + "\" of drop is wrong!");
					return false;
				}
			}
			else if (Bukkit.getPluginManager().isPluginEnabled("NiceShops")) {
				if (!ItemStorage.getItemStacks().containsKey(item)) {
					Utils.log("[Dungeon3] Item id \"" + item + "\" of drop is wrong!");
					return false;
				}
			}
			

		}
		
		return true;
	}
	
}
