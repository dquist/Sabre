package com.gordonfreemanq.sabre.factory;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.SabreBlock;

public class FactoryListener implements Listener {

	private final PlayerManager pm;
	private final BlockManager bm;

	public FactoryListener(PlayerManager pm, BlockManager bm) {
		this.pm = pm;
		this.bm = bm;
	}
	
	

	/**
	 * Handles player interaction with a factory
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFactoryInteract(PlayerInteractEvent e) {
		
		// We only care about the stick hit
		if (e.getItem() == null || !e.getItem().getType().equals(Material.STICK)) {
			return;
		}
		
		Action a = e.getAction();
		if (a != Action.RIGHT_CLICK_BLOCK && a != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		// Player has clicked a block with a stick, now check if it is a factory block
		SabreBlock sb = bm.getBlockAt(e.getClickedBlock().getLocation());
		if (sb == null || !(sb instanceof BaseFactory)) {
			// No factory block here
			return;
		}
		
		// Cancel the use block event if hitting with a stick
		if (a == Action.RIGHT_CLICK_BLOCK) {
			e.setCancelled(true);
		}
		
	}
	
	
	/**
	 * Handles controller interaction with a factory
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onControllerInteract(PlayerInteractEvent e) {
		
		// We only care about the stick hit
		if (e.getItem() == null || !e.getItem().getType().equals(Material.STICK)) {
			return;
		}
		
		Action a = e.getAction();
		if (a != Action.RIGHT_CLICK_BLOCK && a != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		
		// Quick check for metadata
		if (!e.getItem().hasItemMeta()) {
			return;
		}
		
		SabrePlayer p = pm.getPlayerById(e.getPlayer().getUniqueId());

		// Try to parse a location
		Location l = FactoryController.parseLocation(p, false);
		
		// No location
		if (l == null) {
			return;
		}
		
		BaseFactory factory = (BaseFactory)bm.getFactories().get(l);
		
		if (factory == null) {
			p.msg(Lang.factoryNotFound);
			AbstractController.normalizeHeldController(p);
			return;
		}
		
		// Not in configuration mode
		if (!factory.getConfigureMode()) {
			return;
		}
		
		e.setCancelled(true);
		
		Block b = e.getClickedBlock();
		if (b.getType() != Material.CHEST && b.getType() != Material.TRAPPED_CHEST) {
			p.msg("<i>Factory source block must be a chest.");
			return;
		}
		
		if (b.getLocation().distance(l) > 25) {
			p.msg("<i>Chest must be within 16 blocks of the factory.");
			return;
		}
		
		if (factory.getInputLocation() == null) {
			factory.setInputLocation(b.getLocation());
			ItemStack is = (new FactoryController(factory)).toItemStack();
			p.getPlayer().getInventory().setItemInHand(is);
			p.msg("<g>You set the factory input.");
			p.msg("<i>Hit a chest for the factory output.");
			return;
		}
		
		if (factory.getOutputLocation() == null) {
			factory.setOutputLocation(b.getLocation());
			p.msg("<g>You set the factory output.");
			p.msg("<i>Hit a chest for the factory fuel source.");
			return;
		}
		
		if (factory.getFuelLocation() == null) {
			factory.setFuelLocation(b.getLocation());
			p.msg("<g>You set the factory fuel soruce.");
		}
		
		ItemStack is = (new FactoryController(factory)).toItemStack();
		p.getPlayer().getInventory().setItemInHand(is);
		
		p.msg("<g>Factory configuration complete.");
		factory.setConfigureMode(false);
		bm.updateSettings(factory);
	}

	
	/**
	 * Called when a block is charged.
	 * When the furnace block is powered, starts the factory and toggles on any attached levers.
	 * On completion, toggles off any attached levers.
	 */
    @EventHandler()
	public void redstoneChange(BlockRedstoneEvent e)
	{
		// Only trigger on transition from 0 to positive
		if (e.getOldCurrent() > 0 || e.getNewCurrent() == 0) {
			return;
		}
		
		Block rsBlock = e.getBlock();
		BlockFace[] directions = null;
		if (rsBlock.getType() == Material.REDSTONE_WIRE) {
			directions = BaseFactory.REDSTONE_FACES;
		} else if (rsBlock.getType() == Material.WOOD_BUTTON) {
			directions = new BlockFace[] {((Attachable) rsBlock.getState().getData()).getAttachedFace()};
		} else if (rsBlock.getType() == Material.STONE_BUTTON) {
			directions = new BlockFace[] {((Attachable) rsBlock.getState().getData()).getAttachedFace()};
		} else if (rsBlock.getType() == Material.LEVER) {
			directions = new BlockFace[] {((Attachable) rsBlock.getState().getData()).getAttachedFace()};
		} else {
			return; // Don't care
		}
		
		
		for (BlockFace direction : directions) {
			Block block = rsBlock.getRelative(direction);
			
			//Is the block part of a factory?
			if(block.getType() == Material.FURNACE || block.getType() == Material.BURNING_FURNACE)
			{
				BaseFactory factory = (BaseFactory)bm.getFactories().get(block.getLocation());
				
				if (factory == null) {
					return;
				}
				
				Block lever = factory.findActivationLever();
				if (lever == null) {
					// No lever - don't respond to redstone
					return;
				}
				
				if (!factory.getRunning()) {
					// Try to start the factory
					factory.cyclePower(null);
				}
			}
		}
	}
}
