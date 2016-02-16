package com.gordonfreemanq.sabre.blocks;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.warp.AbstractWarpDrive;
import com.gordonfreemanq.sabre.warp.TeleportPad;

/**
 * A class to hold collection of blocks, organized into groups of chunks
 * @author GFQ
 *
 */
public class WarpCollection extends BlockCollection {
	
	/**
	 * Creates a new instance of WarpCollection
	 */
	public WarpCollection() {
		super();
	}
	
	
	/**
	 * Adds a block to the collection
	 * @param b The block to add
	 */
	@Override
	public void add(SabreBlock b) {
		
		if (b instanceof AbstractWarpDrive || b instanceof TeleportPad) {
			Location l = b.getLocation();
			getChunkMap(l.getChunk()).put(l, b);
		}
	}
}
