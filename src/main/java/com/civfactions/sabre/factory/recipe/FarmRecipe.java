package com.civfactions.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.civfactions.sabre.blocks.SabreItemStack;
import com.civfactions.sabre.factory.BaseFactory;
import com.civfactions.sabre.factory.ItemList;
import com.civfactions.sabre.factory.ProbabilisticEnchantment;
import com.civfactions.sabre.factory.farm.CropType;
import com.civfactions.sabre.factory.farm.FarmFactory;
import com.civfactions.sabre.factory.farm.FarmSurveyor;

/**
 * Represents a farm factory production recipe
 * @author GFQ
 *
 */
public class FarmRecipe implements IRecipe {

	private final String name;
	private final CropType crop;
	private final int productionRate;
	private final FarmSurveyor surveyor;
	
	private static final ItemList<SabreItemStack> nullStack = new ItemList<SabreItemStack>();
	private static final List<ProbabilisticEnchantment> nullEnchants = new ArrayList<ProbabilisticEnchantment>();
	
	
	/**
	 * Creates a new Factory Recipe instance
	 */
	public FarmRecipe(String name, CropType crop, int productionRate) {
		this.name = name;
		this.crop = crop;
		this.productionRate = productionRate;
		this.surveyor = crop.createSurveyor();
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
	 * Gets the farm surveyor
	 * @return The farm surveyor
	 */
	public FarmSurveyor getSurveyor() {
		return this.surveyor;
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
