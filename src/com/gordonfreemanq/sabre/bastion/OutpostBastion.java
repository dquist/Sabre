package com.gordonfreemanq.sabre.bastion;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.customitems.SpecialBlock;
import com.mongodb.BasicDBObject;

public class OutpostBastion extends SpecialBlock {
	
	public static final String blockName = "Outpost Bastion";
	
	private final int BREAK_MULTIPLIER = 10;
	
	protected int counter;
	
	public OutpostBastion(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
		this.counter = BREAK_MULTIPLIER;
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
		
		// Only break when the multiplier reaches zero for unauthorized breaking
		if (counter-- > 0) {
			e.setCancelled(true);
		} else {
			counter = BREAK_MULTIPLIER;
		}
	}
}
