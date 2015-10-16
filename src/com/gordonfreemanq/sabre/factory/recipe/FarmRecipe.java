package com.gordonfreemanq.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.ItemList;
import com.gordonfreemanq.sabre.factory.ProbabilisticEnchantment;
import com.gordonfreemanq.sabre.factory.farm.CropType;
import com.gordonfreemanq.sabre.factory.farm.FarmFactory;

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
	 * @param inventory The input inventory
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs() {
		return nullStack;
	}
	
	
	/**
	 * Gets the recipe outputs
	 * @param inventory The input inventory
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
	
	/**
	 * A method to handle switching to the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeStart(BaseFactory factory) {
		if (!(factory instanceof FarmFactory)) {
			return;
		}
		
		FarmFactory farm = (FarmFactory)factory;
		
		Map<CropType, Integer> farmedCrops = farm.getFarmedCrops();
		if (!farmedCrops.containsKey(this.crop)) {
			farmedCrops.put(this.crop, 0);
		}
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
