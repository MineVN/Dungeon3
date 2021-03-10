package mk.plugin.dungeon3.dungeon.info;

import mk.plugin.dungeon3.configable.Configable;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DInfo extends Configable {
	
	private String name;
	private String desc;
	private List<String> worlds;
	
	public DInfo(FileConfiguration config, String path) {
		super(config, path);
	}
	
	public DInfo(String name, String desc, List<String> worlds) {
		this.name = name;
		this.desc = desc;
		this.worlds = worlds;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDesc() {
		return this.desc;
	}
	
	public List<String> getWorlds() {
		return this.worlds;
	}

	@Override
	public void load(FileConfiguration config, String path) {
		this.name = config.getString(path + ".name");
		this.desc = config.getString(path + ".desc");
		this.worlds = config.getStringList(path + ".worlds");
	}

	@Override
	public void save(FileConfiguration config, String path) {
		config.set(path + ".name", name);
		config.set(path + ".desc", desc);
		config.set(path + ".worlds", worlds);
	}
	
}
