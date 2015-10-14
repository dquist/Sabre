package com.gordonfreemanq.sabre.factory.recipe;

import java.util.List;

import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.factory.ItemList;
import com.gordonfreemanq.sabre.factory.ProbabilisticEnchantment;

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
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs() {
		return this.inputs;
	}
	
	
	/**
	 * Gets the recipe outputs
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
}
