package com.gordonfreeman.sabre.warp;

import org.bukkit.Location;
import com.mongodb.BasicDBObject;

public class NetherWarpDrive extends AbstractWarpDrive {

	
	public static final String blockName = "Nether Warp Drive";
	
	public NetherWarpDrive(Location location, String typeName) {
		super(location, blockName, WarpDriveType.NETHER);
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
