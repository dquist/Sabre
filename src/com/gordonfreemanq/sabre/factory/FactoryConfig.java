package com.gordonfreemanq.sabre.factory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.factory.recipe.ProductionRecipe;
import com.gordonfreemanq.sabre.factory.recipe.FarmRecipe;
import com.gordonfreemanq.sabre.factory.recipe.IRecipe;
import com.gordonfreemanq.sabre.factory.recipe.SpecialRecipeType;

/**
 * Reads all the factory recipes from disk
 * @author GFQ
 */
public class FactoryConfig {

	private File folder = null;
	private final SabrePlugin plugin;
	private HashMap<String, FactoryProperties> factoryProperties;
	
	
	private static FactoryConfig instance;
	
	public static FactoryConfig getInstance() {
		return instance;
	}
	
	/**
	 * Creates a new FactoryConfig instance
	 */
	public FactoryConfig() {
		this.plugin = SabrePlugin.getPlugin();
		
		instance = this;
	}
	
	
	/**
	 * Reloads the recipe configuration
	 */
	public void reload() {
	    if (folder == null) {
	    	folder = new File(plugin.getDataFolder(), "factories");
	    }
	    
	    factoryProperties = new HashMap<String, FactoryProperties>();
	    
	    File[] files = folder.listFiles();
	    
	    for (int i = 0; i < files.length; i++) {
	    	File f = files[i];
	    	
	    	// Ignore non-yml files
	    	if (!f.getName().endsWith(".yml")) {
	    		continue;
	    	}
	    	
	    	// Load the recipes for the file
	    	try {
	    		ArrayList<IRecipe> recipes = new ArrayList<IRecipe>();
	    		ArrayList<IRecipe> upgrades = new ArrayList<IRecipe>();
	    		
		    	FileConfiguration config = YamlConfiguration.loadConfiguration(f);
		    	String factoryName = config.getString("name");
			    loadRecipes(config, recipes, upgrades);
			    
			    FactoryProperties fp = new FactoryProperties(factoryName, recipes, upgrades);
			    
			    if (config.getBoolean("farm", false)) {
			    	//loadFarmRecipes(config, recipes);
			    }
			    
			    factoryProperties.put(factoryName, fp);
			    
	    	} catch (Exception ex) {
	    		plugin.log(Level.SEVERE, "Failed to read factory config file %s", f.getName());
	    	}
	    }

	}
	
	
	/**
	 * Gets the recipes for a factory name
	 * @param name The factory name
	 * @return The factory properties
	 */
	public FactoryProperties getFactoryProperties(String name) {
		return factoryProperties.get(name).clone();
	}
	
	
	
	/**
	 * Loads the recipes for a factory config
	 * @param config The config instance to read
	 * @param recipes the list of production recipes
	 * @param upgrades the list of upgrade recipes
	 */
	private void loadRecipes(FileConfiguration config, List<IRecipe> recipes, List<IRecipe> upgrades) {
		

		int defaultSpeed = config.getInt("production_speed", 1);
		
		// Read production recipes
	    ConfigurationSection section = config.getConfigurationSection("recipes");
	    if (section != null) {
		    readRecipeSection(recipes, section, defaultSpeed);
	    }
	    
	    // Read upgrade recipes
	    section = config.getConfigurationSection("upgrades");
	    if (section != null) {
		    readRecipeSection(upgrades, section, defaultSpeed);
	    }
	}
	
	
	/**
	 * Loads the farm recipes
	 * @param config The config instance to read
	 * @param rate the list of farm recipes
	 */
	private void loadFarmRecipes(FileConfiguration config, List<IRecipe> farmRecipes) {
		
		ConfigurationSection section = config.getConfigurationSection("farm_recipes");
		
		for (String recipeName : section.getKeys(false)) {

			ConfigurationSection configSection = section.getConfigurationSection(recipeName);
			
			CropType cropType = CropType.valueOf(configSection.getString("crop"));
			if (cropType == null) {
				continue;
			}
			
			int rate = configSection.getInt("rate", 0);
			farmRecipes.add(new FarmRecipe(recipeName, cropType, rate));
		}
	}
	
	
	/**
	 * Reads a recipe section
	 * @param recipes The recipe list
	 * @param section The section to read
	 * @param defaultSpeed The default run speed
	 */
	private void readRecipeSection(List<IRecipe> recipes, ConfigurationSection section, int defaultSpeed) {
		Iterator<String> recipeTitles = section.getKeys(false).iterator();
		while (recipeTitles.hasNext())
		{
			// Section header in recipe file, also serves as unique identifier for the recipe
			String recipeName = recipeTitles.next();
			ConfigurationSection configSection = section.getConfigurationSection(recipeName);
			
			// Production time of the recipe
			int productionSpeed = configSection.getInt("production_speed", defaultSpeed);
			int fuelCost = configSection.getInt("fuel_cost", 2);
			
			// Is it a special recipe, then look up the class type and create a new instance
			if (configSection.contains("special")) {
				SpecialRecipeType specialType = SpecialRecipeType.valueOf(configSection.getString("special"));
				if (specialType != null) {
					IRecipe recipe = specialType.createRecipe(recipeName, productionSpeed, fuelCost);
					if (recipe != null) {
						recipes.add(recipe);
					}
					break;
				}
			}
			
			// Inputs of the recipe, empty of there are no inputs
			ItemList<SabreItemStack> inputs = getItems(configSection.getConfigurationSection("inputs"));
			
			// Outputs of the recipe, empty of there are no inputs
			ItemList<SabreItemStack> outputs = getItems(configSection.getConfigurationSection("outputs"));
			
			// Enchantments of the recipe, empty of there are no inputs
			List<ProbabilisticEnchantment> enchants = getEnchantments(configSection.getConfigurationSection("enchantments"));
			
			ProductionRecipe recipe = new ProductionRecipe(recipeName, productionSpeed, fuelCost, inputs, outputs, enchants);
			recipes.add(recipe);
		}
	}
	
	
	/**
	 * Gets the items for a section
	 * @param configItems The config section
	 * @return The collection of named items
	 */
	public ItemList<SabreItemStack> getItems(ConfigurationSection configItems)
	{
		ItemList<SabreItemStack> items = new ItemList<SabreItemStack>();
		if(configItems != null)
		{
			for(String commonName : configItems.getKeys(false))
			{
				
				ConfigurationSection configItem = configItems.getConfigurationSection(commonName);

				String materialName = "";
				ItemStack sabreStack = null;
				Material material = null;
				short durability = 0;
				String customItem = configItem.getString("custom_item");
				int amount = configItem.getInt("amount",1);
				if (customItem != null) {
					sabreStack = CustomItems.getInstance().getByName(customItem);
					if (sabreStack == null) {
						plugin.getLogger().severe(configItems.getCurrentPath() + " has invalid custom item " + customItem);
						return items;
					}
					
					material = sabreStack.getType();
					durability = sabreStack.getDurability();
				}
				
				if (sabreStack == null) {
					materialName = configItem.getString("material");
					material = Material.getMaterial(materialName);
					if (material == null) {
						plugin.getLogger().severe(configItems.getCurrentPath() + " has invalid material " + materialName);
						return items;
					}
					durability = (short)configItem.getInt("durability", 0);
				}
				
				
				String displayName = configItem.getString("display_name");
				String lore = configItem.getString("lore");
				List<ProbabilisticEnchantment> compulsoryEnchantments = getEnchantments(configItem.getConfigurationSection("enchantments"));
				List<ProbabilisticEnchantment> storedEnchantments = getEnchantments(configItem.getConfigurationSection("stored_enchantments"));
				List<PotionEffect> potionEffects = getPotionEffects(configItem.getConfigurationSection("potion_effects"));
				
				SabreItemStack is = createItemStack(material, amount, durability, displayName, lore, commonName, sabreStack, compulsoryEnchantments, storedEnchantments, potionEffects);
				
				// Get any possible item substitutes
				if (configItem.contains("subs")) {
					
					ConfigurationSection subs = configItem.getConfigurationSection("subs");
					for(String subName : subs.getKeys(false)) {
						ConfigurationSection subItem = subs.getConfigurationSection(subName);
						
						String subMaterialName = subItem.getString("material");
						Material subMaterial = Material.getMaterial(subMaterialName);
						if (subMaterial == null) {
							plugin.getLogger().severe(configItems.getCurrentPath() + " has invalid material " + subMaterial);
							break;
						}
						
						int subDurability = subItem.getInt("durability", 0);
						
						SabreItemStack subStack = new SabreItemStack(subMaterial, subMaterialName, 1, subDurability);
						is.addSubstitute(subStack);
					}
				}
				
				items.add(is);
			}
		}
		return items;
	}
	
	

	/**
	 * Creates a named item stack
	 * @param material The material
	 * @param stackSize The stack size
	 * @param durability The durability
	 * @param name The item name
	 * @param loreString The lore
	 * @param commonName The common name
	 * @param sabreType The sabre type
	 * @param compulsoryEnchants The enchants
	 * @param storedEnchants The stored enchants
	 * @param potionEffects The potion effects
	 * @return The named item stack
	 */
	private SabreItemStack createItemStack(Material material, int stackSize, short durability, String name, 
			String loreString, String commonName, ItemStack sabreStack, List<ProbabilisticEnchantment> compulsoryEnchants, 
			List<ProbabilisticEnchantment> storedEnchants, List<PotionEffect> potionEffects)
	{
		SabreItemStack namedItemStack = new SabreItemStack(material, commonName, stackSize, durability);
		if(sabreStack != null || name != null || loreString != null || compulsoryEnchants.size() > 0 
				|| storedEnchants.size() > 0|| potionEffects.size() > 0)
		{
			// Sabre types override all other lore/name settings
			if (sabreStack != null) {
				namedItemStack.setItemMeta(sabreStack.getItemMeta());
				return namedItemStack;
			}
			
			// Not a sabre type
			ItemMeta meta = namedItemStack.getItemMeta();
			if (name != null) {
				meta.setDisplayName(name);
			}
			if (loreString!=null) {
				List<String> lore = new ArrayList<String>();
				lore.add(loreString);
				meta.setLore(lore);
			}
			
			for (ProbabilisticEnchantment enchant : compulsoryEnchants) {
				meta.addEnchant(enchant.getEnchantment(), enchant.getLevel(), false);
			}
			if (meta instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta esm = (EnchantmentStorageMeta) meta;
				for (ProbabilisticEnchantment enchant : storedEnchants) {
					esm.addStoredEnchant(enchant.getEnchantment(), enchant.getLevel(), false);
				}
			}
			if (meta instanceof PotionMeta) {
				PotionMeta pm = (PotionMeta) meta;
				for (PotionEffect effect : potionEffects) {
					pm.addCustomEffect(effect, true);
				}
			}
			namedItemStack.setItemMeta(meta);
		}
		return namedItemStack;
	}
	
	
	/**
	 * Gets the enchants for an item
	 * @param config The config section to read
	 * @return The list of enchants
	 */
	private List<ProbabilisticEnchantment> getEnchantments(ConfigurationSection config)
	{
		List<ProbabilisticEnchantment> enchantments = new ArrayList<ProbabilisticEnchantment>();
		if(config != null)
		{
			Iterator<String> names = config.getKeys(false).iterator();
			while (names.hasNext())
			{
				String name=names.next();
				ConfigurationSection configEnchantment=config.getConfigurationSection(name);
				String type=configEnchantment.getString("type");
				if (type != null)
				{
					int level = configEnchantment.getInt("level",1);
					double probability = configEnchantment.getDouble("probability",1.0);
					ProbabilisticEnchantment enchantment = new ProbabilisticEnchantment(name,type,level,probability);
					enchantments.add(enchantment);
				}
			}
		}
		return enchantments;
	}
	
	
	/**
	 * Gets the list of potion effects for an item
	 * @param config The config section to read
	 * @return The list of potion effects
	 */
	private List<PotionEffect> getPotionEffects(ConfigurationSection config) {
		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
		if(config != null)
		{
			Iterator<String> names = config.getKeys(false).iterator();
			while (names.hasNext())
			{
				String name = names.next();
				ConfigurationSection configEffect = config.getConfigurationSection(name);
				String type = configEffect.getString("type");
				if (type != null)
				{
					PotionEffectType effect = PotionEffectType.getByName(type);
					if (effect != null) {
						int duration = configEffect.getInt("duration",200);
						int amplifier = configEffect.getInt("amplifier",0);
						potionEffects.add(new PotionEffect(effect, duration, amplifier));
					}
				}
			}
		}
		return potionEffects;
	}
}
