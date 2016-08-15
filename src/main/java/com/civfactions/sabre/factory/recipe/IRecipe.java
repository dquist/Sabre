package com.civfactions.sabre.factory.recipe;

import java.util.List;

import com.civfactions.sabre.blocks.SabreItemStack;
import com.civfactions.sabre.factory.BaseFactory;
import com.civfactions.sabre.factory.ItemList;
import com.civfactions.sabre.factory.ProbabilisticEnchantment;

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
	 * @param inventory The input inventory
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs();
	
	
	/**
	 * Gets the recipe outputs
	 * @param inventory The input inventory
	 * @return The recipe outputs
	 */
	public ItemList<SabreItemStack> getOutputs();
	
	
	/**
	 * Gets the recipe enchants
	 * @return The recipe enchants
	 */
	public List<ProbabilisticEnchantment> getEnchants();
	
	
	/**
	 * A method to handle starting the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeStart(BaseFactory factory);
	
	
	/**
	 * A method to handle completing the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeComplete(BaseFactory factory);
	
	
	/**
	 * Clones the recipe
	 * @return The clone
	 */
	public IRecipe clone();
}
