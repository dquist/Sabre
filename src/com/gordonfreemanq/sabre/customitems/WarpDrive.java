package com.gordonfreemanq.sabre.customitems;

import org.bukkit.Location;

import com.mongodb.BasicDBObject;

public class WarpDrive extends SpecialBlock {

	
	public static final String blockName = "Warp Drive";
	
	public WarpDrive(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
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
}
