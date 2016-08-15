package com.civfactions.sabre.customitems;

import java.util.Date;

import org.bukkit.Location;
import org.bukkit.Material;

public class AcidBlock extends SpecialBlock {
	
	public static final String blockName = "Acid Block";
	public static final Material MATERIAL = Material.GOLD_BLOCK;
	
	protected Date placedDate;
	
	public AcidBlock(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
	}
	
	
	/**
	 * Gets whether the block is mature or not
	 * @return true if the block is mature
	 */
	public boolean isMature() {
		return false; // TODO
	}
}
