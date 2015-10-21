package com.gordonfreemanq.sabre.factory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

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
	private final ConfigurationSection customConfig;
	
	/**
	 * Creates a new FactoryProperties instance
	 * @param name The factory name
	 * @param recipes The factory recipes
	 * @param upgrades The factory upgrades
	 * @param customConfig The custom configuration section
	 */
	public FactoryProperties(String name, List<IRecipe> recipes, List<IRecipe> upgrades, ConfigurationSection customConfig)
	{
		this.name = name;
		this.recipes = recipes;
		this.upgrades = upgrades;
		this.customConfig = customConfig;
	}
	
	
	/**
	 * Creates a new FactoryProperties instance
	 * @param name The factory name
	 * @param recipes The factory recipes
	 * @param upgrades The factory upgrades
	 */
	public FactoryProperties(String name, List<IRecipe> recipes, List<IRecipe> upgrades)
	{
		this(name, recipes, upgrades, null);
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
	
	
	/**
	 * Gets the custom configuration
	 * @return The custom configuration
	 */
	public ConfigurationSection getCustomConfig() {
		return this.customConfig;
	}
	
	public FactoryProperties clone()
	{
		try{
			ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
			ArrayList<IRecipe> upgrades = new ArrayList<IRecipe>();
			
			for (IRecipe r : this.recipes) {
				recipes.add(r.clone());
			}
			
			for (IRecipe r : this.upgrades) {
				upgrades.add(r.clone());
			}
			
			FactoryProperties props = new FactoryProperties(name, recipes, upgrades, customConfig);
			return props;
		}
		catch (Error e) {
		throw e;
		}
	}
}
