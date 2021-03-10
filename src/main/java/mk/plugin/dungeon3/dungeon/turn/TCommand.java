package mk.plugin.dungeon3.dungeon.turn;

import java.util.List;

public class TCommand {
	
	private List<String> onStart;
	private List<String> onLose;
	private List<String> onWin;
	
	public TCommand(List<String> onStart, List<String> onLose, List<String> onWin) {
		this.onStart = onStart;
		this.onLose = onLose;
		this.onWin = onWin;
	}
	
	public List<String> getStarts() {
		return this.onStart;
	}
	
	public List<String> getLoses() {
		return this.onLose;
	}
	
	public List<String> getWins() {
		return this.onWin;
	}
	
}
