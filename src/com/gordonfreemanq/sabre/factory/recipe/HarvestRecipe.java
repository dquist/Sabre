package com.gordonfreemanq.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;

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
public class HarvestRecipe implements IRecipe {

	private final String name;
	private final int productionRate;

	private final ItemList<SabreItemStack> inputs;
	private final ItemList<SabreItemStack> outputs;
	private static final List<ProbabilisticEnchantment> nullEnchants = new ArrayList<ProbabilisticEnchantment>();
	
	
	/**
	 * Creates a new Factory Recipe instance
	 */
	public HarvestRecipe(String name, int productionRate) {
		this.name = name;
		this.productionRate = productionRate;
		this.inputs = new ItemList<SabreItemStack>();
		this.outputs = new ItemList<SabreItemStack>();
	}
	
	
	/**
	 * Gets the recipe name
	 * @return The recipe name
	 */
	public String getName() {
		return this.name;
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
		return 5;
	}
	
	
	/**
	 * Gets the recipe inputs
	 * @param inventory The input inventory
	 * @return The recipe inputs
	 */
	public ItemList<SabreItemStack> getInputs() {
		return inputs;
	}
	
	
	/**
	 * Gets the recipe outputs
	 * @param inventory The input inventory
	 * @return The recipe outputs
	 */
	public ItemList<SabreItemStack> getOutputs() {
		return outputs;
	}
	
	
	/**
	 * Gets the recipe enchants
	 * @return The recipe enchants
	 */
	public List<ProbabilisticEnchantment> getEnchants() {
		return nullEnchants;
	}
	
	/**
	 * A method to handle starting the the recipe
	 * Build the outputs based on accumulated crops
	 * @param factory The factory instance
	 */
	public void onRecipeStart(BaseFactory factory) {
		if (!(factory instanceof FarmFactory)) {
			return;
		}
		
		this.inputs.clear();
		this.outputs.clear();
		boolean cropsToHarvest = false;
		
		FarmFactory farm = (FarmFactory)factory;
		
		for (Entry<CropType, Integer> e : farm.getFarmedCrops().entrySet()) {
			if (e.getValue() > 0) {
				SabreItemStack crop = e.getKey().createCropItem();
				if (crop != null) {
					crop.setAmount(e.getValue());
					this.outputs.add(crop);
					cropsToHarvest = true;
				}
			}
		}
		
		if (!cropsToHarvest) {
			this.inputs.add(new SabreItemStack(Material.STONE, "Farmed Crops", 1, 99));
		}
	}
	
	
	/**
	 * A method to handle completing the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeComplete(BaseFactory factory) {
		((FarmFactory)factory).clearFarmedCrops();
	}
	
	/**
	 * Clones the recipe
	 * @return The clone
	 */
	public IRecipe clone() {
		return new HarvestRecipe(name, productionRate);
	}
}
