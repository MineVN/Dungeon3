package mk.plugin.dungeon3.slave;

import com.google.common.collect.Maps;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class Slaves {
	
	public static Map<String, Slave> slaves = Maps.newHashMap();
	
	public static void reload(FileConfiguration config) {
		config.getConfigurationSection("slave").getKeys(false).forEach(id -> {
			String name = config.getString("slave." + id + ".name");
			int color = config.getInt("slave." + id + ".color");
			Slave slv = new Slave(name, color);
			slaves.put(id, slv);
		});
	}
	
	public static Slave get(String id) {
		return slaves.getOrDefault(id, null);
	}
	
}
