package com.gordonfreemanq.sabre.blocks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.mongodb.BasicDBObject;

public class SabreBlock {
	
	protected final Location location;
	protected String name;
	protected String typeName = "Block";
	protected Reinforcement reinforcememnt;
	
	// Whether the block should tell it's type when interacted with
	protected boolean tellBlockName;
	
	// Whether a player needs access to see the block name
	protected boolean requireAccessForName;
	
	protected boolean hasEffectRadius;

	/**
	 * Creates a new BlockRecord instance
	 * @param location The block location
	 */
	public SabreBlock(Location location, String typeName) {
		this.location = location;
		this.hasEffectRadius = false;
		this.tellBlockName = true;
		this.requireAccessForName = false;
		this.name = "";
		
		if (typeName != null) {
			this.typeName = typeName;
		}
	}
	

	public SabreBlock(Location location) {
		this(location, null);
	}
	
	/**
	 * Gets the block location
	 * @return The block location
	 */
	public Location getLocation() {
		return this.location;
	}
	
	
	/**
	 * Gets the Sabre block type
	 * @return
	 */
	public String getTypeName() {
		return typeName;
	}
	
	
	/**
	 * Gets whether a particular location is affected by this block
	 * @param l The location to check
	 * @return true if this block affects the location
	 */
	public boolean affectsLocation(Location l) {
		return false;
	}
	
	
	/**
	 * Whether the block should tell the player it's type name
	 * @return true if the type name should be displayed
	 */
	public boolean getTellBlockName() {
		return this.tellBlockName;
	}
	
	
	/**
	 * Whether access is required to see the block name
	 * @return true if access is required
	 */
	public boolean getRequireAccessForName() {
		return this.requireAccessForName;
	}
	
	
	/**
	 * Gets the display name of the block
	 * @return The block's display name
	 */
	public String getDisplayName() {
		return this.name;
	}
	
	
	/**
	 * Sets the block's display name
	 * @param name The new display name
	 */
	public void setDisplayName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Checks if the block has a display name
	 * @return true if it has a display name
	 */
	public boolean hasDisplayName() {
		return !name.equals("");
	}
	
	
	/**
	 * Gets the reinforcement record for this block
	 * @return The reinforcement instance if it exists
	 */
	public Reinforcement getReinforcement() {
		return this.reinforcememnt;
	}
	
	
	/**
	 * Sets the reinforcement record for this block
	 * @param reinforcememnt The new reinforcement record
	 */
	public void setReinforcement(Reinforcement reinforcememnt) {
		this.reinforcememnt = reinforcememnt;
	}	
	
	
	/**
	 * Gets the group name if it exists
	 * @return The group name
	 */
	public String getGroupName() {
		if (this.reinforcememnt != null) {
			return this.reinforcememnt.getGroup().getName();
		}
		return "";
	}
	
	
	/**
	 * Gets whether a player can access this block
	 * @param p The player to check
	 * @return true if the player can access it
	 */
	public boolean canPlayerAccess(SabrePlayer p) {
		if (this.reinforcememnt != null) {
			return this.reinforcememnt.getGroup().isMember(p);
		}
		return true;
	}
	
	
	/**
	 * Gets whether a player can modify this block
	 * @param p The player to check
	 * @return true if the player can access it
	 */
	public boolean canPlayerModify(SabrePlayer p) {
		if (this.reinforcememnt != null) {
			return this.reinforcememnt.getGroup().isBuilder(p);
		}
		return true;
	}
	
	
	/**
	 * Creates an item stack from the block
	 * @param amount The number to create
	 * @return The new item stack
	 */
	public ItemStack createItemStack(Material m, int amount) {
		return CustomItems.getInstance().getByName(this.getTypeName());
	}
	
	
	/**
	 * Creates a new basic block instance
	 * @param m The block material
	 * @param amount The block amount
	 * @return The new block instance
	 */
	public static ItemStack createBasicBlock(Material m, int amount) {
		ItemStack is = new ItemStack(m, amount);
		return is;
	}
	
	
	/**
	 * Creates the seed lore for the block
	 * @param typeName The block type name
	 * @param seedLore The lore
	 * @return The formatted lore
	 */
	protected static List<String> createItemSeedLore(String typeName, String[] seedLore) {
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", typeName));
		
		for (int i = 0; i < seedLore.length; i++) {
			lore.add(parse(seedLore[i]));
		}
		
		return lore;
	}
	
	
	/**
	 * Parses colored text
	 * @param str The string to parse
	 * @return The parsed string
	 */
	protected static String parse(String str) {
		return SabrePlugin.getPlugin().txt.parse(str);
	}
	
	
	/**
	 * Parses colored text
	 * @param str The string to parse
	 * @param args The string args
	 * @return The parsed string
	 */
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	
	
	/** 
	 * A quick way to check if this is anything except a basic block
	 * @return true if it is a special block
	 */
	public boolean isSpecial() {
		return false;
	}
	
	
	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	public BasicDBObject getSettings() {
		return null;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		// Do nothing
	}
	
	
	/**
	 * Saves settings for the block
	 */
	public void saveSettings() {
		BlockManager.getInstance().updateSettings(this);
	}
	
	
	/**
	 * Handles interaction
	 * @param p The player interacting
	 */
	public void onInteract(PlayerInteractEvent e, SabrePlayer p) {
		// Do nothing
	}
	

	/**
	 * Handles interacting with a stick
	 * @param p The player interacting
	 */
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer p) {
		// Do nothing
	}
	
	
	/**
	 * Handles the block breaking, low priority event
	 * @param e The event args
	 */
	public void onBlockBreaking(SabrePlayer p, BlockBreakEvent e) {
		// Do nothing
	}
	
	
	/**
	 * Handles the block broken event
	 * @param e The event args
	 */
	public void onBlockBroken(SabrePlayer p, BlockBreakEvent e) {
		// Do nothing
	}
	
}
