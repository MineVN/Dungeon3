package mk.plugin.dungeon3.dungeon.player;

import com.google.common.collect.Lists;
import mk.plugin.dungeon3.dungeon.Dungeon;
import mk.plugin.dungeon3.dungeon.util.DDataUtils;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

public class DPlayer {
	
	private static final String DROP_RATE_PERM = "dungeon3.droprate.buff.";
	private static final String REVIVE_RATE_PERM = "dungeon3.revive.buff.";
	
	private double dropRateBuff;
	private int reviveBuff;
	
	public DPlayer(double dropRateBuff, int reviveBuff) {
		this.dropRateBuff = dropRateBuff;
		this.reviveBuff = reviveBuff;
	}
	
	public double getDropRateBuff() {
		return this.dropRateBuff;
	}
	
	public int getReviveBuff() {
		return this.reviveBuff;
	}
	
	public int getMaxDead(String id) {
		Dungeon d = DDataUtils.getDungeon(id);
		return d.getRule().getRespawnTime() + this.getReviveBuff();
	}
	
	public static DPlayer from(Player p) {
		double dr = 0;
		int rb = 0;
		User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
		for (Node node : Lists.newArrayList(user.getNodes())) {
			if (node.getKey().contains(DROP_RATE_PERM)) dr = Math.max(dr, Double.valueOf(node.getKey().replace(DROP_RATE_PERM, ""))); 
			if (node.getKey().contains(REVIVE_RATE_PERM)) rb = Math.max(rb, Integer.valueOf(node.getKey().replace(REVIVE_RATE_PERM, ""))); 
		}
		return new DPlayer(dr, rb);
	}
	
}
