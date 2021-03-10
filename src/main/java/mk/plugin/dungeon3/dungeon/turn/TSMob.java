package mk.plugin.dungeon3.dungeon.turn;

public class TSMob {
	
	private String mob;
	private int amount;
	private String l;
	
	public TSMob(String mob, int amount, String l) {
		this.mob = mob;
		this.amount = amount;
		this.l = l;
	}
	
	public TSMob(String s) {
		this.mob = s.split(";")[0];
		this.amount = Integer.valueOf(s.split(";")[1]);
		this.l = s.split(";")[2];
	}
	
	public String getMob() {
		return this.mob;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public String getLocation() {
		return this.l;
	}
	
	@Override
	public String toString() {
		return this.mob + ";" + this.amount + ";" + this.l;
	}
	
}
