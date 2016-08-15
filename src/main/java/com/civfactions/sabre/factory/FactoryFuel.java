package com.civfactions.sabre.factory;

import com.civfactions.sabre.blocks.SabreItemStack;


/**
 * Represents a fuel type for a factory
 * @author GFQ
 */
public class FactoryFuel {

	private final ItemList<SabreItemStack> items;
	private final ItemList<SabreItemStack> returnItems;
	private int energy;
	
	/**
	 * Creates a new FactoryFuel instance
	 * @param items
	 * @param energy
	 */
	public FactoryFuel(ItemList<SabreItemStack> items, int energy) {
		this.items = items;
		this.energy = energy;
		this.returnItems = new ItemList<SabreItemStack>();
	}
	
	
	/**
	 * Creates a new FactoryFuel instance
	 * @param items
	 * @param energy
	 */
	public FactoryFuel(SabreItemStack item, int energy) {
		this.items = new ItemList<SabreItemStack>();
		this.returnItems = new ItemList<SabreItemStack>();
		this.items.add(item);
		this.energy = energy;
	}
	
	
	/**
	 * Gets the fuel items
	 * @return The fuel item stacks
	 */
	public ItemList<SabreItemStack> getItems() {
		return this.items;
	}
	
	
	/**
	 * Gets the energy level for this fuel
	 * @return The energy level
	 */
	public int getEnergy() {
		return this.energy;
	}
	
	
	/**
	 * Gets the return items
	 * @return The fuel return item stacks
	 */
	public ItemList<SabreItemStack> getReturnItems() {
		return this.returnItems;
	}
	
	
	/**
	 * Adds a new return item
	 * @param stack The stack to add
	 */
	public void addReturnItem(SabreItemStack stack) {
		this.returnItems.add(stack);
	}
}
