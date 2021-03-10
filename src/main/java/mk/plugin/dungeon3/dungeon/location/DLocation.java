package mk.plugin.dungeon3.dungeon.location;

import mk.plugin.dungeon3.configable.Configable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class DLocation extends Configable {
	
	private double radius;
	private Location location;
	
	public DLocation(FileConfiguration config, String path) {
		super(config, path);
	}
	
	public DLocation(int radius, Location location) {
		this.radius = radius;
		this.location = location;
	}
	
	public double getRadius() {
		return this.radius;
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	@Override
	public void load(FileConfiguration config, String path) {	
		String s = config.getString(path);
		double r = Double.valueOf(s.split(";")[0]);
		World w = Bukkit.getWorld(s.split(";")[1]);
		double x = Double.valueOf(s.split(";")[2]);
		double y = Double.valueOf(s.split(";")[3]);
		double z = Double.valueOf(s.split(";")[4]);
		float p = Float.valueOf(s.split(";")[5]);
		float yw = Float.valueOf(s.split(";")[6]);
		
		this.radius = r;
		this.location = new Location(w, x, y, z, yw, p);
	}

	@Override
	public void save(FileConfiguration config, String path) {
		String s = radius + ";" + location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getPitch() + ";" + location.getYaw();;
		config.set(path, s);
	}
	
}
