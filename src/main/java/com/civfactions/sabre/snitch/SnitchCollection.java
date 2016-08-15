package com.civfactions.sabre.snitch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockCollection;
import com.civfactions.sabre.blocks.SabreBlock;
import com.civfactions.sabre.snitch.QTBox;
import com.civfactions.sabre.snitch.SparseQuadTree;

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
	 * Finds snitches for a location
	 * @param world The world to search
	 * @param location
	 * @return The set of snitches
	 */
	public Set<Snitch> findSnitches(Location location) {
        return findSnitches(location, false);
    }

	
	/**
	 * Finds the snitches for a location
	 * @param l The location to check
	 * @param includePaddingZone whether to include the padding hysteresis
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
	
	
	/**
	 * Searches for a snitch around a player
	 * @param player The player
	 * @return The snitch if found
	 */
    public Snitch getSnitchUnderCursor(SabrePlayer sp) {
        Iterator<Block> itr = new BlockIterator(sp.getPlayer(), 40); // Within 2.5 chunks
        while (itr.hasNext()) {
            final Block block = itr.next();
            final Material mat = block.getType();
            if (mat != Material.JUKEBOX) {
                continue;
            }
            
            SabreBlock sb = SabrePlugin.instance().getBlockManager().getBlockAt(block.getLocation());
            if (sb instanceof Snitch) {
            	return (Snitch)sb;
            }
        }
        return null;
    }
    
    
    /**
     * Checks if a snitch block actually exists
     * @param snitch The snitch to check
     * @param cleanup Whether to remove any ghost snitches
     * @return true if the snitch block exists
     */
    public boolean doesSnitchExist(Snitch snitch, boolean cleanup) {
    	
    	Location l = snitch.getLocation();
    	if (l.getBlock().getType().equals(Material.JUKEBOX)) {
    		return true;
    	} else if (cleanup) {
            int x = l.getBlockX();
            int y = l.getBlockY();
            int z = l.getBlockZ();
            SabrePlugin.log("Removing ghost snitch '" + snitch.getDisplayName() + "' at x:" + x + " y:" + y + " z:" + z);
            this.remove(snitch);
    	}
        return false;
    }
    
    
    /**
     * Finds the closest owned snitch
     * @param player The player
     * @return The closest snitch, or null if one doesn't exist
     */
    public Snitch findClosestOwnedSnitch(SabrePlayer sp) {
        Snitch closestSnitch = null;
        double closestDistance = Double.MAX_VALUE;
        Location playerLoc = sp.getPlayer().getLocation();
        
        Set<Snitch> snitches = this.findSnitches(playerLoc);
        for (final Snitch snitch : snitches) {
            if (doesSnitchExist(snitch, true) && snitch.canPlayerAccess(sp)) {
                double distance = snitch.getLocation().distanceSquared(playerLoc);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestSnitch = snitch;
                }
            }
        }
        return closestSnitch;
    }
	
	
    /**
     * Finds the targeted snitch
     * @param sp The player
     * @return The snitch if it exists
     */
    public Snitch findTargetedOwnedSnitch(SabrePlayer sp) {
        Snitch snitch = getSnitchUnderCursor(sp);
        if (snitch != null && doesSnitchExist(snitch, true) && snitch.canPlayerAccess(sp)) {
            return snitch;
        }
        return findClosestOwnedSnitch(sp);
    }
}
