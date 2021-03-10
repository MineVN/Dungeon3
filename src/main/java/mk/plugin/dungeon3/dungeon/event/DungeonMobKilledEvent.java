package mk.plugin.dungeon3.dungeon.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DungeonMobKilledEvent extends Event {
	
	private String id;
	private String mID;
	private LivingEntity entity;
	private Player killer;
	
	public DungeonMobKilledEvent(String id, String mID, LivingEntity entity, Player killer) {
		this.id = id;
		this.mID = mID;
		this.entity = entity;
		this.killer = killer;
	}
	
	public String getID() {
		return this.id;
	}
	
	public String getMobID() {
		return this.mID;
	}
	
	public LivingEntity getEntity() {
		return this.entity;
	}
	
	public Player getKiller() {
		return this.killer;
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
