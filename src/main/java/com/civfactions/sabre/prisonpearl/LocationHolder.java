package com.civfactions.sabre.prisonpearl;

import org.bukkit.Location;

public class LocationHolder implements IItemHolder {

	private final Location l;
	
	public LocationHolder(Location l) {
		this.l = l;
	}

	@Override
	public String getName() {
		return "nobody";
	}

	@Override
	public Location getLocation() {
		return l;
	}
}
