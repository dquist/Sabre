package com.civfactions.sabre.blocks;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;

public class SabreItemStack extends ItemStack {
	
	protected final String name;
	protected List<String> lore;
	protected Class<? extends SabreBlock> blockClass;
	protected final Set<SabreItemStack> subItems;
	
	/**
	 * Creates a new SabreItemStack instance
	 * @param type The material type
	 * @param name The item name
	 * @param the The stack amount
	 */
	public SabreItemStack(Material type, String name, int amount, int durability, List<String> lore) {
		super(type, amount, (short)durability);
		this.name = name;
		this.lore = lore;
		this.subItems = new HashSet<SabreItemStack>();
		
		if (this.lore == null) {
			this.lore = this.getLore();
		}
		
		if (this.lore != null) {
			ItemMeta im = this.getItemMeta();
			im.setDisplayName(name);
			im.setLore(this.getLore());
			this.setItemMeta(im);
		}
	}
	
	public SabreItemStack(Material type, String name, int amount, List<String> lore) {
		this(type, name, amount, 0, lore);
	}
	
	public SabreItemStack(Material type, String name, int amount) {
		this(type, name, amount, 0, null);
	}
	
	public SabreItemStack(Material type, String name, int amount, int durability) {
		this(type, name, amount, durability, null);
	}
	
	/**
	 * Gets the name of the controller
	 * @return The name of the controller
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets the class value
	 * @return The class value
	 */
	public Class<? extends SabreBlock> getBlockClass() {
		return this.blockClass;
	}
	
	
	/**
	 * Sets the block class
	 * @param blockClass The block class type
	 */
	public void setBlockClass(Class<? extends SabreBlock> blockClass) {
		this.blockClass = blockClass;
	}
	
	/**
	 * Gets the substitute items
	 */
	public Set<SabreItemStack> getSubstitutes() {
		return this.subItems;
	}
	
	/**
	 * Adds a substitute item
	 * @param item
	 */
	public void addSubstitute(SabreItemStack item) {
		this.subItems.add(item);
	}
	
	public boolean isSubstitute(ItemStack stack) {
		for (SabreItemStack item : this.subItems) {
			if (item.getType() == stack.getType()) {
				if (item.getDurability() == stack.getDurability()
						|| item.getDurability() == -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	public SabreItemStack clone()
	{
		try{
			SabreItemStack namedItemStack = (SabreItemStack) super.clone();
			return namedItemStack;
		}
		catch (Error e) {
		throw e;
		}
	}
	
	
	public String toString()
	{
		return String.valueOf(getAmount()) + " " + name;
	}
	
	public String getCommonName()
	{
		return name;
	}
	
	
    /**
     * This method is the same as equals, but does not consider stack size
     * (amount).
     *
     * @param stack the item stack to compare to
     * @return true if the two stacks are equal, ignoring the amount
     */
    @Utility
    public boolean isSimilar(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack == this) {
            return true;
        }
        
        boolean r1 = getType() == stack.getType();
        boolean r2 = getDurability() == stack.getDurability()
        				|| getDurability() == -1
        				|| stack.getDurability() == -1;
        boolean r3 = hasItemMeta() == stack.hasItemMeta();
        boolean r4 = Bukkit.getItemFactory().equals(getItemMeta(), stack.getItemMeta());
        
        return (r1 && r2 && r3 && r4) || (isSubstitute(stack) && r3 && r4);
    }
	
	
	/**
	 * Gets the lore
	 * @return The lore
	 */
	public List<String> getLore() {
		return lore;
	}
	
	
	/**
	 * Updates the lore on the item stack
	 */
	public void updateLore() {
		List<String> lore = this.getLore();
		if (lore != null) {
			ItemMeta im = this.getItemMeta();
			im.setDisplayName(name);
			im.setLore(this.getLore());
			this.setItemMeta(im);
		}
	}
	
	
	protected static String parse(String str) {
		return SabrePlugin.instance().txt().parse(str);
	}
	
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	
	
	public void onPlayerInteract(IPlayer sp, PlayerInteractEvent e) {
		// Do nothing
	}
}
