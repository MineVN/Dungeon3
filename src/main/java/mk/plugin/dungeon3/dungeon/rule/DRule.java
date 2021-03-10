package mk.plugin.dungeon3.dungeon.rule;

import mk.plugin.dungeon3.configable.Configable;
import org.bukkit.configuration.file.FileConfiguration;

public class DRule extends Configable {
	
	private boolean lavaDead;
	private boolean waterDead;
	private int respawn;
	private boolean pvp;
	private int yDead;
	
	public DRule(FileConfiguration config, String path) {
		super(config, path);
	}
	
//	public DRule(boolean lavaDead, boolean waterDead, int respawn, boolean pvp) {
//		this.lavaDead = lavaDead;
//		this.waterDead = waterDead;
//		this.respawn = respawn;
//		this.pvp = pvp;
//	}
//	
	public boolean isLavaDead() {
		return this.lavaDead;
	}
	
	public boolean isWaterDead() {
		return this.waterDead;
	}
	
	public int getRespawnTime() {
		return this.respawn;
	}
	
	public boolean getPvP() {
		return this.pvp;
	}

	public int getYDead() {
		return this.yDead;
	}
	
	@Override
	public void load(FileConfiguration config, String path) {
		this.respawn = config.getInt(path + ".respawn");
		this.lavaDead = config.getBoolean(path + ".lava-dead");
		this.waterDead = config.getBoolean(path + ".water-dead");
		this.pvp = config.getBoolean(path + ".pvp");
		if (config.contains(path + ".y-dead")) {
			this.yDead = config.getInt(path + ".y-dead");
		} else this.yDead = -64;
	}

	@Override
	public void save(FileConfiguration config, String path) {
		config.set(path + ".respawn", this.respawn);
		config.set(path + ".lava-dead", this.lavaDead);
		config.set(path + ".water-dead", this.waterDead);
		config.set(path + ".y-dead", this.yDead);
		config.set(path + ".pvp", this.pvp);
	}
	
}
