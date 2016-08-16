package com.civfactions.sabre.prisonpearl;

import org.bukkit.Location;

import com.civfactions.sabre.IPlayer;

public class PlayerHolder implements IItemHolder {

	private final IPlayer p;
	
	public PlayerHolder(IPlayer p) {
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
	
	
	public IPlayer getPlayer() {
		return this.p;
	}
}
