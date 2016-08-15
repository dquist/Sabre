package com.civfactions.sabre.customitems;

import org.bukkit.Location;

import com.civfactions.sabre.blocks.SabreBlock;

public class SpecialBlock extends SabreBlock {
	
	public SpecialBlock(Location location, String typeName) {
		super(location, typeName);
	}
	
	
	/** 
	 * A quick way to check if this is anything except a basic block
	 * @return true if it is a special block
	 */
	@Override
	public boolean isSpecial() {
		return true;
	}
}
