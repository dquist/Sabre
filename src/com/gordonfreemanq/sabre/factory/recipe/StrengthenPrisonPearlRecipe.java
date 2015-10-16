package com.gordonfreemanq.sabre.factory.recipe;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.ItemList;
import com.gordonfreemanq.sabre.factory.ProbabilisticEnchantment;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;

/**
 * Recipe that charges a prison pearl strength
 * @author GFQ
 *
 */
public class StrengthenPrisonPearlRecipe implements IRecipe {

	private final String name;
	private final int productionSpeed;
	private int configFuelCost;
	private int fuelCost;
	private BaseFactory factory;
	
	private PrisonPearl recipePearl;
	private int recipeStrengthAmount;
	
	private final ItemList<SabreItemStack> inputs;
	private final ItemList<SabreItemStack> outputs;
	
	private final List<ProbabilisticEnchantment> nullEnchants = new ArrayList<ProbabilisticEnchantment>();
	
	/**
	 * Creates a new ChargePrisonPearlRecipe instance
	 */
	public StrengthenPrisonPearlRecipe(String name, int productionSpeed, int fuelCost) {
		this.name = name;
		this.productionSpeed = productionSpeed;
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
		
		// Add a default unmatchable prison-pearl item with 1 durability
		SabreItemStack pearlStack = new SabreItemStack(Material.ENDER_PEARL, "Prison Pearl", 1, 1);
		
		// If a prison pearl exists in the inventory, then replace the default ingredient pearl with that item
		List<PrisonPearl> pearls = PearlManager.getInstance().getInventoryPrisonPearls(factory.getInputInventory());
		if (pearls.size() > 0) {
			recipePearl = pearls.get(0);
			pearlStack = recipePearl.createItemStack();
			outputs.add(pearlStack);
		}
		
		// Add the pearl to the recipe
		inputs.add(pearlStack);
		
		// The default recipe requires at least 1 cuendillar item
		SabreItemStack inputCuendillar = CustomItems.getInstance().getByName("Cuendillar");
		inputCuendillar.setAmount(1);
		
		// Change the recipe to use however much cuendillar is available in the inventory
		recipeStrengthAmount = ItemList.amountAvailable(factory.getInputInventory(), inputCuendillar);
		if (recipeStrengthAmount > 1) {
			inputCuendillar.setAmount(recipeStrengthAmount);
		}
		
		// Add the cuendillar to the recipe
		inputs.add(inputCuendillar);
		
		// update fuel cost based on the strength
		fuelCost = recipeStrengthAmount * 2;
	}
	
	
	/**
	 * A method to handle completing the recipe
	 * 
	 * This will update modified prison pearl in the output chest
	 * 
	 * @param factory The factory instance
	 */
	public void onRecipeComplete(BaseFactory factory) {

		// Update the new location for the pearl
		recipePearl.setHolder(factory.getLocation());
		
		// Update the new strength
		PearlManager.getInstance().increaseSealStrength(recipePearl, recipeStrengthAmount);
		
		// Gets the item stack from the chest and validate it, which will update the strength lore
		ItemStack is = recipePearl.getItemFromInventory(factory.getOutputInventory());
		recipePearl.validateItemStack(is);
	}
	
	/**
	 * Clones the recipe
	 * @return The clone
	 */
	public IRecipe clone() {
		return new StrengthenPrisonPearlRecipe(this.name, this.productionSpeed, this.configFuelCost);
	}
}
