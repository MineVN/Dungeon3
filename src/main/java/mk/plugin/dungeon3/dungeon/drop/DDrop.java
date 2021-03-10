package mk.plugin.dungeon3.dungeon.drop;

import mk.plugin.dungeon3.configable.ConfigableListContent;

public class DDrop extends ConfigableListContent {

	private String mob;
	private String item;
	private int amount;
	private double chance;
	
	public DDrop(String s) {
		super(s);
	}
	
	public String getMobID() {
		return this.mob;
	}
	
	public String getItemID() {
		return this.item;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public double getChance() {
		return this.chance;
	}
	
	@Override
	public void load(String s) {
		String mob = s.split(";")[0];
		String item = s.split(";")[1];
		int amount = Integer.valueOf(s.split(";")[2]);
		double chance = 100;
		if (s.split(";").length > 3) chance = Double.valueOf(s.split(";")[3]);
		
		this.mob = mob;
		this.item = item;
		this.amount = amount;
		this.chance = chance;
	}

	@Override
	public String toString() {
		String s = mob + ";" + item + ";" + amount + ";" + chance;
		return s;
	}
	
}
