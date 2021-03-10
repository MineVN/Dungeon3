package mk.plugin.dungeon3.dungeon.turn;

import mk.plugin.dungeon3.rank.Rank;

import java.util.List;

public class TChest {
	
	private String loc;
	private Rank rank;
	private double chance;
	private List<String> commands;
	
	public TChest(String loc, Rank rank, double chance, List<String> commands) {
		this.loc = loc;
		this.rank = rank;
		this.chance = chance;
		this.commands = commands;
	}
	
	public double getChance() {
		return this.chance;
	}
	
	public Rank getRank() {
		return this.rank;
	}
	
	public String getLocation() {
		return this.loc;
	}
	
	public List<String> getCommands() {
		return this.commands;
	}
	
}
