package com.gordonfreemanq.sabre.blocks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Utility;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gordonfreemanq.sabre.SabrePlugin;

public class SabreItemStack extends ItemStack {
	
	protected final String name;
	protected final List<String> lore;
	protected Class<? extends SabreBlock> blockClass;
	
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
		
		if (lore != null) {
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
        boolean r2 = getDurability() == stack.getDurability();
        boolean r3 = hasItemMeta() == stack.hasItemMeta();
        boolean r4 = Bukkit.getItemFactory().equals(getItemMeta(), stack.getItemMeta());
        
        return r1 && r2 && r3 && r4;
    }
	
	
	/**
	 * Gets the lore
	 * @return The lore
	 */
	public List<String> getLore() {
		return lore;
	}
	
	
	protected static String parse(String str) {
		return SabrePlugin.getPlugin().txt.parse(str);
	}
	
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	
	
	public void onPlayerInteract(PlayerInteractEvent e) {
		// Do nothing
	}
}
