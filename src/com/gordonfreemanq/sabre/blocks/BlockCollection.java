package com.gordonfreemanq.sabre.blocks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * A class to hold collection of blocks, organized into groups of chunks
 * @author GFQ
 *
 */
public class BlockCollection {

	protected final HashMap<Chunk, HashMap<Location, SabreBlock>> blocks;
	protected HashSet<BlockCollection> subs;
	
	
	/**
	 * Creates a new BlockCollection instance
	 */
	public BlockCollection() {
		this.blocks = new HashMap<Chunk, HashMap<Location,SabreBlock>>();
		this.subs = new HashSet<BlockCollection>();
	}
	
	
	public void addSub(BlockCollection sub) {
		subs.add(sub);
	}
	
	
	/**
	 * Adds a whole map of chunk data
	 * @param c The chunk to add
	 * @param map The matching block map
	 */
	public void putChunk(Chunk c, HashMap<Location,SabreBlock> map) {
		blocks.put(c, map);
	}
	
	
	/**
	 * Unloads the data for a chunk
	 * @param c The chunk to unload
	 */
	public void unloadChunk(Chunk c) {
		blocks.remove(c);
		
		for (BlockCollection sub : subs) {
			sub.unloadChunk(c);
		}
	}
	
	
	/**
	 * Adds a block to the collection
	 * @param b The block to add
	 */
	public void add(SabreBlock b) {
		Location l = b.getLocation();
		getChunkMap(l.getChunk()).put(l, b);
		
		for (BlockCollection sub : subs) {
			sub.add(b);
		}
	}
	
	
	/**
	 * Removes a block from the collection
	 * @param b The block to remove
	 */
	public void remove(SabreBlock b) {
		Location l = b.getLocation();
		getChunkMap(l.getChunk()).remove(l);
		
		for (BlockCollection sub : subs) {
			sub.remove(b);
		}
	}
	
	
	/**
	 * Gets a block from the colelction
	 * @param l The location
	 * @return The block if it exists
	 */
	public SabreBlock get(Location l) {
		HashMap<Location,SabreBlock> map = getChunkMap(l.getChunk());
		if (map == null) {
			return null;
		}
		
		return map.get(l);
	}
	
	
	/**
	 * Gets the map for a specified chunk
	 * @param c The chunk
	 * @return The chunk map
	 */
	protected HashMap<Location,SabreBlock> getChunkMap(Chunk c) {
		HashMap<Location, SabreBlock> map = blocks.get(c);
		if (map == null) {
			map = new HashMap<Location, SabreBlock>();
			blocks.put(c, map);
		}
		
		return map;
	}
	
	
	/**
	 * Gets all the blocks in a chunk
	 * @param c The chunk
	 * @return The block collections
	 */
	public Collection<SabreBlock> getChunkBlocks(Chunk c) {
		return getChunkMap(c).values();
	}
}
