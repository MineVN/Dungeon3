package mk.plugin.dungeon3.util;

public class MinMax {
	
	private int min;
	private int max;
	
	public MinMax(String s) {
		this.min = Integer.valueOf(s.split(":")[0]);
		this.max = Integer.valueOf(s.split(":")[1]);
	}
	
	public MinMax(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public int getMax() {
		return this.max;
	}
	
	@Override
	public String toString() {
		return this.min + ":" + this.max;
	}
	
}
