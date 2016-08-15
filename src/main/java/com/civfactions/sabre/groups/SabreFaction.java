package com.civfactions.sabre.groups;

import java.util.UUID;

import com.civfactions.sabre.SabrePlugin;

public class SabreFaction extends SabreGroup {

	public SabreFaction(SabrePlugin plugin, UUID id, String name) {
		super(plugin, id, name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Gets whether the group is a faction
	 * @return true if the group is a faction
	 */
	@Override
	public boolean isFaction() {
		return true;
	}

}
