package com.civfactions.sabre.factory.recipe;

import java.util.List;

import com.civfactions.sabre.blocks.SabreItemStack;
import com.civfactions.sabre.factory.BaseFactory;
import com.civfactions.sabre.factory.ItemList;
import com.civfactions.sabre.factory.ProbabilisticEnchantment;

/**
 * Represents a production recipe to be used in a factory
 * @author GFQ
 *
 */
public class ProductionRecipe implements IRecipe {

	private final String name;
	private final int productionSpeed;
	private final int fuelCost;
	private final ItemList<SabreItemStack> inputs;
	private final ItemList<SabreItemStack> outputs;
	private final List<ProbabilisticEnchantment> enchants;
	
	
	/**
	 * Creates a new Factory Recipe instance
	 */
	public ProductionRecipe(String name, int productionSpeed, int fuelCost, ItemList<SabreItemStack> inputs, 
			ItemList<SabreItemStack> outputs, List<ProbabilisticEnchantment> enchants) {
		this.name = name;
		this.productionSpeed = productionSpeed;
		this.fuelCost = fuelCost;
		this.inputs = inputs;
		this.outputs = outputs;
		this.enchants = enchants;
	}
	
	
	/**
	 * Gets the recipe name
	 * @return The recipe name
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Gets the recipe production speed
	 * @return The recipe production speed
	 */
	public int getProductionSpeed() {
		return this.productionSpeed;
	}
	
	
	/**
	 * Gets the recipe fuel cost
	 * @return The recipe fuel cost
	 */
	public int getFuelCost() {
		return this.fuelCost;
	}
	
	
	/**
	 * Gets the recipe inputs
	 * @param inventory The input inventory
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs() {
		return this.inputs;
	}
	
	
	/**
	 * Gets the recipe outputs
	 * @param inventory The input inventory
	 * @return The recipe outputs
	 */
	public ItemList<SabreItemStack> getOutputs() {
		return this.outputs;
	}
	
	
	/**
	 * Gets the recipe enchants
	 * @return The recipe enchants
	 */
	public List<ProbabilisticEnchantment> getEnchants() {
		return this.enchants;
	}
	
	
	/**
	 * A method to handle switching to the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeStart(BaseFactory factory) {
		// Do nothing
	}
	
	
	/**
	 * A method to handle completing the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeComplete(BaseFactory factory) {
		// Do nothing
	}
	
	
	/**
	 * Clones the recipe
	 * @return The clone
	 */
	public IRecipe clone() {
		return this;
	}
}
