package com.gordonfreemanq.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;

import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.factory.CropType;
import com.gordonfreemanq.sabre.factory.ItemList;
import com.gordonfreemanq.sabre.factory.ProbabilisticEnchantment;

/**
 * Represents a farm factory production recipe
 * @author GFQ
 *
 */
public class FarmRecipe implements IRecipe {

	private final String name;
	private final CropType crop;
	private final int productionRate;
	
	private static final ItemList<SabreItemStack> nullStack = new ItemList<SabreItemStack>();
	private static final List<ProbabilisticEnchantment> nullEnchants = new ArrayList<ProbabilisticEnchantment>();
	
	
	/**
	 * Creates a new Factory Recipe instance
	 */
	public FarmRecipe(String name, CropType crop, int productionRate) {
		this.name = name;
		this.crop = crop;
		this.productionRate = productionRate;
	}
	
	
	/**
	 * Gets the recipe name
	 * @return The recipe name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the crop type
	 * @return The crop type
	 */
	public CropType getCrop() {
		return crop;
	}
	
	/**
	 * Gets the production rate
	 * @return The production rate
	 */
	public int getProductionRate() {
		return this.productionRate;
	}
	
	
	/**
	 * Gets the recipe production speed
	 * @return The recipe production speed
	 */
	public int getProductionSpeed() {
		return 1;
	}
	
	
	/**
	 * Gets the recipe fuel cost
	 * @return The recipe fuel cost
	 */
	public int getFuelCost() {
		return 0;
	}
	
	
	/**
	 * Gets the recipe inputs
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs() {
		return nullStack;
	}
	
	
	/**
	 * Gets the recipe outputs
	 * @return The recipe outputs
	 */
	public ItemList<SabreItemStack> getOutputs() {
		return nullStack;
	}
	
	
	/**
	 * Gets the recipe enchants
	 * @return The recipe enchants
	 */
	public List<ProbabilisticEnchantment> getEnchants() {
		return nullEnchants;
	}
}
