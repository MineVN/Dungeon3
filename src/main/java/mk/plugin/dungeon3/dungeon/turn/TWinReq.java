package mk.plugin.dungeon3.dungeon.turn;

import mk.plugin.dungeon3.util.Utils;

import java.util.List;

public class TWinReq {
	
	private int save;
	private List<String> kills;
	private boolean killAll;
	
	public TWinReq(int save, List<String> kills, boolean killAll) {
		this.save = save;
		this.kills = kills;
		this.killAll = killAll;
	}
	
	public int getSlaveSave() {
		return this.save;
	}
	
	public List<String> getKills() {
		return this.kills;
	}
	
	public String killsToString() {
		return Utils.to(this.kills, ";");
	}
	
	public boolean isKillAll() {
		return this.killAll;
	}
	
}
