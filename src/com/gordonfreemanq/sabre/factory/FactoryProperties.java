package com.gordonfreemanq.sabre.factory;

import java.util.List;


/**
 * Holds information about a factory
 * @author GFQ
 */
public class FactoryProperties
{
	private final String name;
	private final List<FactoryRecipe> recipes;
	private final List<FactoryRecipe> upgrades;
	
	/**
	 * Creates a new FactoryProperties instance
	 * @param name The factory name
	 * @param recipes The factory recipes
	 * @param upgrades The factory upgrades
	 */
	public FactoryProperties(String name, List<FactoryRecipe> recipes, List<FactoryRecipe> upgrades)
	{
		this.name = name;
		this.recipes = recipes;
		this.upgrades = upgrades;
	}
	
	
	/**
	 * Gets the factory name
	 * @return The factory name
	 */
	public String getName()
	{
		return name;
	}
	
	
	/**
	 * Gets the list of recipes
	 * @return The recipes
	 */
	public List<FactoryRecipe> getRecipes() {
		return this.recipes;
	}
	
	
	/**
	 * Gets the list of upgrades
	 * @return The upgrades
	 */
	public List<FactoryRecipe> getUpgrades() {
		return this.upgrades;
	}
}
