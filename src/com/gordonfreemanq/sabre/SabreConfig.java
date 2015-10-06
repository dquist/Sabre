package com.gordonfreemanq.sabre;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import com.gordonfreemanq.sabre.blocks.ReinforcementMaterial;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public class SabreConfig {
	
	public static int CONFIG_VERSION = 1;
	
	private final FileConfiguration fc;
	
	/**
	 * Creates a new SabreConfig instance
	 */
	public SabreConfig(FileConfiguration fc) {
		this.fc = fc;
		this.DbAddress = "";
		this.DbPort = 0;
		this.DbName = "";
		this.DbUser = "";
		this.DbPassword = "";
		this.ChatRadius = 1000;
		this.lockableItems = new ArrayList<Material>();
		this.nonReinforceable = new ArrayList<Material>();
		this.reinforcementMaterials = new ArrayList<ReinforcementMaterial>();
		this.disabledRecipes = new ArrayList<SabreItemStack>();
		this.disabledItemDrops = new HashSet<Material>();
		this.prisonWorld = "world_the_end";
		this.freeWorld = "world";
	}
	
	/**
	 * The database address
	 */
	public String DbAddress;
	
	
	/**
	 * The Database port
	 */
	public int DbPort;
	
	
	/**
	 * The database name
	 */
	public String DbName;
	
	
	/**
	 * The database user name
	 */
	public String DbUser;
	
	
	/**
	 * The database password
	 */
	public String DbPassword;
	
	
	/**
	 * The global chat radius
	 */
	public int ChatRadius;
	

	/**
	 * Materials that can be locked
	 */
	private List<Material> lockableItems;
	
	
	/**
	 * Materials that are not reinforceable
	 */
	private List<Material> nonReinforceable;
	
	
	private List<ReinforcementMaterial> reinforcementMaterials;
	private List<SabreItemStack> disabledRecipes;
	private Set<Material> disabledItemDrops;
	
	
	
	public int snitchEntryOverlap;
	
	
	public int snitchOverlapSearch;
	
	
	public String freeWorld;
	
	public String prisonWorld;
	
	
	/**
	 * Factory method for creating a new config class
	 * @param fc The file configuration instance
	 * @return The new configuration class instance
	 */
	public static SabreConfig load(FileConfiguration fc) {
		SabreConfig config = new SabreConfig(fc);
		config.read();
		config.save();
		return config;
	}
	
	
	/**
	 * Reads the config values into memory
	 */
	@SuppressWarnings("unused")
	public void read() {		
		int version = fc.getInt("general.version");
		
		this.DbAddress = fc.getString("database.address", this.DbAddress);
		this.DbPort = fc.getInt("database.port", this.DbPort);
		this.DbName = fc.getString("database.name", this.DbUser);
		this.DbUser = fc.getString("database.user", this.DbUser);
		this.DbPassword = fc.getString("database.pass", this.DbPassword);
		this.ChatRadius = fc.getInt("chat.global_radius", this.ChatRadius);
		this.snitchEntryOverlap = fc.getInt("snitch.overlap_time", this.snitchEntryOverlap);
		this.snitchOverlapSearch = fc.getInt("snitch.overlap_depth", this.snitchOverlapSearch);
		this.prisonWorld = fc.getString("prison_pearl.prison_world", this.prisonWorld);
		this.freeWorld = fc.getString("prison_pearl.free_world", this.freeWorld);
		
		this.reinforcementMaterials.clear();
		this.lockableItems.clear();
		this.nonReinforceable.clear();
		
		if (fc.contains("build.reinforcements")) {
			Set<String> rKeys = fc.getConfigurationSection("build.reinforcements").getKeys(false);
			for (String s : rKeys) {
				Material m = Material.getMaterial(s);
				int strength = fc.getInt(String.format("build.reinforcements.%s.strength", s));
				this.reinforcementMaterials.add(new ReinforcementMaterial(m, strength));
			}
		} else {
			setDefaultReinforcements();
		}

		
		this.disabledRecipes.clear();
		if (fc.contains("disabled_recipes")) {
			Set<String> rKeys = fc.getConfigurationSection("disabled_recipes").getKeys(false);
			for (String s : rKeys) {
				String materialKey = String.format("disabled_recipes.%s.material", s);
				String duraKey = String.format("disabled_recipes.%s.durability", s);
				
				Material m = Material.getMaterial(fc.getString(materialKey));
				int durability = 0;
				if (fc.contains(duraKey)) {
					durability = fc.getInt(duraKey);
				}
				
				if (m != null) {
					SabreItemStack is = new SabreItemStack(m, m.name(), 1, durability);
					this.disabledRecipes.add(is);
				}
			}
		}
		
		
		this.disabledItemDrops.clear();
		if (fc.contains("disabled_item_drops")) {
			Set<String> rKeys = fc.getConfigurationSection("disabled_item_drops").getKeys(false);
			for (String s : rKeys) {
				String materialKey = String.format("disabled_item_drops.%s.material", s);
				String duraKey = String.format("disabled_item_drops.%s.durability", s);
				
				Material m = Material.getMaterial(fc.getString(materialKey));
				int durability = 0;
				if (fc.contains(duraKey)) {
					durability = fc.getInt(duraKey);
				}
				
				if (m != null) {
					this.disabledItemDrops.add(m);
				}
			}
		}
		
		if (fc.contains("build.lockable")) {
			for (Object item : fc.getList("build.lockable")) {
				lockableItems.add(Material.matchMaterial(item.toString()));
			}
		} else {
			setDefaultLockables();
		}
		
		if (fc.contains("build.nonreinforceable")) {
			for (Object item : fc.getList("build.nonreinforceable")) {
				nonReinforceable.add(Material.matchMaterial(item.toString()));
			}
		} else {
			setDefaultReinforceables();
		}
	}
	
	
	/**
	 * Saves the config value to disk
	 */
	public void save() {
		fc.set("general.config_version", CONFIG_VERSION);
		fc.set("database.address", this.DbAddress);
		fc.set("database.port", this.DbPort);
		fc.set("database.name", this.DbName);
		fc.set("database.user", this.DbUser);
		fc.set("database.pass", this.DbPassword);
		fc.set("chat.global_radius", this.ChatRadius);
		fc.set("snitch.overlap_time", this.snitchEntryOverlap);
		fc.set("snitch.overlap_depth", this.snitchOverlapSearch);
		fc.set("prison_pearl.free_world", this.freeWorld);
		fc.set("prison_pearl.prison_world", this.prisonWorld);
		
		ConfigurationSection sect = fc.createSection("build.reinforcements");
		for(ReinforcementMaterial r : this.reinforcementMaterials) {
			ConfigurationSection rSect = sect.createSection(r.material.toString());
			rSect.set("strength", r.strength);
		}
		
	}
	
	
	private void setDefaultLockables() {
		lockableItems.add(Material.CHEST);
		lockableItems.add(Material.TRAPPED_CHEST);
		lockableItems.add(Material.WOODEN_DOOR);
		lockableItems.add(Material.IRON_DOOR);
		lockableItems.add(Material.IRON_DOOR_BLOCK);
		lockableItems.add(Material.FURNACE);
		lockableItems.add(Material.DISPENSER);
		lockableItems.add(Material.FENCE_GATE);
		lockableItems.add(Material.BED);
		lockableItems.add(Material.BED_BLOCK);
		lockableItems.add(Material.BOOKSHELF);
		lockableItems.add(Material.BEACON);
		lockableItems.add(Material.ANVIL);
		lockableItems.add(Material.TRAP_DOOR);
		lockableItems.add(Material.ENCHANTMENT_TABLE);
		lockableItems.add(Material.WOOD_BUTTON);
		lockableItems.add(Material.STONE_BUTTON);
		lockableItems.add(Material.LEVER);
		lockableItems.add(Material.BREWING_STAND);
		lockableItems.add(Material.BREWING_STAND_ITEM);
		lockableItems.add(Material.HOPPER);
		lockableItems.add(Material.REDSTONE_COMPARATOR);
		lockableItems.add(Material.REDSTONE_COMPARATOR_OFF);
		lockableItems.add(Material.REDSTONE_COMPARATOR_ON);
		lockableItems.add(Material.DIODE);
		lockableItems.add(Material.DIODE_BLOCK_OFF);
		lockableItems.add(Material.DIODE_BLOCK_ON);
		lockableItems.add(Material.JUKEBOX);
	}
	
	
	private void setDefaultReinforceables() {
		nonReinforceable.add(Material.AIR);
		nonReinforceable.add(Material.WATER);
		nonReinforceable.add(Material.STATIONARY_WATER);
		nonReinforceable.add(Material.LAVA);
		nonReinforceable.add(Material.STATIONARY_LAVA);
		nonReinforceable.add(Material.SAPLING);
		nonReinforceable.add(Material.DEAD_BUSH);
		nonReinforceable.add(Material.PISTON_EXTENSION);
		nonReinforceable.add(Material.PISTON_MOVING_PIECE);
		nonReinforceable.add(Material.LONG_GRASS);
		nonReinforceable.add(Material.RED_ROSE);
		nonReinforceable.add(Material.YELLOW_FLOWER);
		nonReinforceable.add(Material.BROWN_MUSHROOM);
		nonReinforceable.add(Material.RED_MUSHROOM);
		nonReinforceable.add(Material.TNT);
		nonReinforceable.add(Material.FIRE);
		nonReinforceable.add(Material.CROPS);
		nonReinforceable.add(Material.SNOW);
		nonReinforceable.add(Material.ICE);
		nonReinforceable.add(Material.CACTUS);
		nonReinforceable.add(Material.SUGAR_CANE_BLOCK);
		nonReinforceable.add(Material.PORTAL);
		nonReinforceable.add(Material.CAKE_BLOCK);
		nonReinforceable.add(Material.PUMPKIN_STEM);
		nonReinforceable.add(Material.MELON_STEM);
		nonReinforceable.add(Material.VINE);
		nonReinforceable.add(Material.NETHER_WARTS);
		nonReinforceable.add(Material.ENDER_PORTAL);
	}
	
	
	private void setDefaultReinforcements() {
		reinforcementMaterials.add(new ReinforcementMaterial(Material.STONE, 150));
		reinforcementMaterials.add(new ReinforcementMaterial(Material.IRON_INGOT, 1500));
		reinforcementMaterials.add(new ReinforcementMaterial(Material.DIAMOND, 4500));
		reinforcementMaterials.add(new ReinforcementMaterial(Material.DIAMOND_BLOCK, 10000));
	}
	
	
	/**
	 * Gets a reinforcement material by material if it exists
	 * @param m The material to check
	 * @return The reinforcement material instance
	 */
	public ReinforcementMaterial getReinforcementMaterial(Material m) {		
		for (ReinforcementMaterial r : reinforcementMaterials) {
			if (r.material.equals(m)) {
				return r;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the disabled crafting recipes
	 * @return The disabled recipes
	 */
	public List<SabreItemStack> getDisabledRecipes() {
		return this.disabledRecipes;
	}
	
	/**
	 * Gets the disabled entity drops
	 * @return The disabled entity drops
	 */
	public Set<Material> getDisabledEntityDrops() {
		return this.disabledItemDrops;
	}
	
	
	
	
	/**
	 * Checks whether a block type is non reinforceable
	 * @param m The material to check
	 * @return true if it is non reinforceable
	 */
	public boolean blockIsNonreinforceable(Material m) {
		for (Material mat : nonReinforceable) {
			if (mat.equals(m)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Checks whether a block type is lockable
	 * @param m The material to check
	 * @return true if it is lockable
	 */
	public boolean blockIsLockable(Material m) {
		for (Material mat : lockableItems) {
			if (mat.equals(m)) {
				return true;
			}
		}
		
		return false;
	}
}
