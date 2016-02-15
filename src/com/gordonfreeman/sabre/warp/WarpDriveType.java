package com.gordonfreeman.sabre.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;

public enum WarpDriveType {
	NETHER,
	END,
	OVERWORLD;
	
	public static String OVER_WORLD_NAME = "world";
	public static String NETHER_WORLD_NAME = "world_nether";
	public static String END_WORLD_NAME = "world_the_end";
	
	/**
	 * Gets the destination teleport location for a given warp type
	 * @param from The starting location
	 * @return The destination location
	 */
	public Location getTeleportLocation(Location from) {
		World destWorld = null;
		double destX = 0;
		double destZ = 0;
		double destY = 0;
		
		switch (this)
		{
		case NETHER:
		default:
			if (from.getWorld().getEnvironment() == Environment.NORMAL) {
				destWorld = Bukkit.getWorld(NETHER_WORLD_NAME);
				destX = from.getBlockX() >> 3; // divide by 8
				destZ = from.getBlockZ() >> 3; // divide by 8
				destY = from.getBlockY();
			}
			break;
		}
	
		return new Location(destWorld, destX, destY, destZ);
	}
}
