package com.civfactions.sabre.blocks;

import org.bukkit.Material;

/**
 * Represents a reinforcement material that players can use to fortify blocks
 * @author GFQ
 *
 */
public class ReinforcementMaterial {

	public final Material material;
	public final int durability;
	public final int strength;
	public final boolean admin;
	
	/**
	 * Creates a new ReinforcementMaterial instance
	 * @param material The material type
	 * @param durability The material durability
	 * @param strength How strong the reinforcement is
	 */
	public ReinforcementMaterial(Material material, int durability, int strength, boolean admin) {
		this.material = material;
		this.durability = durability;
		this.strength = strength;
		this.admin = admin;
	}
	
	/**
	 * Creates a new ReinforcementMaterial instance
	 * @param material The material type
	 * @param strength How strong the reinforcement is
	 */
	public ReinforcementMaterial(Material material, int strength) {
		this(material, 0, strength, false);
	}
}
