package mk.plugin.dungeon3.rank;

import org.bukkit.configuration.file.FileConfiguration;

public enum Rank {
	
	F("§7", 1), 
	D("§9", 2), 
	C("§a", 3), 
	B("§c", 4), 
	A("§6", 5), 
	S("§e", 6);
	
	private double kill;
	private double save;
	private double time;
	private double dead;
	
	private int value;
	
	private String color;
	
	private Rank(String color, int value) {
		this.color = color;
		this.value = value;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public double getKillPercent() {
		return this.kill;
	}
	
	public double getSavePercent() {
		return this.save;
	}
	
	public double getTimePercent() {
		return this.time;
	}
	
	public double getDeadPercent() {
		return this.dead;
	}
	
	public void load(FileConfiguration config) {
		this.kill = config.getDouble("rank." + this.name() + ".kill");
		this.save = config.getDouble("rank." + this.name() + ".save");
		this.time = config.getDouble("rank." + this.name() + ".time");
		this.dead = config.getDouble("rank." + this.name() + ".dead");
	}
	
	public static void loadAll(FileConfiguration config) {
		for (Rank r : values()) {
			r.load(config);
		}
	}
	
}
