package com.civfactions.sabre.groups;

import java.util.UUID;

public class SabreFaction extends SabreGroup {

	public SabreFaction(UUID id, String name) {
		super(id, name);
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
