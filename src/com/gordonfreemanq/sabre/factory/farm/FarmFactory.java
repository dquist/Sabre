package com.gordonfreemanq.sabre.factory.farm;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.FactoryController;
import com.gordonfreemanq.sabre.factory.FactoryWorker;
import com.gordonfreemanq.sabre.factory.recipe.FarmRecipe;

/**
 * Class that provides core functionality for all the farm factories
 * @author GFQ
 *
 */
public class FarmFactory extends BaseFactory {
	
	// How many factory ticks to wait before producing
	int farmProductionTicks;
	
	// How far away it has to be away from another running farm
	int farmProximity;
	
	// The current production tick counter for when the farm is running
	int farmTickCounter;
	
	// The crop coverage from 0 to 1.0
	private double coverage;
	
	// Efficiency factory based on proximity to other farms from 0 to 1.0
	private double proximityFactor;
	
	// Biome specific efficiency factor
	private double biomeFactor;
	
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
		this.farmProximity = SabrePlugin.getPlugin().getSabreConfig().getFarmProductionTicks();
		this.farmTickCounter = 0;
		this.coverage = 1.0; // TODO
		this.proximityFactor = 1.0; // TODO
		this.biomeFactor = 1.0; // TODO
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
				if (recipe instanceof FarmRecipe) {
					FarmRecipe fr = (FarmRecipe)recipe;
					sp.msg("<a>Status: <n>Farming %s, %d ready for harvest", fr.getCrop().toString(), 
							this.farmedCrops.get(((FarmRecipe) recipe).getCrop()));

				} else {
					sp.msg("<a>Status: <n>%d%%", this.getPercentComplete());
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
		
		if (recipe instanceof FarmRecipe) {
			if (farmTickCounter++ >= farmProductionTicks) {
				farmTickCounter = 0;
				runFarmRecipe();
			}
		} else {
			super.update();
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
		this.biomeFactor = Math.min(biomeFactor, 1.0);
		
		CropType cropType = fr.getCrop();
		int realOutput = (int)(fr.getProductionRate() * coverage * proximityFactor * biomeFactor);
		int alreadyFarmed = farmedCrops.get(cropType);
		farmedCrops.put(cropType, alreadyFarmed + realOutput);
	}
	
	
	/**
	 * Calculates the coverage and proximity factor values
	 */
	public void survey() {
		calculateLayoutFactor();
		calculateBiomeFactor();
		calculateProximityFactor();
	}
	
	
	/**
	 * Calculates the farm layout factor
	 */
	private void calculateLayoutFactor() {
		// TODO
	}
	
	
	/**
	 * Calculates the biome factor
	 */
	private void calculateBiomeFactor() {
		// TODO
	}
	
	
	/**
	 * Calculates the farm proximity factor
	 */
	private void calculateProximityFactor() {
		double factor = 1.0;
		
		for (BaseFactory f : FactoryWorker.getInstance().getRunningFactories()) {
			if (!(f instanceof FarmFactory)) {
				continue;
			}
			
			double dist = f.getLocation().distance(this.location);
			
			// The proximity factor will drop proportionally to each farm that is within
			// the exclusion zone
			if (dist < farmProximity) {
				factor = Math.max(0, factor - ((farmProximity - dist) / farmProximity));
			}
			
			// No need to go below zero
			if (factor == 0) {
				break;
			}
		}
		
		this.proximityFactor = factor;
	}
	
	
	/**
	 * Gets the farmed crops
	 * @return The farmed crops
	 */
	public HashMap<CropType, Integer> getFarmedCrops() {
		return this.farmedCrops;
	}
	

}
