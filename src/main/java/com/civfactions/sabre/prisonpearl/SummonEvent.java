package com.civfactions.sabre.prisonpearl;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SummonEvent extends Event {
	public enum Type { SUMMONED, RETURNED, KILLED, DIED }

    private final PrisonPearl pp;
	private final Type type;
	private Location location;

	private boolean cancelled;
	
	/**
	 * Creates a new SummonEvent instance
	 * @param pp The prison pearl
	 * @param type The event type
	 */
	public SummonEvent(PrisonPearl pp, Type type) {
		this.pp = pp;
		this.type = type;
	}
	
	/**
	 * Creates a new SummonEvent instance
	 * @param pp The prison pearl
	 * @param type The event type
	 * @param The summon location
	 */
	public SummonEvent(PrisonPearl pp, Type type, Location location) {
		this.pp = pp;
		this.type = type;
		this.location = location;
	}
	
	/**
	 * Gets the prison pearl
	 * @return The prison pearl
	 */
	public PrisonPearl getPrisonPearl() {
		return pp;
	}
	
	
	/**
	 * Gets the event type
	 * @return The event type
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Gets the locatio
	 * @return The location
	 */
	public Location getLocation() {
		return location;
	}

	
	/**
	 * Gets whether the event is cancelled
	 * @return true if it is cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
	
	/**
	 * Sets whether the event is cancelled
	 * @param cancelled Whether it is cancelled
	 */
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
