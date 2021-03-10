package mk.plugin.dungeon3.dungeon.statistic;

import com.google.common.collect.Sets;

import java.util.Set;

public class DStatistic {
	
	private int mobKilled;
	private int slaveSaved;
	private int timeSurvived;
	private int dead;
	
	private Set<String> killList;
	
	public DStatistic() {
		this.mobKilled = 0;
		this.slaveSaved = 0;
		this.timeSurvived = 0;
		this.dead = 0;
		this.killList = Sets.newHashSet();
	}
	
	public int getMobKilled() {
		return this.mobKilled;
	}
	
	public void setMobKilled(int value) {
		this.mobKilled = value;
	}
	
	public void addMobKilled(int value) {
		this.setMobKilled(this.getMobKilled() + value);
	}
	
	public int getSlaveSaved() {
		return this.slaveSaved;
	}
	
	public Set<String> getListKilled() {
		return this.killList;
	}
	
	public int getDead() {
		return this.dead;
	}
	
	public void setSlaveSaved(int value) {
		this.slaveSaved = value;
	}
	
	public void addSlaveSaved(int value) {
		this.setSlaveSaved(this.getSlaveSaved() + value);
	}
	
	public int getTimeSurvived() {
		return this.timeSurvived;
	}
	
	public void setTimeSurvived(int value) {
		this.timeSurvived = value;
	}
	
	public void addKilled(String id) {
		this.killList.add(id);
	}
	
	public void setDead(int value) {
		this.dead = value;
	}
	
	public void addDead(int value) {
		this.setDead(this.getDead() + value);
	}
	
}
