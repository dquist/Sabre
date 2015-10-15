package com.gordonfreemanq.sabre.factory;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.factory.recipe.ProductionRecipe;
import com.gordonfreemanq.sabre.factory.recipe.FarmRecipe;

/**
 * Class that provides core functionality for all the farm factories
 * @author GFQ
 *
 */
public class FarmFactory extends BaseFactory {
	
	// How many factory ticks to wait before producing
	int farmProductionTicks;
	
	// The current production tick counter for when the farm is running
	int farmTickCounter;
	
	// The crop coverage from 0 to 1.0
	private double coverage;
	
	// Efficiency factory based on proximity to other farms from 0 to 1.0
	private double proximityFactor;
	
	// The amount of farmed goods
	private HashMap<CropType, Integer> farmedCrops;

	/**
	 * Creates a new FarmFactory instance
	 * @param location The factory location
	 * @param typeName The factory name
	 */
	public FarmFactory(Location location, String typeName) {
		super(location, typeName);
		this.farmProductionTicks = SabrePlugin.getPlugin().getSabreConfig().getFarmProductionTicks();
		this.farmTickCounter = 0;
		this.coverage = 0;
		this.proximityFactor = 1.0;
		this.farmedCrops = new HashMap<CropType, Integer>();
		
	}
	
	
	/**
	 * Handles hitting the block with a stick
	 * @param p The player interacting
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		// Create a controller if it doesn't exist
		if (this.canPlayerModify(sp)) {
			createController(sp);
		}
		
		Action a = e.getAction();
		if (a == Action.RIGHT_CLICK_BLOCK) {
			if (running) {
				if (recipe instanceof ProductionRecipe) {
					sp.msg("<a>Status: <n>%d%%", this.getPercentComplete());
				} else if (recipe instanceof FarmRecipe) {
					FarmRecipe fr = (FarmRecipe)recipe;
					sp.msg("<a>Status: <n>Farming %s", fr.getCrop().toString());
				}
			} else {
				cycleRecipe(sp);
			}
		} else {
			cyclePower(sp);
		}
	}
	
	
	/**
	 * Creates the factory controller
	 * @param sp The player
	 */
	@Override
	protected void createController(SabrePlayer sp) {
		ItemStack is = (new FactoryController(this)).toItemStack();
		sp.getPlayer().getInventory().setItemInHand(is);
	}
	
	
	/**
	 * Update method for running the factory
	 */
	@Override
	public void update() {
		
		if (!running) {
			return;
		}
		
		if (recipe instanceof ProductionRecipe) {
			super.update();
		} else {
			if (farmTickCounter++ >= farmProductionTicks) {
				farmTickCounter = 0;
				runFarmRecipe();
			}
		}
	}
	
	protected void runFarmRecipe() {
		if (!running) {
			return;
		}
		
		if (!(recipe instanceof FarmRecipe)) {
			return;
		}
		
		FarmRecipe fr = (FarmRecipe)recipe;
		
		// Just a quick sanity check on these numbers
		this.coverage = Math.min(coverage, 1.0);
		this.proximityFactor = Math.min(proximityFactor, 1.0);
		
		CropType cropType = fr.getCrop();
		int realOutput = (int)(fr.getProductionRate() * coverage * proximityFactor);
		int alreadyFarmed = farmedCrops.get(cropType);
		farmedCrops.put(cropType, alreadyFarmed + realOutput);
	}
	
	
	/**
	 * Calculates the coverage and proximity factor values
	 */
	public void survey() {
		// TODO
	}
	
	
	/**
	 * Gets the farmed crops
	 * @return The farmed crops
	 */
	public HashMap<CropType, Integer> getFarmedCrops() {
		return this.farmedCrops;
	}
	

}
