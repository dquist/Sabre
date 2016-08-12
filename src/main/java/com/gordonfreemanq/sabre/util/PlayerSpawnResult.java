package com.gordonfreemanq.sabre.util;

import org.bukkit.Location;

public class PlayerSpawnResult {

	private final Location location;
	private final boolean bedSpawn;
	
	/**
	 * Creates a new PlayerSpawnResult instance
	 * @param location The spawn location
	 * @param bedSpawn Whether it was a bed spawn
	 */
	public PlayerSpawnResult(Location location, boolean bedSpawn) {
		this.location = location;
		this.bedSpawn = bedSpawn;
	}
	
	/**
	 * Gets the spawn location
	 * @return The spawn location
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Gets whether it was a bed spawn
	 * @return true if it was a bed spawn
	 */
	public boolean wasBedSpawn() {
		return bedSpawn;
	}
}
