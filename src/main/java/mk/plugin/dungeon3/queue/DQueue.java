package mk.plugin.dungeon3.queue;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DQueue {
	
	private int max;
	private int min;
	
	private List<UUID> players;
	
	public DQueue(int min, int max) {
		this.max = max;
		this.min = min;
		this.players = new ArrayList<UUID> ();
	}
	
	public int getMax() {
		return this.max;
	}
	
	public int getMin() {
		return this.min;
	}
	
	public List<UUID> getPlayers() {
		return this.players;
	}
	
	public boolean canAdd(Player player) {	
		if (this.players.contains(player.getUniqueId())) return false;
		return this.players.size() < max;
	}
	
	public boolean canStart() {
		return this.players.size() >= min;
	}
	
	public void add(Player player) {
		if (!canAdd(player)) return;
		this.players.add(player.getUniqueId());
 	}
	
	public void remove(Player player) {
		this.players.remove(player.getUniqueId());
	}
}
