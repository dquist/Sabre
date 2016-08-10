package com.gordonfreemanq.sabre.customitems;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.mongodb.BasicDBObject;

public class SteelPlate extends SpecialBlock {

	
	public static final String blockName = "Steel Plate";
	
	protected final int DAMAGE_COUNTER = 9;
	
	protected int counter;
	
	public SteelPlate(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
		this.counter = DAMAGE_COUNTER;
	}
	
	

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		return null;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
	}
	
	

	/**
	 * Handles the block breaking, low priority event
	 * @param e The event args
	 */
	public void onBlockBreaking(SabrePlayer p, BlockBreakEvent e) {
		
		// Normal operation if player can access
		if (this.canPlayerAccess(p)) {
			return;
		}
		
		// Otherwise, cancel the break 19/20 times
		if (counter-- > 0) {
			e.setCancelled(true);
		} else {
			counter = DAMAGE_COUNTER;
		}
	}
}
