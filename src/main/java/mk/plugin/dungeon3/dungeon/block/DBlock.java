package mk.plugin.dungeon3.dungeon.block;

import mk.plugin.dungeon3.configable.Configable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class DBlock extends Configable {
	
	private Material m;
	private int d;
	private Location location;
	
	public DBlock(FileConfiguration config, String path) {
		super(config, path);
	}
	
	public DBlock(Material material, int durability, Location location) {
		this.m = material;
		this.d = durability;
		this.location = location;
	}
	
	public Material getMaterial() {
		return this.m;
	}
	
	public int getBlockDamage() {
		return this.d;
	}
	
	public Location getLocation() {
		return this.location;
	}

	@Override
	public void load(FileConfiguration config, String path) {
		String s = config.getString(path);
		
		Material m = Material.valueOf(s.split(";")[0]);
		int d = Integer.valueOf(s.split(";")[1]);
		World w = Bukkit.getWorld(s.split(";")[2]);
		double x = Double.valueOf(s.split(";")[3]);
		double y = Double.valueOf(s.split(";")[4]);
		double z = Double.valueOf(s.split(";")[5]);
		
		this.m = m;
		this.d = d;
		this.location = new Location(w, x, y, z);
	}

	@Override
	public void save(FileConfiguration config, String path) {
		String s = m.name() + ";" + d + ";" + location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
		config.set(path, s);
	}
	
}
