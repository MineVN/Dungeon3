package mk.plugin.dungeon3.dungeon.event;

import mk.plugin.dungeon3.dungeon.status.DStatus;
import mk.plugin.dungeon3.dungeon.status.DungeonResult;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonFinishEvent extends Event {
	
	private String id;
	private DStatus status;
	private DungeonResult result;
	
	public DungeonFinishEvent(String id, DStatus status, DungeonResult result) {
		this.id = id;
		this.status = status;
		this.result = result;
	}
	
	public String getID() {
		return this.id;
	}
	
	public DStatus getStatus() {
		return this.status;
	}
	
	public DungeonResult getResult() {
		return this.result;
	}
	
	/*
	 *  Required
	 */
	
	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public final static HandlerList getHandlerList(){
		return handlers;
	}
}
