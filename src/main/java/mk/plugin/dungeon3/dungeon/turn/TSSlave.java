package mk.plugin.dungeon3.dungeon.turn;

public class TSSlave {
	
	private String slv;
	private String loc;
	
	public TSSlave(String slv, String loc) {
		this.slv = slv;
		this.loc = loc;
	}
	
	public TSSlave(String s) {
		this.slv = s.split(";")[0];
		this.loc = s.split(";")[1];
	}
	
	public String getSlave() {
		return this.slv;
	}
	
	public String getLocation() {
		return this.loc;
	}
	
}
