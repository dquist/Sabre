package com.gordonfreemanq.sabre.factory;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlayer;

/**
 * Class that provides core functionality for all the farm factories
 * @author GFQ
 *
 */
public class FarmFactory extends BaseFactory {

	/**
	 * Creates a new FarmFactory instance
	 * @param location The factory location
	 * @param typeName The factory name
	 */
	public FarmFactory(Location location, String typeName) {
		super(location, typeName);
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
				if (recipe instanceof FactoryRecipe) {
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
		// TODO
	}
	
	
	public void survey() {
		// TODO
	}

}
