package com.gordonfreemanq.sabre.blocks;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.customitems.SecureSign;

/**
 * A class to hold collection of blocks, organized into groups of chunks
 * @author GFQ
 *
 */
public class SignCollection extends BlockCollection {
	
	/**
	 * Creates a new SignCollection instance
	 */
	public SignCollection() {
		super();
	}
	
	
	/**
	 * Adds a block to the collection
	 * @param b The block to add
	 */
	@Override
	public void add(SabreBlock b) {
		
		if (b instanceof SecureSign) {
			Location l = b.getLocation();
			getChunkMap(l.getChunk()).put(l, b);
		}
	}
}
