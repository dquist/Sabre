package com.gordonfreemanq.sabre.blocks;

import org.bukkit.Material;

/**
 * Represents a reinforcment material
 * @author GFQ
 *
 */
public class ReinforcementMaterial {

	public final Material material;
	public final int strength;
	
	public ReinforcementMaterial(Material material, int strength) {
		this.material = material;
		this.strength = strength;
	}
}
