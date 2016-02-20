package com.gordonfreemanq.sabre.factory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.DirectionalContainer;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.customitems.SpecialBlock;
import com.gordonfreemanq.sabre.factory.recipe.IRecipe;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.mongodb.BasicDBObject;

public class BaseFactory extends SpecialBlock {
	
	// Saved
	protected boolean configureMode;
	protected Location inputLocation;
	protected Location outputLocation;
	protected Location fuelLocation;
	protected int recipeIndex;
	
	// Not saved
	protected final FactoryProperties properties;
	protected IRecipe recipe;
	protected boolean running;
	protected int fuelPerBurn;
	protected int fuelCounter;
	protected int energyTimer;
	protected List<FactoryFuel> fuels;
	protected FactoryFuel curFuel;
	protected SabrePlayer runner;
	protected boolean upgradeMode;
	

	/**
	 * Creates a new BlockRecord instance
	 * @param location The block location
	 */
	public BaseFactory(Location location, String typeName) {
		super(location, typeName);
		
		// Get the recipes for this factory
		this.properties = FactoryConfig.getInstance().getFactoryProperties(this.typeName);
		
		List<IRecipe> recipes = properties.getRecipes();
		
		// Set the current recipe
		if (recipeIndex < recipes.size()) {
			recipe = recipes.get(recipeIndex);
		} else if (recipes.size() > 0) {
			recipe = recipes.get(0);
		}
		
		fuelPerBurn = 1;

		fuels = new ArrayList<FactoryFuel>();
		
		fuels.add(new FactoryFuel(new SabreItemStack(Material.COAL, "Coal", 1, 0), 1));
		fuels.add(new FactoryFuel(new SabreItemStack(Material.COAL, "Charcoal", 1, 1), 1));
		fuels.add(new FactoryFuel(new SabreItemStack(Material.BLAZE_ROD, "Blaze Rod", 1), 4));
		FactoryFuel lavaFuel = new FactoryFuel(new SabreItemStack(Material.LAVA_BUCKET, "Lava Bucket", 1), 16);
		lavaFuel.addReturnItem(new SabreItemStack(Material.BUCKET, "Bucket", 1));
		fuels.add(lavaFuel);
		fuels.add(new FactoryFuel(CustomItems.getInstance().getByName("Plasma"), 64));
		
		setFurnaceState(false);
	}
	
	
	/**
	 * Gets the configure mode
	 * @return The configure mode
	 */
	public boolean getConfigureMode() {
		return this.configureMode;
	}
	
	
	/**
	 * Sets the configure mode
	 * @param configureMode The new configure mode
	 */
	public void setConfigureMode(boolean configureMode) {
		if (running) {
			return;
		}
		
		this.configureMode = configureMode;
	}
	
	/**
	 * Gets the upgrade mode
	 * @return The upgrade mode
	 */
	public boolean getUpgradeMode() {
		return this.upgradeMode;
	}
	
	
	/**
	 * Sets the upgrade mode
	 * @param upgradeMode The new upgrade mode
	 */
	public void setUpgradeMode(boolean upgradeMode) {
		if (running) {
			return;
		}
		this.upgradeMode = upgradeMode;
	}
	
	
	/**
	 * Gets the running status
	 * @return The running status
	 */
	public boolean getRunning() {
		return this.running;
	}
	
	
	 /**
     * Gets the percent complete
     * @return The percent complete
     */
    public Long getPercentComplete() {
    	Double dbl = new Double((double)fuelCounter / recipe.getFuelCost());
    	return Math.min(Math.round(dbl * 100.0), 100);
    }
	
	
	/**
	 * Gets the ingredient location
	 * @return The ingredient location
	 */
	public Location getInputLocation() {
		return this.inputLocation;
	}
	
	
	/**
	 * Sets the ingredient location
	 * @param ingredientLocation The new ingredient location
	 */
	public void setInputLocation(Location inputLocation) {
		this.inputLocation = inputLocation;
	}
	
	
	/**
	 * Gets the output location
	 * @return The output location
	 */
	public Location getOutputLocation() {
		return this.outputLocation;
	}
	
	
	/**
	 * Sets the output location
	 * @param fuel The new output location
	 */
	public void setOutputLocation(Location outputLocation) {
		this.outputLocation = outputLocation;
	}
	
	
	/**
	 * Gets the fuel location
	 * @return The fuel location
	 */
	public Location getFuelLocation() {
		return this.fuelLocation;
	}
	
	
	/**
	 * Sets the fuel location
	 * @param fuel The new fuel location
	 */
	public void setFuelLocation(Location fuelLocation) {
		this.fuelLocation = fuelLocation;
	}
	
	
	/**
	 * Gets the recipe number
	 * @return The recipe number
	 */
	public int getRecipeNumber() {
		return this.recipeIndex;
	}
	
	
	/**
	 * Sets the recipe number
	 * @param recipe The new recipe number
	 */
	public void setRecipeNumber(int recipe) {
		this.recipeIndex = recipe;
	}
	
	
	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	public BasicDBObject getSettings() {
		BasicDBObject doc = new BasicDBObject()
		.append("recipe", this.recipeIndex);
		
		if (inputLocation != null) {
			doc = doc.append("input", SabreUtil.serializeLocation(this.inputLocation));
		}
		
		if (outputLocation != null) {
			doc = doc.append("output", SabreUtil.serializeLocation(this.outputLocation));
		}
		
		if (fuelLocation != null) {
			doc = doc.append("fuel", SabreUtil.serializeLocation(this.fuelLocation));
		}
		
		doc = doc.append("running", this.running);
		doc = doc.append("factory", true);
		
		return doc;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		
		if (o.containsField("input")) {
			this.inputLocation = SabreUtil.deserializeLocation(o.get("input"));
		}
		
		if (o.containsField("output")) {
			this.outputLocation = SabreUtil.deserializeLocation(o.get("output"));
		}
		
		if (o.containsField("fuel")) {
			this.fuelLocation = SabreUtil.deserializeLocation(o.get("fuel"));
		}
		this.recipeIndex = o.getInt("recipe");

		List<IRecipe> recipes = properties.getRecipes();
		if (recipes.size() > 0) {
			if (recipeIndex >= recipes.size()) {
				recipeIndex = 0;
			}
			recipe = recipes.get(recipeIndex);
		}
		
		this.running = o.getBoolean("running", false);
		if (this.running) {
			this.powerOn();
		}
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
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !p.getPlayer().isSneaking()) {
			e.setCancelled(true);
		}
	}
	

	/**
	 * Handles hitting the block with a stick
	 * @param p The player interacting
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		// Create a controller if it doesn't exist
		if (this.canPlayerModify(sp)) {
			createController(sp);
		}
		
		Action a = e.getAction();
		if (a == Action.RIGHT_CLICK_BLOCK) {
			if (running) {
				sp.msg("<a>Status: <n>%d%%", this.getPercentComplete());
			} else {
				cycleRecipe(sp);
			}
		} else {
			cyclePower(sp);
		}
	}
	
	
	/**
	 * Handles the block breaking, low priority event
	 * @param e The event args
	 */
	public void onBlockBreaking(SabrePlayer p, BlockBreakEvent e) {
		if (running) {
			powerOff();
		}
	}
	
	
	/**
	 * Creates the factory controller
	 * @param sp The player
	 */
	protected void createController(SabrePlayer sp) {
		ItemStack is = (new FactoryController(this)).toItemStack();
		sp.getPlayer().getInventory().setItemInHand(is);
	}
	
	
	
	/**
	 * Cycles the recipe
	 * @param sp The player
	 */
	protected void cycleRecipe(SabrePlayer runner) {
		
		this.runner = runner;
		
		// Can't switch recipes while running
		if (this.running) {
			msg("<b>You can't change recipes while the factory is running.");
			return;
		}
		
		// force upgrade mode for factories with no normal recipes
		if (properties.getRecipes().size() == 0 && properties.getUpgrades().size() > 0) {
			upgradeMode = true;
		}
		
		List<IRecipe> recipes;
		if (upgradeMode) {
			recipes = properties.getUpgrades();
		} else {
			recipes = properties.getRecipes();
		}
		
		if (recipes.size() == 0) {
			msg("<i>This factory has no recipes.", recipeIndex);
			return;
		}
		recipeIndex++;
		
		if (recipeIndex >= recipes.size()) {
			recipeIndex = 0;
		}
		
		recipe = recipes.get(recipeIndex);

		String nextRecipeName;
		if (recipeIndex != recipes.size() - 1) {
			nextRecipeName = recipes.get(recipeIndex + 1).getName();
		} else {
			nextRecipeName = recipes.get(0).getName();
		}
		
		msg(SabrePlugin.getPlugin().txt.titleize("<silver>" + this.name));
		//msg("<i>-----------------------------------------------------");
		msg("<g>Switched recipe to: <c>%s", recipe.getName());
		msg("<g>Next recipe is: <i><it>%s", nextRecipeName);
		
		BlockManager.getInstance().updateSettings(this);
	}
	
	
	/**
	 * Cycles power on the factory
	 */
	public void cyclePower(SabrePlayer runner) {
		this.runner = runner;
		
		if (running) {
			powerOff();
			msg(Lang.factoryDeactivated);
		} else {
			if (recipe == null) {
				msg(Lang.factoryNoRecipe);
				return;
			}
			
			if (!checkChests()) {
				msg(Lang.factoryMissingChests);
				return;
			}
			
			if (!checkFuel()) {
				msg(Lang.factoryMissingFuel);
				return;
			}
			
			recipe.onRecipeStart(this);
			
			if (!checkMaterials()) {
				ItemList<SabreItemStack> needAll = new ItemList<SabreItemStack>();
				needAll.addAll(recipe.getInputs().getDifference(getInputInventory()));
				msg(Lang.factoryNeedFollowing, needAll.toString());
				return;
			}
			
			
			// Good to go
			powerOn();
			msg(Lang.factoryActivated, recipe.getName());
		}
	}
	
	
	protected void onPowerOn() {
		// Do nothing
	}
	
	
	/**
	 * Turns the factory on
	 * @param sp The player
	 */
	protected void powerOn() {

		running = true;
		onPowerOn();
		fuelCounter = 0;
		energyTimer = 0;
		FactoryWorker.getInstance().addRunning(this);
		setActivationLever(true);
		setFurnaceState(true);
	}
	
	
	/**
	 * Turns the factory off
	 * @param sp The player
	 */
	protected void powerOff() {
		running = false;
		fuelCounter = 0;
		energyTimer = 0;
		setActivationLever(false);
		FactoryWorker.getInstance().removeRunning(this);
		setFurnaceState(false);
		
		this.runner = null;
	}
	
	
	/**
	 * Sets the state of the furnace block
	 * @param state The state of the furnace
	 */
	protected void setFurnaceState(boolean state) {
		Block f = location.getBlock();
		
		if (state) {
			if (f.getType() != Material.FURNACE) {
				return;
			}
			
			Furnace furnace = (Furnace) f.getState();
			ItemStack[] oldContents = furnace.getInventory().getContents();
			BlockFace facing = ((DirectionalContainer)furnace.getData()).getFacing();
			furnace.getInventory().clear();
			f.setType(Material.BURNING_FURNACE);
			furnace = (Furnace) f.getState();
			MaterialData data = furnace.getData();
			((DirectionalContainer)data).setFacingDirection(facing);
			furnace.setData(data);
			furnace.update();
			furnace.setBurnTime(Short.MAX_VALUE);
			furnace.getInventory().setContents(oldContents);
		} else {
			if (f.getType() != Material.BURNING_FURNACE) {
				return;
			}
			
			Furnace furnace = (Furnace) f.getState();
			ItemStack[] oldContents = furnace.getInventory().getContents();
			BlockFace facing = ((DirectionalContainer)furnace.getData()).getFacing();
			furnace.getInventory().clear();
			f.setType(Material.FURNACE);
			furnace = (Furnace) f.getState();
			MaterialData data = furnace.getData();
			((DirectionalContainer)data).setFacingDirection(facing);
			furnace.setData(data);
			furnace.update();
			furnace.getInventory().setContents(oldContents);
		}
	}
	
	
	/**
	 * Checks the status of the factory chests
	 * @return true if all the chests exist
	 */
	private boolean checkChests() {
		if (inputLocation == null || outputLocation == null || fuelLocation == null) {
			return false;
		}
		
		if (!isLocationChest(inputLocation) || !isLocationChest(outputLocation) || !isLocationChest(fuelLocation)) {
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * Checks if there is enough fuel available for at least once energy cycle
	 * @return true if there is enough fuel, otherwise false
	 */
	private boolean checkFuel()
	{
		Inventory inv = getFuelInventory();
		if (inv != null) {
			for (FactoryFuel f : fuels) {
				if (f.getItems().allIn(inv)) {
					curFuel = f;
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	/**
	 * Checks if the input chest has the required materials
	 * @return true if the materials exist
	 */
	private boolean checkMaterials() {
		
		return recipe.getInputs().allIn(getInputInventory());
	}
	
	
	/**
	 * Gets the input chest inventory
	 * @return The input inventory
	 */
	public Inventory getInputInventory() {
		Chest chest = (Chest)inputLocation.getBlock().getState();
		return chest.getInventory();
	}
	
	
	/**
	 * Gets the output chest inventory
	 * @return The output inventory
	 */
	public Inventory getOutputInventory() {
		Chest chest = (Chest)outputLocation.getBlock().getState();
		return chest.getInventory();
	}
	
	
	/**
	 * Gets the fuel chest inventory
	 * @return The fuel inventory
	 */
	public Inventory getFuelInventory() {
		Chest chest = (Chest)fuelLocation.getBlock().getState();
		return chest.getInventory();
	}
	
	
	/**
	 * Checks if a location is a chest
	 * @param l The location
	 * @return true if the location is a chest
	 */
	public static boolean isLocationChest(Location l) {
		return l.getBlock().getType().equals(Material.CHEST) || l.getBlock().getType().equals(Material.TRAPPED_CHEST);
	}
	
	
	/**
	 * Update method for running the factory
	 */
	public void update() 
	{
		if (!running) {
			return;
		}
		
		// Power off non-loaded factories
		if (!this.location.getChunk().isLoaded()) {
			powerOff();
			return;
		}
		
		if (!checkMaterials()) {
			powerOff();
			return;
		}
		
		if (fuelCounter < recipe.getFuelCost()) {

			// Check if we should grab more fuel
			if (energyTimer <= 0) {
				
				if (!checkFuel()) {
					if (runner != null) {
						runner.msg(Lang.factoryRecipeOutOfFuel, recipe.getName());
						runner = null;
					}
					powerOff();
					return;
				}
				
				curFuel.getItems().removeFrom(getFuelInventory());
				curFuel.getReturnItems().putIn(getFuelInventory());
				energyTimer = curFuel.getEnergy();
			}

			int speed = recipe.getProductionSpeed();
			if (speed > energyTimer) {
				speed = energyTimer;
			}
			
			fuelCounter += speed;
			energyTimer -= speed;
			
			return;
		}
		else if (fuelCounter >= recipe.getFuelCost()) {
			
			// Consume inputs
			if (!recipe.getInputs().removeFrom(getInputInventory())) {
				powerOff();
				
			}
			
			try {
				// Product outputs
				if (upgradeMode) {
					performUpgrade();
				} else {
					recipe.getOutputs().putIn(getOutputInventory());
					recipe.onRecipeComplete(this);
				}
				
				if (runner != null) {
					runner.msg(Lang.factoryComplete, recipe.getName());
					runner = null;
				}
			} catch (Exception ex) {
				if (runner != null) {
					runner.msg(Lang.factoryComplete, recipe.getName());
					runner = null;
				}
			} finally {
				powerOff();
			}
		}
	}
	
	
	/**
	 * Performs the factory upgrade
	 */
	private void performUpgrade() {

		BlockManager bm = BlockManager.getInstance();
		
		// This is the output item
		SabreItemStack item = recipe.getOutputs().get(0);
		
		SabreBlock upgradeBlock = BlockManager.createBlockFromItem(item, this.getLocation());
		
		if(upgradeBlock == null) {
			runner.msg(Lang.factoryError);
			throw new RuntimeException();
		}
		
		upgradeBlock.setReinforcement(this.getReinforcement());
		
		if (upgradeBlock instanceof BaseFactory) {
			((BaseFactory)upgradeBlock).copyConfiguration(this);
		}
		
		
		// Out with the old, in with the new
		bm.removeBlock(this);
		bm.addBlock(upgradeBlock);
	}
	
	
	/**
	 * Updates the factory activation lever
	 * @param state The new state
	 */
	private void setActivationLever(boolean state) {
		
		Block block = location.getBlock();
    	Block lever = findAttachedLever(block);
    	
		if (lever != null) {
			setLever(lever, state);
		}
	}
	

	public static final BlockFace[] REDSTONE_FACES = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
	
	/**
	 * Finds the activation lever
	 * @return The activation lever
	 */
	public Block findActivationLever() {
    	Block block = location.getBlock();
    	return findAttachedLever(block);
	}
	
	
	/**
	 * Finds the attached lever
	 * @param block The block to find
	 * @return The lever block, or null if it doesn't exist
	 */
    public Block findAttachedLever(Block block) {
		// Check sides for attached lever - required for automation
		Block lever = null;
		for (BlockFace face : REDSTONE_FACES) {
			lever = block.getRelative(face);
			if (lever.getType() == Material.LEVER) {
			    BlockFace attached = getAttachedFace(lever);
			    if (attached != null && attached == face) {
					return lever;
			    }
			}
		}
		
		return null;
    }
    
    
    /**
     * Gets an attached block face
     * @param lever The lever block
     * @return The attached face
     */
    private static BlockFace getAttachedFace(Block lever) {
    	BlockState state = lever.getState();
    	MaterialData md = state.getData();
    	if (md instanceof Attachable) {
    		BlockFace face = ((Attachable) md).getAttachedFace();
    		return face.getOppositeFace();
    	} else {
    		return null;
    	}
    }
    
	/**
	* Sets the toggled state of a single lever<br>
	* <b>No Lever type check is performed</b>
	*
	* @param b block
	* @param down state to set to
	*/
	@SuppressWarnings("deprecation")
	private static void setLever(Block b, boolean powered) {
		if (b.getType() != Material.LEVER) {
			return;
		}
		
		Lever lever = (Lever) b.getState().getData();
		lever.setPowered(powered);
		b.getState().update();
		b.setData(lever.getData(), true);
		
		Block supportBlock = b.getRelative(lever.getAttachedFace());
		BlockState initialSupportState = supportBlock.getState();
		BlockState supportState = supportBlock.getState();
		supportState.setType(Material.AIR);
		supportState.update(true, false);
		initialSupportState.update(true);
	}
	
	
	/**
	 * Copies the configuration from another factory
	 * @param other
	 */
	public void copyConfiguration(BaseFactory other) {
		this.inputLocation = other.inputLocation;
		this.outputLocation = other.outputLocation;
		this.fuelLocation = other.fuelLocation;
	}
	

	/**
	 * Messages the runner
	 * @param str The message
	 * @param args The message args
	 */
	public void msg(String str, Object... args)
	{
		if (runner == null) {
			return;
		}
		
		runner.msg(str, args);
	}
    
    
    /**
     * Gets the current recipe
     * @return
     */
    public IRecipe getRecipe() {
    	return this.recipe;
    }
    
    
    /**
     * Whether the factory should run while unloaded
     * @return
     */
    public boolean runUnloaded() {
    	return false;
    }
}
