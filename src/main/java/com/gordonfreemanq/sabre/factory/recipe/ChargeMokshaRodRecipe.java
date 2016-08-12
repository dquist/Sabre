package com.gordonfreemanq.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.customitems.MokshaRod;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.ItemList;
import com.gordonfreemanq.sabre.factory.ProbabilisticEnchantment;

/**
 * Recipe that charges a prison pearl strength
 * @author GFQ
 *
 */
public class ChargeMokshaRodRecipe implements IRecipe {

	private final String name;
	private final int productionSpeed;
	private final int costFactor;
	
	private int configFuelCost;
	private int fuelCost;
	private BaseFactory factory;
	
	private int recipeStrengthAmount;
	
	private final ItemList<SabreItemStack> inputs;
	private final ItemList<SabreItemStack> outputs;
	
	private final List<ProbabilisticEnchantment> nullEnchants = new ArrayList<ProbabilisticEnchantment>();
	
	/**
	 * Creates a new ChargePrisonPearlRecipe instance
	 */
	public ChargeMokshaRodRecipe(String name, int productionSpeed, int fuelCost) {
		this.name = name;
		this.productionSpeed = productionSpeed;
		this.costFactor = SabrePlugin.instance().getSabreConfig().getJailbreakCostFactor();
		this.configFuelCost = fuelCost;
		this.inputs = new ItemList<SabreItemStack>();
		this.outputs = new ItemList<SabreItemStack>();
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getProductionSpeed() {
		return productionSpeed;
	}

	@Override
	public int getFuelCost() {
		return fuelCost;
	}

	/**
	 * This will dynamically generate a list of inputs based on the input inventory
	 */
	@Override
	public ItemList<SabreItemStack> getInputs() {
		return this.inputs;
	}

	@Override
	public ItemList<SabreItemStack> getOutputs() {
		return this.outputs;
	}

	@Override
	public List<ProbabilisticEnchantment> getEnchants() {
		return nullEnchants;
	}
	
	/**
	 * A method to handle switching to the recipe
	 * @param factory The factory instance
	 */
	public void onRecipeStart(BaseFactory factory) {
		this.factory = factory;
		generateRecipe();
	}
	
	/**
	 * Dynamically generates the recipe based on the contents of the input chest.
	 */
	private void generateRecipe() {
		inputs.clear();
		outputs.clear();
		fuelCost = configFuelCost;
		
		// Default item
		SabreItemStack mokshaStack = new MokshaRod();
		
		// Get the rod from the inventory if it exists
		ItemStack is = MokshaRod.getFromInventory(factory.getInputInventory());
		if (is != null) {
			mokshaStack = MokshaRod.getRodFromItem(is);
			
			// Add the pearl to the recipe
			outputs.add(mokshaStack);
		}
		
		// Add the pearl to the recipe
		inputs.add(mokshaStack);
		
		// The default recipe requires at least the factor amount of cuendillar
		SabreItemStack inputCuendillar = CustomItems.getInstance().getByName("Cuendillar");
		inputCuendillar.setAmount(costFactor);
		
		// Change the recipe to use however much cuendillar is available in the inventory
		recipeStrengthAmount = ItemList.amountAvailable(factory.getInputInventory(), inputCuendillar);
		if (recipeStrengthAmount > 1) {
			inputCuendillar.setAmount(recipeStrengthAmount);
		}
		
		// Add the cuendillar to the recipe
		inputs.add(inputCuendillar);
		
		// update fuel cost based on the strength
		fuelCost = recipeStrengthAmount / costFactor * 2;
	}
	
	
	/**
	 * A method to handle completing the recipe
	 * 
	 * This will update modified prison pearl in the output chest
	 * 
	 * @param factory The factory instance
	 */
	public void onRecipeComplete(BaseFactory factory) {
		
		// Get the rod from the inventory if it exists
		ItemStack is = MokshaRod.getFromInventory(factory.getInputInventory());
		if (is != null) {
			MokshaRod mokshaStack = MokshaRod.getRodFromItem(is);
			
			// jailbreaking is more expensive than pearl holding
			int increase = (int)(recipeStrengthAmount / costFactor);
			
			mokshaStack.setStrength(mokshaStack.getStrength() + increase);
			is.setItemMeta(mokshaStack.getItemMeta());
		}
	}
	
	/**
	 * Clones the recipe
	 * @return The clone
	 */
	public IRecipe clone() {
		return new ChargeMokshaRodRecipe(this.name, this.productionSpeed, this.configFuelCost);
	}
}
