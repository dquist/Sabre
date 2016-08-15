package com.civfactions.sabre.prisonpearl;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class BlockHolder implements IItemHolder {

	private final Block b;
	private String name;
	
	public BlockHolder(Block b) {
		this.b = b;
		
		switch (b.getType()) {
		case CHEST:
		case TRAPPED_CHEST:
			this.name = "a chest";
			break;
			
		case FURNACE:
			this.name =  "a furnace";
			break;
			
		case BREWING_STAND:
			this.name =  "a brewing stand";
			break;
			
		case DISPENSER:
			this.name =  "a dispenser";
			break;
			
		case ITEM_FRAME:
			this.name =  "a wall frame";
			break;
			
		case DROPPER:
			this.name =  "a dropper";
			break;
			
		case HOPPER:
			this.name =  "a hopper";
			break;
			
		case ENDER_CHEST:
			this.name =  "a chest";
			break;
			
		default:
			this.name = "a block";
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Location getLocation() {
		return b.getLocation();
	}
	
	public Block getBlock() {
		return this.b;
	}
}
