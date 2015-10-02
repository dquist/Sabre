package com.gordonfreemanq.sabre.snitch;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import com.gordonfreemanq.sabre.blocks.BlockCollection;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.snitch.QTBox;
import com.gordonfreemanq.sabre.snitch.SparseQuadTree;

/**
 * A class to hold collection of blocks, organized into groups of chunks
 * @author GFQ
 *
 */
public class SnitchCollection extends BlockCollection {
	
	public static final int EXIT_PADDING = 2;
	
    private final HashMap<World, SparseQuadTree> snitches;
	
	/**
	 * Creates a new SignCollection instance
	 */
	public SnitchCollection() {
		super();
		
		snitches = new HashMap<World, SparseQuadTree>(EXIT_PADDING);
	}
	
	@Override
	public void putChunk(Chunk c, HashMap<Location,SabreBlock> map) {
		for (SabreBlock b : map.values()) {
			add(b);
		}
	}
	
	
	/**
	 * Adds a block to the collection
	 * @param b The block to add
	 */
	@Override
	public void add(SabreBlock b) {
		
		if (b instanceof Snitch) {
			Snitch snitch = (Snitch)b;
			World world = snitch.getLocation().getWorld();
			
			if (snitches.get(world) == null) {
	            SparseQuadTree map = new SparseQuadTree(EXIT_PADDING);
	            map.add(snitch);
	            snitches.put(world, map);
	        } else {
	            snitches.get(world).add(snitch);
	        }
			
			World w = b.getLocation().getWorld();
			snitches.get(w);
		}
	}
	
	
	/**
	 * Removes a block from the collection
	 * @param b The block to remove
	 */
	@Override
	public void remove(SabreBlock b) {
		
		if (b instanceof Snitch) {
			Snitch snitch = (Snitch)b;
			SparseQuadTree tree = this.getWorldSnitches(b.getLocation().getWorld());
			tree.remove(snitch);
		}
	}
	
	
	/**
	 * Gets the tree for a specified world
	 * @param w The world
	 * @return The snitch tree
	 */
	public SparseQuadTree getWorldSnitches(World w) {
		SparseQuadTree tree = snitches.get(w);
		if (tree == null) {
			tree = new SparseQuadTree();
			snitches.put(w, tree);
		}
		
		return tree;
	}
	
	
	/**
	 * Finds snitches for a loction
	 * @param world The world to search
	 * @param location
	 * @return
	 */
	public Set<Snitch> findSnitches(Location location) {
        return findSnitches(location, false);
    }

	
	/**
	 * Finds the snitches for a location
	 * @param l The location to check
	 * @param includePaddingZone whether to include the padding histerisis
	 * @return The set of snitches
	 */
    public Set<Snitch> findSnitches(Location l, boolean includePaddingZone) {
    	World w = l.getWorld();
        int y = l.getBlockY();
    	
    	SparseQuadTree tree = this.getWorldSnitches(w);
    	
        Set<Snitch> results = new TreeSet<Snitch>();
        Set<QTBox> found = tree.find(l.getBlockX(), l.getBlockZ(), includePaddingZone);
        
        for (QTBox box : found) {
            Snitch sn = (Snitch) box;
            if (sn.isWithinHeight(y)) {
                results.add(sn);
            }
        }
        return results;
    }
	
	
}
