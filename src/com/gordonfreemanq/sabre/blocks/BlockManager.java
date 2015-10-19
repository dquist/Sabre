package com.gordonfreemanq.sabre.blocks;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.logging.Level;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.base.CharMatcher;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.customitems.SecureSign;
import com.gordonfreemanq.sabre.data.IDataAccess;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.FactoryCollection;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.snitch.SnitchCollection;

public class BlockManager {

	private final IDataAccess db;
	private final BlockCollection allBlocks;
	private final SignCollection secureSigns;
	private final SnitchCollection snitches;
	private final FactoryCollection factories;
	
	public SignCollection getSigns() {
		return secureSigns;
	}
	
	public SnitchCollection getSnitches() {
		return snitches;
	}
	
	public FactoryCollection getFactories() {
		return factories;
	}
	
	private static BlockManager instance;
	
	public static BlockManager getInstance() {
		return instance;
	}


	/**
	 * Creates a  new BlockManager instance
	 * @param db
	 */
	public BlockManager(IDataAccess db) {
		this.db = db;
		this.allBlocks = new BlockCollection();
		
		// Holds the secure signs
		this.secureSigns = new SignCollection();
		this.snitches = new SnitchCollection();
		this.factories = new FactoryCollection();
		allBlocks.addSub(secureSigns);
		allBlocks.addSub(snitches);
		allBlocks.addSub(factories);
		
		instance = this;
	}
	
	
	/**
	 * Loads all the block data for a chunk
	 */
	public void loadChunk(Chunk c) {
		
		HashMap<Location, SabreBlock> all = new HashMap<Location, SabreBlock>();
		HashMap<Location, SabreBlock> signs = new HashMap<Location, SabreBlock>();
		HashMap<Location, SabreBlock> snitches = new HashMap<Location, SabreBlock>();
		HashMap<Location, SabreBlock> factories = new HashMap<Location, SabreBlock>();
		Location l;
		
		for (SabreBlock b : db.blockGetChunkRecords(c)) {
			l = b.getLocation();
			all.put(l, b);
			
			if (b.isSpecial()) { 
				if (b instanceof SecureSign) {
					signs.put(l, b);
				} else if (b instanceof Snitch) {
					snitches.put(l,  b);
				} else if (b instanceof BaseFactory) {
					factories.put(l, b);
				}
			}
		}
		
		allBlocks.putChunk(c, all);
		secureSigns.putChunk(c, signs);
		this.snitches.putChunk(c, snitches);
		this.factories.putChunk(c, factories);
	}
	
	
	/**
	 * Unloads all the block data for a chunk
	 */
	public void unloadChunk(Chunk c) {
		
		allBlocks.unloadChunk(c);
	}


	/**
	 * Adds a new block record
	 * @param b The block to add
	 */
	public void addBlock(SabreBlock b) {
		allBlocks.add(b);
		db.blockInsert(b);
	}


	/**
	 * Gets a block record at a location
	 * @param l The location
	 * @return The block record, if it exists
	 */
	public SabreBlock getBlockAt(Location l) {
		return allBlocks.get(l);
	}
	
	
	/**
	 * Removes a block record
	 * @param b The block record to remove
	 */
	public void removeBlock(SabreBlock b) {
		allBlocks.remove(b);
		db.blockRemove(b);
	}
	
	
	/**
	 * Creates a new block instance from an item
	 * @return The new block instance
	 */
	public static SabreBlock createBlockFromItem(ItemStack is, Location l) {
		
		SabreBlock b = null;
		String displayName = "";
		String typeName = "block";
		
		if (is.hasItemMeta()) {
			ItemMeta im = is.getItemMeta();
			
			// Check display name
			if (im.hasDisplayName()) {
				displayName = im.getDisplayName();
			}
			
			// Check lore
			if (im.hasLore()) {
				// The type name for special blocks is always the first line
				typeName = is.getItemMeta().getLore().get(0);
				typeName = ChatColor.stripColor(typeName);
				
				// Remove any color formatting
				if (!typeName.equals(CharMatcher.ASCII.retainFrom(typeName))) {
					typeName = typeName.substring(2);
				}
			}
			
			b = blockFactory(l, typeName);
			if (b != null) {
				b.setDisplayName(displayName);
			}
		}
		
		return b;
	}
	
	
	/**
	 * Block factory
	 * @param l The block location
	 * @param name The type name
	 * @return The new instance
	 */
	public static SabreBlock blockFactory(Location l, String typeName) {
		
		
		if (typeName.equalsIgnoreCase("block")) {
			return new SabreBlock(l, typeName);
		}
		
		Class<? extends SabreBlock> blockClass = CustomItems.getInstance().getItemClass(typeName);
		
		// Check if the class exists
		if (blockClass == null) {
			return null;  // Not found
		}
		
		try {

			Class<?>[] types = { Location.class, String.class };
			Constructor<? extends SabreBlock> c = blockClass.getConstructor(types);
			
			Object[] parameters = { l, typeName };
			SabreBlock blockInstance = c.newInstance(parameters);
			return blockInstance;
			
		} catch (Exception ex) {
			SabrePlugin.getPlugin().log(Level.SEVERE, "Failed to create block instance of type %s.", typeName);
			return null;
		}
	}
	
	
	/**
	 * Sets the reinforcement record for a block
	 * @param b The block to update
	 * @param r The reinforcement to set
	 */
	public void setReinforcement(SabreBlock b, Reinforcement r) {
		b.setReinforcement(r);
		db.blockSetReinforcement(b);
	}
	
	
	/**
	 * Updates the reinforcement strength
	 * @param b The block to update
	 */
	public void updateReinforcementStrength(SabreBlock b) {
		db.blockUpdateReinforcementStrength(b);
	}
	
	
	/**
	 * Updates the settings for a block
	 * @param b The block to update
	 */
	public void updateSettings(SabreBlock b) {
		db.blockSetSettings(b);
	}
	
	
	/**
	 * Checks if a player can access a specific block
	 * @param p The player to check
	 * @param b The block to check
	 * @return true if access is permitted
	 */
	public boolean playerCanAccessBlock(SabrePlayer p, SabreBlock b) {
		if (b == null) {
			return true;
		}
		
		Reinforcement r = b.getReinforcement();
		if (r == null) {
			return true;
		}
		
		if (r.getPublic()) {
			return true;
		}
		
		SabreGroup g = r.getGroup();
		return g.isMember(p);
	}
	
	
	/**
	 * Checks if a player can access a specific block
	 * @param p The player to check
	 * @param b The block to check
	 * @return true if access is permitted
	 */
	public boolean playerCanAccessBlock(SabrePlayer p, Block b) {
		return this.playerCanAccessBlock(p, getBlockAt(b.getLocation()));
	}
	
	
	/**
	 * Checks if a player can break a specific block
	 * @param p The player to check
	 * @param b The block to check
	 * @return true if access is permitted
	 */
	public boolean playerCanBreakBlock(SabrePlayer p, SabreBlock b) {
		if (b == null) {
			return true;
		}
		
		Reinforcement r = b.getReinforcement();
		if (r == null) {
			return true;
		}
		
		SabreGroup g = r.getGroup();
		SabreMember m = g.getMember(p);
		if (m != null && m.canBuild()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks if a player can break a specific block
	 * @param p The player to check
	 * @param b The block to check
	 * @return true if access is permitted
	 */
	public boolean playerCanModifyBlock(SabrePlayer p, Block b) {
		return this.playerCanBreakBlock(p, getBlockAt(b.getLocation()));
	}
	
	
	/**
	 * Loads all the factory blocks
	 */
	public void loadAllFactories() {
		
	}
}
