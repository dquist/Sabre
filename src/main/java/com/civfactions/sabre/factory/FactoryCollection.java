package com.civfactions.sabre.factory;

import org.bukkit.Location;

import com.civfactions.sabre.blocks.BlockCollection;
import com.civfactions.sabre.blocks.SabreBlock;

/**
 * A class to hold collection of blocks, organized into groups of chunks
 * @author GFQ
 *
 */
public class FactoryCollection extends BlockCollection {
	
	/**
	 * Creates a new FactoryCollection instance
	 */
	public FactoryCollection() {
		super();
	}
	
	
	/**
	 * Adds a block to the collection
	 * @param b The block to add
	 */
	@Override
	public void add(SabreBlock b) {
		
		if (b instanceof BaseFactory) {
			Location l = b.getLocation();
			getChunkMap(l.getChunk()).put(l, b);
		}
	}
}
