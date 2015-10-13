package com.gordonfreemanq.sabre.factory;

import java.util.List;

import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public interface IRecipe {
	
	/**
	 * Gets the recipe name
	 * @return The recipe name
	 */
	public String getName();
	
	
	/**
	 * Gets the recipe production speed
	 * @return The recipe production speed
	 */
	public int getProductionSpeed();

	
	/**
	 * Gets the recipe fuel cost
	 * @return The recipe fuel cost
	 */
	public int getFuelCost();
	
	
	/**
	 * Gets the recipe inputs
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs();
	
	
	/**
	 * Gets the recipe outputs
	 * @return The recipe outputs
	 */
	public ItemList<SabreItemStack> getOutputs();
	
	
	/**
	 * Gets the recipe enchants
	 * @return The recipe enchants
	 */
	public List<ProbabilisticEnchantment> getEnchants();
}
