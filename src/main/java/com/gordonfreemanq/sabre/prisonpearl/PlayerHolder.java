package com.gordonfreemanq.sabre.prisonpearl;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.SabrePlayer;

public class PlayerHolder implements IItemHolder {

	private final SabrePlayer p;
	
	public PlayerHolder(SabrePlayer p) {
		this.p = p;
	}

	@Override
	public String getName() {
		return p.getName();
	}

	@Override
	public Location getLocation() {
		return p.getPlayer().getLocation().add(0, -.5, 0);
	}
	
	
	public SabrePlayer getPlayer() {
		return this.p;
	}
}
