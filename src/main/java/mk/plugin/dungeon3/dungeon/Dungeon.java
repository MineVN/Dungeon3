package mk.plugin.dungeon3.dungeon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mk.plugin.dungeon3.configable.Configable;
import mk.plugin.dungeon3.dungeon.block.DBlock;
import mk.plugin.dungeon3.dungeon.drop.DDrop;
import mk.plugin.dungeon3.dungeon.info.DInfo;
import mk.plugin.dungeon3.dungeon.location.DLocation;
import mk.plugin.dungeon3.dungeon.moneycoin.DMoneyDrop;
import mk.plugin.dungeon3.dungeon.option.DOption;
import mk.plugin.dungeon3.dungeon.rewardreq.DRewardReq;
import mk.plugin.dungeon3.dungeon.rule.DRule;
import mk.plugin.dungeon3.dungeon.turn.DTurn;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dungeon extends Configable {

	private DInfo info;
	private DOption option;
	private DRule rule;
	private Map<String, DLocation> locs;
	private Map<String, DBlock> blocks;
	private List<DMoneyDrop> moneyDrops;
	private List<DDrop> drops;
	private DRewardReq rewardReq;
	private List<String> rewards;
	private List<DTurn> turns;
	private List<String> checkPoints;
	
	public Dungeon(DInfo info, DOption option, DRule rule, List<String> rewards, Map<String, DLocation> locs, Map<String, DBlock> blocks, List<DMoneyDrop> moneyDrops, List<DDrop> drops, DRewardReq rewardReq, List<DTurn> turns, List<String> checkPoints) {
		this.info = info;
		this.option = option;
		this.rule = rule;
		this.locs = locs;
		this.blocks = blocks;
		this.moneyDrops = moneyDrops;
		this.drops = drops;
		this.rewardReq = rewardReq;
		this.turns = turns;
		this.checkPoints = checkPoints;
		this.rewards = rewards;
	}
	
	public Dungeon(FileConfiguration config, String path) {
		super(config, path);
	}
	
	public DInfo getInfo() {
		return this.info;
	}
	
	public DOption getOption() {
		return this.option;
	}
	
	public DRule getRule() {
		return this.rule;
	}
	
	public Map<String, DLocation> getLocations() {
		return this.locs;
	}
	
	public DLocation getLocation(String id) {
		return this.locs.getOrDefault(id, null);
	}
	
	public Map<String, DBlock> getBlocks() {
		return this.blocks;
	}
	
	public DBlock getBlock(String id) {
		return this.blocks.getOrDefault(id, null);
	}
	
	public List<DMoneyDrop> getMoneyDrops() {
		return this.moneyDrops;
	}
	
	public List<DDrop> getDrops() {
		return this.drops;
	}
	
	public DRewardReq getRewardReq() {
		return this.rewardReq;
	}
	
	public List<DTurn> getTurns() {
		return this.turns;
	}
	
	public List<String> getCheckPoints() {
		return this.checkPoints;
	}

	public List<String> getRewards() {
		return this.rewards;
	}
	
	public void setLocation(String id, Location l, int r) {
		this.locs.put(id, new DLocation(r, l));
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(String id, Block b) {
		this.blocks.put(id, new DBlock(b.getType(), b.getData(), b.getLocation()));
	}
	
	@Override
	public void load(FileConfiguration config, String path) {
		this.info = new DInfo(config, path + ".info");
		this.option = new DOption(config, path + ".option");
		this.rule = new DRule(config, path + ".rule");
		this.rewardReq = new DRewardReq(config, path + ".reward-req");
		
		this.rewards = Lists.newArrayList();
		config.getStringList(path + ".rewards").forEach(s -> {
			this.rewards.add(s);
		});

		this.locs = Maps.newHashMap();
		if (config.contains(path + ".location")) {
			config.getConfigurationSection(path + ".location").getKeys(false).forEach(id -> {
				this.locs.put(id, new DLocation(config, path + ".location." + id));
			});
		}

		this.blocks = Maps.newHashMap();
		if (config.contains(path + ".block")) {
			config.getConfigurationSection(path + ".block").getKeys(false).forEach(id -> {
				this.blocks.put(id, new DBlock(config, path + ".block." + id));
			});
		}
		
		this.moneyDrops = config.getStringList(path + ".money-drop").stream().map(s -> new DMoneyDrop(s)).collect(Collectors.toList());
		
		this.drops = Lists.newArrayList();
		config.getStringList(path + ".drop").forEach(s -> {
			drops.add(new DDrop(s));
		});

		this.turns = Lists.newArrayList();
		int i = 1;
		while (config.contains(path + ".turn.t" + i)) {
			this.turns.add(new DTurn(config, path + ".turn.t" + i));
			i++;
		}

		this.checkPoints = config.getStringList(path + ".check-points");
	}

	@Override
	public void save(FileConfiguration config, String path) {
		this.info.save(config, path + ".info");
		this.option.save(config, path + ".option");
		this.rule.save(config, path + ".rule");
		this.rewardReq.save(config, path + ".reward-req");
		
		List<String> r = Lists.newArrayList();
		this.rewards.forEach(cmd -> {
			r.add(cmd);
		});
		config.set(path + ".rewards", r);
		
		this.locs.forEach((id, l) -> {
			l.save(config, path + ".location." + id);
		});
		this.blocks.forEach((id, b) -> {
			b.save(config, path + ".block." + id);
		});
		
		config.set(path + ".money-drop", this.moneyDrops.stream().map(s -> s.toString()).collect(Collectors.toList()));
		
		List<String> dl = Lists.newArrayList();
		this.drops.forEach(drop -> {
			dl.add(drop.toString());
		});
		config.set(path + ".drop", dl);
		
		for (int i = 0 ; i < turns.size() ; i++) {
			turns.get(i).save(config, path + ".turn.t" + (i + 1));
		}
		
		config.set(path + ".check-points", this.checkPoints);
	}
	
}
