package com.gordonfreemanq.sabre.factory;

import java.util.List;

import com.gordonfreemanq.sabre.factory.recipe.IRecipe;


/**
 * Holds information about a factory
 * @author GFQ
 */
public class FactoryProperties
{
	private final String name;
	private final List<IRecipe> recipes;
	private final List<IRecipe> upgrades;
	
	/**
	 * Creates a new FactoryProperties instance
	 * @param name The factory name
	 * @param recipes The factory recipes
	 * @param upgrades The factory upgrades
	 */
	public FactoryProperties(String name, List<IRecipe> recipes, List<IRecipe> upgrades)
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
	public List<IRecipe> getRecipes() {
		return this.recipes;
	}
	
	
	/**
	 * Gets the list of upgrades
	 * @return The upgrades
	 */
	public List<IRecipe> getUpgrades() {
		return this.upgrades;
	}
}
