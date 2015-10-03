package com.gordonfreemanq.sabre.mining;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public class MiningListener implements Listener {

	private final SabreConfig config;


	public MiningListener(SabreConfig config) {
		this.config = config;
	}
	
	
	/**
	 * Prevent placing blocks directly under you
	 * @param e
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		
		Player p = e.getPlayer();
		Entity ent = (Entity)p;
		
		if (!ent.isOnGround()) {
			e.setCancelled(true);
			return;
		}
		
		Location under = p.getLocation().getBlock().getLocation().subtract(0, 1, 0);
		
		if (under.getBlock().getType() == Material.AIR) {
			e.setCancelled(true);
			return;
		}
		
		Block b = e.getBlock();
		
		if (under.getBlock() == b) {
			e.setCancelled(true);
			return;
		}
		
		if (b.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
			return;
		}
		
		Material m = b.getType();
		
		if (m.equals(Material.COBBLESTONE) || m.equals(Material.DIRT)) {
			e.getBlock().getWorld().spawnFallingBlock(b.getLocation(), m, b.getData());
			b.setType(Material.AIR);
		}
	}
	



	/**
	 * Deal extra tool durability based on Y level
	 * @param e
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onMiningEvent(BlockBreakEvent e) {
		Block b = e.getBlock();

		switch (b.getType()) {
		case STONE:
		case OBSIDIAN:
		case IRON_ORE:
		case DIAMOND_ORE:
		case REDSTONE_ORE:
		case LAPIS_ORE:
		case EMERALD_ORE:
			ItemStack is = e.getPlayer().getItemInHand();
			int dura = this.getDurability(b);
			if (isPickaxe(is.getType())) {
				is.setDurability((short) (is.getDurability() + dura));
			}
			break;

		default:
			break;
		}
	}


	static double MAX_DURA = 50;


	public int getDurability(Block b) {
		String biomeName = b.getBiome().toString();
		double dura = 1;
		int groundLevel = 100;
		int y = b.getY();

		if (biomeName.equalsIgnoreCase("IceMountains")) {
			groundLevel = 250;
		}

		groundLevel -= groundLevel * 0.25;

		if (y < groundLevel) {
			dura += (groundLevel - y) * (MAX_DURA / groundLevel);
		}

		return (int) dura;

	}


	public boolean isPickaxe(Material m) {
		switch(m) {
		case WOOD_PICKAXE:
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLD_PICKAXE:
		case DIAMOND_PICKAXE:
			return true;

		default:
			return false;
		}
	}


	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFallBelowBedrock(PlayerMoveEvent e) {

		if (e.getPlayer().getWorld().equals(PearlManager.getInstance().getPrisonWorld())) {
			return;
		}
		
		Location l = e.getPlayer().getLocation();
		if (l.getBlockY() < -2) {
			
			// Falling below bedrock
			SabreUtil.tryToTeleport(e.getPlayer(), l.add(0, 10, 0));
		}
	}
	
	
	
	 @EventHandler
    public void craftItem(PrepareItemCraftEvent e) {
		ItemStack result = e.getRecipe().getResult();
        
        for(SabreItemStack is :config.getDisabledRecipes()) {
        	if (is.isSimilar(result)) {
            	e.getInventory().setResult(new ItemStack(Material.AIR));
        	}
        }
    }
}
