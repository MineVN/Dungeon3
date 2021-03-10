package mk.plugin.dungeon3.dungeon.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonStartEvent extends Event {
	
	private String id;
	
	public DungeonStartEvent(String id) {
		this.id = id;
	}
	
	public String getID() {
		return this.id;
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
