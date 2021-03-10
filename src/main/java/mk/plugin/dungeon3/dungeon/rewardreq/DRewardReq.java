package mk.plugin.dungeon3.dungeon.rewardreq;

import mk.plugin.dungeon3.configable.Configable;
import mk.plugin.dungeon3.rank.Rank;
import mk.plugin.dungeon3.util.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class DRewardReq extends Configable {
	
	private int save;
	private List<String> kills;
	private int killCount;
	private int maxDead;
	private Rank rank;
	
	public DRewardReq(FileConfiguration config, String path) {
		super(config, path);
	}
	
	public DRewardReq(int save, List<String> kills, int killCount, int maxDead, Rank rank) {
		this.save = save;
		this.kills = kills;
		this.killCount = killCount;
		this.maxDead = maxDead;
		this.rank = rank;
	}
	
	public int getSave() {
		return this.save;
	}
	
	public List<String> getKills() {
		return this.kills;
	}
	
	public int getKillCount() {
		return this.killCount;
	}

	public int getMaxDead() {
		return this.maxDead;
	}
	
	private String killsToString() {
		return Utils.to(this.kills, ";");
	}
	
	public Rank getRank() {
		return this.rank;
	}
	
	@Override
	public void load(FileConfiguration config, String path) {
		this.save = config.getInt(path + ".slave-save");
		this.kills = Utils.from(config.getString(path + ".mob-kill"), ";");
		this.killCount = config.getInt(path + ".kill-count");
		this.maxDead = config.getInt(path + ".max-dead");
		this.rank = config.contains(path + ".rank") ? Rank.valueOf(config.getString(path + ".rank")) : Rank.F;
	}

	@Override
	public void save(FileConfiguration config, String path) {
		config.set(path + ".slave-save", this.save);
		config.set(path + ".mob-kill", this.killsToString());
		config.set(path + ".kill-count", this.killCount);
		config.set(path + ".max-dead", this.maxDead);
		config.set(path + ".rank", this.rank.name());
	}
	
}
