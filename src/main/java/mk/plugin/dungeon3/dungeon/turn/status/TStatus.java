package mk.plugin.dungeon3.dungeon.turn.status;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.dungeon3.dungeon.statistic.DStatistic;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.Map;

public class TStatus {
	
	private Map<LivingEntity, String> mobToKills;
	private List<LivingEntity> slaveToSaves;
	private DStatistic statistic;
	
	public TStatus() {
		this.mobToKills = Maps.newHashMap();
		this.slaveToSaves = Lists.newArrayList();
		this.statistic = new DStatistic();
	}
	
	public Map<LivingEntity, String> getMobToKills() {
		return this.mobToKills;
	}
	
	public List<LivingEntity> getSlaveToSaves() {
		return this.slaveToSaves;
	}
	
	public DStatistic getStatistic() {
		return this.statistic;
	}
	
	public void removeMobToKill(LivingEntity entity) {
		this.mobToKills.remove(entity);
	}
	
	public void removeSlaveToSave(LivingEntity entity) {
		this.slaveToSaves.remove(entity);
	}
	
	public void addMobToKill(String mID, LivingEntity entity) {
		this.mobToKills.put(entity, mID);
	}
	
	public void addSlaveToSave(LivingEntity entity) {
		this.slaveToSaves.add(entity);
	}
	
	public void setStatistic(DStatistic statistic) {
		this.statistic = statistic;
	}
	
}
