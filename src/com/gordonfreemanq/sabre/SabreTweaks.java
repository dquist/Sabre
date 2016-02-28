package com.gordonfreemanq.sabre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.StorageMinecart;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.CombatInterface;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

@SuppressWarnings("deprecation")
public class SabreTweaks implements Listener {

	private final SabreConfig config;
	private Random rand;

	private final CombatInterface combatTag;

	private Map<String, PearlTeleportInfo> pearlTeleportInfo = new TreeMap<String, PearlTeleportInfo>();
	private final static int PEARL_THROTTLE_WINDOW = 10000;  // 10 sec
	private final static int PEARL_NOTIFICATION_WINDOW = 1000;  // 1 sec
	
    private static final String CACTUS_HIT_TAG = "hitCactus";
    private static MetadataValue CACTUS_HIT;

	public SabreTweaks(SabreConfig config) {
		this.config = config;
		this.rand = new Random(1337);

		RegisterCustomRecipes();
		
		// Combat Tag API
		combatTag = SabrePlugin.getPlugin().getCombatTag();
		
	    CACTUS_HIT = new FixedMetadataValue(SabrePlugin.getPlugin(), true);
	}


	private void RegisterCustomRecipes() {

		// Recipe: 1 XP bottle creates 9 Emeralds 
		ShapelessRecipe expToEmeraldRecipe = new ShapelessRecipe(new ItemStack(Material.EMERALD, 1));
		expToEmeraldRecipe.addIngredient(9, Material.EXP_BOTTLE);
		Bukkit.addRecipe(expToEmeraldRecipe);

		// Recipe: 9 Emeralds create 1 XP bottle 
		ShapelessRecipe emeraldToExpRecipe = new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE, 9));
		emeraldToExpRecipe.addIngredient(Material.EMERALD);
		Bukkit.addRecipe(emeraldToExpRecipe);
	}

	
	/**
	 * CobbleStone and dirt act like sand/gravel
	 * 
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Block b = e.getBlock();
		Material m = b.getType();
		Material under = b.getRelative(BlockFace.DOWN).getType();
		
		if (under == Material.AIR || under == Material.STATIONARY_WATER ||under == Material.WATER || under == Material.LAVA|| under == Material.STATIONARY_LAVA) {
			if (m.equals(Material.COBBLESTONE) || m.equals(Material.DIRT)) {
				b.setType(Material.AIR);
				e.getBlock().getWorld().spawnFallingBlock(b.getLocation(), m, b.getData());
			}
		}
	}
	
	
	/**
	 * Disable bed bombs in nether and end
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerEnterBed(BlockPlaceEvent event) {
		Block b = event.getBlock();
		if (!(b.getType() == Material.BED || b.getType() == Material.BED_BLOCK)) {
			return;
		}
		
		Environment env = b.getLocation().getWorld().getEnvironment();
		if (env == Environment.NETHER || env == Environment.THE_END) {
			event.setCancelled(true);
		}
	}
	  

	/**
	 * Deal extra tool durability based on Y level
	 * 
	 * @param e
	 */
	/*
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onMiningEvent(BlockBreakEvent e) {
		Block b = e.getBlock();

		switch (b.getType()) {
		case STONE:
		case OBSIDIAN:
		case IRON_ORE:
		case DIAMOND_ORE:
		case REDSTONE_ORE:
		case LAPIS_ORE:
		case EMERALD_ORE:
			ItemStack is = e.getPlayer().getItemInHand();
			int dura = this.getDurability(b);
			if (isPickaxe(is.getType())) {
				is.setDurability((short) (is.getDurability() + dura));
			}
			break;

		default:
			break;
		}
	} */

	static double MAX_DURA = 50;

	public int getDurability(Block b) {
		String biomeName = b.getBiome().toString();
		double dura = 1;
		int groundLevel = 100;
		int y = b.getY();

		if (biomeName.equalsIgnoreCase("IceMountains")) {
			groundLevel = 250;
		}

		groundLevel -= groundLevel * 0.25;

		if (y < groundLevel) {
			dura += (groundLevel - y) * (MAX_DURA / groundLevel);
		}

		return (int) dura;

	}

	public boolean isPickaxe(Material m) {
		switch (m) {
		case WOOD_PICKAXE:
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLD_PICKAXE:
		case DIAMOND_PICKAXE:
			return true;

		default:
			return false;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFallBelowBedrock(PlayerMoveEvent e) {

		if (e.getPlayer().getWorld()
				.equals(PearlManager.getInstance().getPrisonWorld())) {
			return;
		}

		Location l = e.getPlayer().getLocation();
		if (l.getBlockY() < -2) {

			// Falling below bedrock
			SabreUtil.tryToTeleport(e.getPlayer(), l.add(0, 10, 0));
		}
	}

	
	/**
	 * Disable crafting for lore items and disabled recipes
	 * 
	 * @param e
	 *            The PrepareItemCraftEvent
	 */
	@EventHandler
	public void craftItem(PrepareItemCraftEvent e) {

		for (ItemStack is : e.getInventory().getContents()) {
			if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
				if(is.getItemMeta().getDisplayName().contains("Controller")) {
					continue; // who cares about sticks
				}
				
				e.getInventory().setResult(new ItemStack(Material.AIR));

				// Prevent crafting with lore items
				for(HumanEntity he : e.getViewers()) {
					if(he instanceof Player) {
						PlayerManager.getInstance().getPlayerById(he.getUniqueId()).msg(Lang.noCraftingLore);
					}
				}

				return;
			}
		}

		ItemStack result = e.getRecipe().getResult();

		for (SabreItemStack is : config.getDisabledRecipes()) {
			if (is.isSimilar(result)) {
				e.getInventory().setResult(new ItemStack(Material.AIR));

				for(HumanEntity he : e.getViewers()) {
					if(he instanceof Player) {
						PlayerManager.getInstance().getPlayerById(he.getUniqueId()).msg(Lang.recipeDisabled);
					}
				}
				return;
			}
		}
	}

	
	/**
	 * Disable villager trading
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Entity npc = e.getRightClicked();
		if (npc == null) {
			return;
		}
		if (npc.getType() == EntityType.VILLAGER) {
			e.setCancelled(true);
		}
	}
	

	/**
	 * Disable ender chest
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEnderChestUse(PlayerInteractEvent e) {
		Action action = e.getAction();
		Material material = e.getClickedBlock().getType();

		if (action == Action.RIGHT_CLICK_BLOCK
				&& material.equals(Material.ENDER_CHEST)) {
			e.setCancelled(true);
		}
	}

	
	/**
	 * Unlimited cauldron water
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onCauldronInteract(PlayerInteractEvent e) {

		// block water going down on cauldrons
		if (e.getClickedBlock().getType() == Material.CAULDRON
				&& e.getMaterial() == Material.GLASS_BOTTLE
				&& e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			if (block.getData() > 0) {
				block.setData((byte) (block.getData() + 1));
			}
		}
	}

	// ================================================
	// Portals

	@EventHandler(ignoreCancelled = true)
	public void onPortalCreate(PortalCreateEvent e) {
		e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityPortalCreate(EntityCreatePortalEvent e) {
		e.setCancelled(true);
	}

	// ================================================
	// EnderDragon

	@EventHandler(ignoreCancelled = true)
	public void onDragonSpawn(CreatureSpawnEvent e) {
		if (e.getEntityType() == EntityType.ENDER_DRAGON) {
			e.setCancelled(true);
		}
	}

	// ================================================
	// Endermen Griefing

	@EventHandler(ignoreCancelled = true)
	public void onEndermanGrief(EntityChangeBlockEvent e) {
		if (e.getEntity() instanceof Enderman) {
			e.setCancelled(true);
		}
	}

	// ================================================
	// Wither Insta-breaking and Explosions

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity npc = event.getEntity();
		if (npc == null) {
			return;
		}
		EntityType npc_type = npc.getType();
		if (npc_type.equals(EntityType.WITHER)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity npc = event.getEntity();
		if (npc == null) {
			return;
		}
		EntityType npc_type = npc.getType();
		if ((npc_type.equals(EntityType.WITHER) || npc_type
				.equals(EntityType.WITHER_SKULL))) {
			event.blockList().clear();
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onWitherSpawn(CreatureSpawnEvent event) {
		if (!event.getEntityType().equals(EntityType.WITHER)) {
			return;
		}
		event.setCancelled(true);
	}

	// ================================================
	// Prevent specified items from dropping off mobs

	public void removeItemDrops(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			return;
		}
		Set<Material> remove_ids = config.getDisabledEntityDrops();
		List<ItemStack> drops = event.getDrops();
		ItemStack item;
		int i = drops.size() - 1;
		while (i >= 0) {
			item = drops.get(i);
			if (remove_ids.contains(item.getType())) {
				drops.remove(i);
			}
			--i;
		}
	}

	// ================================================
	// Fix a few issues with pearls, specifically pearls in unloaded chunks and
	// slime blocks.
	@EventHandler()
	public void onChunkUnloadEvent(ChunkUnloadEvent event) {
		Entity[] entities = event.getChunk().getEntities();
		for (Entity ent : entities)
			if (ent.getType() == EntityType.ENDER_PEARL)
				ent.remove();
	}

	public void registerTimerForPearlCheck() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(
				SabrePlugin.getPlugin(), new Runnable() {

					@Override
					public void run() {
						for (Entity ent : pearlTime.keySet()) {
							if (pearlTime.get(ent).booleanValue()) {
								ent.remove();
							} else
								pearlTime.put(ent, true);
						}
					}

				}, 100, 200);
	}

	private Map<Entity, Boolean> pearlTime = new HashMap<Entity, Boolean>();

	public void onPearlLastingTooLong(EntityInteractEvent event) {
		if (event.getEntityType() != EntityType.ENDER_PEARL)
			return;
		Entity ent = event.getEntity();
		pearlTime.put(ent, false);
	}

	// ================================================
	// Generic mob drop rate adjustment
	public void adjustMobItemDrops(EntityDeathEvent e) {
		Entity mob = e.getEntity();
		if (mob instanceof Player) {
			return;
		}

		e.setDroppedExp(0);

		// if a dropped item was in the mob's inventory, drop only one,
		// otherwise drop the amount * the multiplier
		LivingEntity liveMob = (LivingEntity) mob;
		EntityEquipment mobEquipment = liveMob.getEquipment();
		ItemStack[] eeItem = mobEquipment.getArmorContents();
		for (ItemStack item : e.getDrops()) {
			boolean armor = false;
			boolean hand = false;
			for (ItemStack i : eeItem) {
				if (i.isSimilar(item)) {
					armor = true;
					item.setAmount(1);
				}
			}
			if (item.isSimilar(mobEquipment.getItemInHand())) {
				hand = true;
				item.setAmount(1);
			}
			if (!hand && !armor) {
				int amount = item.getAmount();
				item.setAmount(amount);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDeathEvent(EntityDeathEvent event) {
		removeItemDrops(event);
		// adjustWitherSkulls(event);
		adjustMobItemDrops(event);
	}

	// ================================================
	// Enchanted Golden Apple

	public boolean isEnchantedGoldenApple(ItemStack item) {
		// Golden Apples are GOLDEN_APPLE with 0 durability
		// Enchanted Golden Apples are GOLDEN_APPLE with 1 durability
		if (item == null) {
			return false;
		}
		if (item.getDurability() != 1) {
			return false;
		}
		Material material = item.getType();
		return material.equals(Material.GOLDEN_APPLE);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerEatGoldenApple(PlayerInteractEvent event) {
		// The event when eating is cancelled before even LOWEST fires when the
		// player clicks on AIR.
		ItemStack item = event.getItem();

		if (isEnchantedGoldenApple(item)) {
			item.setDurability((short) 0);
		}
	}

	// This needs to be removed when updated to citadel 3.0
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void entityPortalEvent(EntityPortalEvent e) {
		e.setCancelled(true);
	}

	// =================================================
	// Enchanted Book

	public boolean isNormalBook(ItemStack item) {
		if (item == null) {
			return false;
		}
		Material material = item.getType();
		return material.equals(Material.BOOK);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPrepareItemEnchantEvent(PrepareItemEnchantEvent event) {
		ItemStack item = event.getItem();
		if (isNormalBook(item)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEnchantItemEvent(EnchantItemEvent event) {
		ItemStack item = event.getItem();
		if (isNormalBook(item)) {
			event.setCancelled(true);
			// Player player = event.getEnchanter();
			// warning("Prevented book enchant. This should not trigger. Watch player "
			// + player.getName());
		}
	}

	// ================================================
	// Stop Cobble generation from lava+water

	private static final BlockFace[] faces_ = new BlockFace[] {
		BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,
		BlockFace.UP, BlockFace.DOWN };

	private BlockFace WaterAdjacentLava(Block lava_block) {
		for (BlockFace face : faces_) {
			Block block = lava_block.getRelative(face);
			Material material = block.getType();
			if (material.equals(Material.WATER)
					|| material.equals(Material.STATIONARY_WATER)) {
				return face;
			}
		}
		return BlockFace.SELF;
	}

	public void ConvertLava(final Block block) {
		int data = (int) block.getData();
		if (data == 0) {
			return;
		}
		Material material = block.getType();
		if (!material.equals(Material.LAVA)
				&& !material.equals(Material.STATIONARY_LAVA)) {
			return;
		}
		if (isLavaSourceNear(block, 3)) {
			return;
		}
		BlockFace face = WaterAdjacentLava(block);
		if (face == BlockFace.SELF) {
			return;
		}
		Bukkit.getScheduler().runTask(SabrePlugin.getPlugin(), new Runnable() {
			@Override
			public void run() {
				block.setType(Material.AIR);
			}
		});
	}

	public boolean isLavaSourceNear(Block block, int ttl) {
		int data = (int) block.getData();
		if (data == 0) {
			Material material = block.getType();
			if (material.equals(Material.LAVA)
					|| material.equals(Material.STATIONARY_LAVA)) {
				return true;
			}
		}
		if (ttl <= 0) {
			return false;
		}
		for (BlockFace face : faces_) {
			Block child = block.getRelative(face);
			if (isLavaSourceNear(child, ttl - 1)) {
				return true;
			}
		}
		return false;
	}

	public void LavaAreaCheck(Block block, int ttl) {
		ConvertLava(block);
		if (ttl <= 0) {
			return;
		}
		for (BlockFace face : faces_) {
			Block child = block.getRelative(face);
			LavaAreaCheck(child, ttl - 1);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		Material material = block.getType();
		if (!material.equals(Material.LAVA)
				&& !material.equals(Material.STATIONARY_LAVA)) {
			return;
		}
		LavaAreaCheck(block, 2);
	}

	// ================================================
	// Fix dupe bug with chests and other containers

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void blockExplodeEvent(EntityExplodeEvent event) {
		List<HumanEntity> humans = new ArrayList<HumanEntity>();
		for (Block block : event.blockList()) {
			if (block.getState() instanceof InventoryHolder) {
				InventoryHolder holder = (InventoryHolder) block.getState();
				for (HumanEntity ent : holder.getInventory().getViewers()) {
					humans.add(ent);
				}
			}
		}
		for (HumanEntity human : humans) {
			human.closeInventory();
		}
	}

	// ==================================================
	// Prevent entity dup bug
	// From https://github.com/intangir/EventBlocker

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPistonPushRail(BlockPistonExtendEvent e) {
		for (Block b : e.getBlocks()) {
			Material t = b.getType();
			if (t == Material.RAILS || t == Material.POWERED_RAIL
					|| t == Material.DETECTOR_RAIL) {
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRailPlace(BlockPlaceEvent e) {
		Block b = e.getBlock();
		Material t = b.getType();
		if (t == Material.RAILS || t == Material.POWERED_RAIL
				|| t == Material.DETECTOR_RAIL) {
			for (BlockFace face : faces_) {
				t = b.getRelative(face).getType();
				if (t == Material.PISTON_STICKY_BASE
						|| t == Material.PISTON_EXTENSION
						|| t == Material.PISTON_MOVING_PIECE
						|| t == Material.PISTON_BASE) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	
	/**
	 * Tags players on server join
	 * @param e The event
	 */
	@EventHandler
	public void tagOnJoin(PlayerJoinEvent e) {
		// Delay two ticks to tag after secure login has been denied.
		// This opens a 1 tick window for a cheater to login and grab
		// server info, which should be detectable and bannable.
		final Player loginPlayer = e.getPlayer();
		Bukkit.getScheduler().runTaskLater(SabrePlugin.getPlugin(),
				new Runnable() {
			@Override
			public void run() {
				if (loginPlayer == null || !loginPlayer.isOnline()) {
					return;
					
				}
				SabrePlugin.getPlugin().getCombatTag().tagPlayer(loginPlayer);
				loginPlayer.sendMessage("You have been Combat Tagged on Login");
			}
		}, 2L);
	}

	// =================================================
	// Water in the nether? Nope.
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent e) {
		if ((e.getBlockClicked().getBiome() == Biome.HELL)
				&& (e.getBucket() == Material.WATER_BUCKET)) {
			e.setCancelled(true);
			e.getItemStack().setType(Material.BUCKET);
			e.getPlayer().addPotionEffect(
					new PotionEffect(PotionEffectType.WATER_BREATHING, 5, 1));
		}

		// Indestructible end portal
		Block baseBlock = e.getBlockClicked();
		BlockFace face = e.getBlockFace();
		Block block = baseBlock.getRelative(face);
		if (block.getType() == Material.ENDER_PORTAL) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFromToEvent(BlockFromToEvent e) {
		if (e.getToBlock().getBiome() == Biome.HELL) {
			if ((e.getBlock().getType() == Material.WATER)
					|| (e.getBlock().getType() == Material.STATIONARY_WATER)) {
				e.setCancelled(true);
			}
		}

		if (e.getToBlock().getType() == Material.ENDER_PORTAL) {
			e.setCancelled(true);
		}
		if (!e.isCancelled()) {
			generateObsidian(e);
		}
	}

	
	/**
	 * Generates obsidian like it did in 1.7.
	 * Note that this does not change anything in versions where obsidian
	 * generation exists.
	 * @param e The event args
	 */
	public void generateObsidian(BlockFromToEvent e) {
		if (!e.getBlock().getType().equals(Material.STATIONARY_LAVA)) {
			return;
		}
		if (!e.getToBlock().getType().equals(Material.TRIPWIRE)) {
			return;
		}
		Block string = e.getToBlock();
		if (!(string.getRelative(BlockFace.NORTH).getType()
				.equals(Material.STATIONARY_WATER)
				|| string.getRelative(BlockFace.EAST).getType()
				.equals(Material.STATIONARY_WATER)
				|| string.getRelative(BlockFace.WEST).getType()
				.equals(Material.STATIONARY_WATER) || string
				.getRelative(BlockFace.SOUTH).getType()
				.equals(Material.STATIONARY_WATER))) {
			return;
		}
		string.setType(Material.OBSIDIAN);
	}
	

	/**
	 * Changes the yield from an XP bottle
	 * @param e
	 */
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent e) {
		final int bottle_xp = config.getXpBottleValue();
		((Player) e.getEntity().getShooter()).giveExp(bottle_xp);
	      e.setExperience(0);
	}


	/**
	 * Disables all XP gain except when manually changed via code.
	 * @param e The event args
	 */
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent e) {
		e.setAmount(0);
	}

	
	// ================================================
	// Find the end portals
	/*
	public static final int ender_portal_id_ = Material.ENDER_PORTAL.getId();
	public static final int ender_portal_frame_id_ = Material.ENDER_PORTAL_FRAME
			.getId();
	private Set<Long> end_portal_scanned_chunks_ = new TreeSet<Long>();


	@EventHandler
	public void onFindEndPortals(ChunkLoadEvent event) {
		World world = event.getWorld();
		if (!world.getName().equalsIgnoreCase("world")) {
			return;
		}
		Chunk chunk = event.getChunk();
		long chunk_id = ((long) chunk.getX() << 32L) + (long) chunk.getZ();
		if (end_portal_scanned_chunks_.contains(chunk_id)) {
			return;
		}
		end_portal_scanned_chunks_.add(chunk_id);
		int chunk_x = chunk.getX() * 16;
		int chunk_end_x = chunk_x + 16;
		int chunk_z = chunk.getZ() * 16;
		int chunk_end_z = chunk_z + 16;
		int max_height = 0;
		for (int x = chunk_x; x < chunk_end_x; x += 3) {
			for (int z = chunk_z; z < chunk_end_z; ++z) {
				int height = world.getMaxHeight();
				if (height > max_height) {
					max_height = height;
				}
			}
		}
		for (int y = 1; y <= max_height; ++y) {
			int z_adj = 0;
			for (int x = chunk_x; x < chunk_end_x; ++x) {
				for (int z = chunk_z + z_adj; z < chunk_end_z; z += 3) {
					int block_type = world.getBlockTypeIdAt(x, y, z);
					if (block_type == ender_portal_id_
							|| block_type == ender_portal_frame_id_) {
						SabrePlugin.getPlugin().log(Level.INFO, String.format("End portal found at %d,%d", x, z));
						return;
					}
				}
				// This funkiness results in only searching 48 of the 256 blocks
				// on
				// each y-level. 81.25% fewer blocks checked.
				++z_adj;
				if (z_adj >= 3) {
					z_adj = 0;
					x += 2;
				}
			}
		}
	} */

	// ================================================
	// Prevent inventory access while in a vehicle, unless it's the Player's

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPreventVehicleInvOpen(InventoryOpenEvent event) {
		// Cheap break-able conditional statement
		HumanEntity human = event.getPlayer();
		if (!(human instanceof Player)) {
			return;
		}
		if (!human.isInsideVehicle()) {
			return;
		}
		InventoryHolder holder = event.getInventory().getHolder();
		if (holder == human) {
			return;
		}

		if (!event.isCancelled()) {
			holder = event.getInventory().getHolder();
			if (holder instanceof StorageMinecart
					|| holder instanceof HopperMinecart) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPreventLandBoats(VehicleMoveEvent event) {
		final Vehicle vehicle = event.getVehicle();
		if (vehicle == null || !(vehicle instanceof Boat)) {
			return;
		}
		final Entity passenger = vehicle.getPassenger();
		if (passenger == null || !(passenger instanceof Player)) {
			return;
		}
		final Location to = event.getTo();
		final Material boatOn = to.getBlock().getType();
		if (boatOn.equals(Material.STATIONARY_WATER)
				|| boatOn.equals(Material.WATER)) {
			return;
		}
		vehicle.eject();
		vehicle.getWorld().dropItem(vehicle.getLocation(),
				new ItemStack(Material.BOAT));
		vehicle.remove();
	}

	// ================================================
	// Fix minecarts

	public boolean checkForTeleportSpace(Location loc) {
		final Block block = loc.getBlock();
		final Material mat = block.getType();
		if (mat.isSolid()) {
			return false;
		}
		final Block above = block.getRelative(BlockFace.UP);
		if (above.getType().isSolid()) {
			return false;
		}
		return true;
	}

	public boolean tryToTeleport(Player player, Location location, String reason) {
		Location loc = location.clone();
		loc.setX(Math.floor(loc.getX()) + 0.500000D);
		loc.setY(Math.floor(loc.getY()) + 0.02D);
		loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
		final Location baseLoc = loc.clone();
		final World world = baseLoc.getWorld();
		// Check if teleportation here is viable
		boolean performTeleport = checkForTeleportSpace(loc);
		if (!performTeleport) {
			loc.setY(loc.getY() + 1.000000D);
			performTeleport = checkForTeleportSpace(loc);
		}
		if (performTeleport) {
			player.setVelocity(new Vector());
			player.teleport(loc);
			return true;
		}
		loc = baseLoc.clone();
		// Create a sliding window of block types and track how many of those
		// are solid. Keep fetching the block below the current block to move
		// down.
		int air_count = 0;
		LinkedList<Material> air_window = new LinkedList<Material>();
		loc.setY((float) world.getMaxHeight() - 2);
		Block block = world.getBlockAt(loc);
		for (int i = 0; i < 4; ++i) {
			Material block_mat = block.getType();
			if (!block_mat.isSolid()) {
				++air_count;
			}
			air_window.addLast(block_mat);
			block = block.getRelative(BlockFace.DOWN);
		}
		// Now that the window is prepared, scan down the Y-axis.
		while (block.getY() >= 1) {
			Material block_mat = block.getType();
			if (block_mat.isSolid()) {
				if (air_count == 4) {
					player.setVelocity(new Vector());
					loc = block.getLocation();
					loc.setX(Math.floor(loc.getX()) + 0.500000D);
					loc.setY(loc.getY() + 1.02D);
					loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
					player.teleport(loc);
					return true;
				}
			} else { // !block_mat.isSolid()
				++air_count;
			}
			air_window.addLast(block_mat);
			if (!air_window.removeFirst().isSolid()) {
				--air_count;
			}
			block = block.getRelative(BlockFace.DOWN);
		}
		return false;
	}

	public void kickPlayerFromVehicle(Player player) {
		Entity vehicle = player.getVehicle();
		if (vehicle == null
				|| !(vehicle instanceof Minecart || vehicle instanceof Horse || vehicle instanceof Arrow)) {
			return;
		}
		Location vehicleLoc = vehicle.getLocation();
		// Vehicle data has been cached, now safe to kick the player out
		player.leaveVehicle();
		if (!tryToTeleport(player, vehicleLoc, "logged out")) {
			player.setHealth(0.000000D);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDisallowVehicleLogout(PlayerQuitEvent event) {
		kickPlayerFromVehicle(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFixMinecartReenterBug(VehicleExitEvent event) {
		final Vehicle vehicle = event.getVehicle();
		if (vehicle == null || !(vehicle instanceof Minecart)) {
			return;
		}
		final Entity passengerEntity = event.getExited();
		if (passengerEntity == null || !(passengerEntity instanceof Player)) {
			return;
		}
		// Must delay the teleport 2 ticks or else the player's mis-managed
		// movement still occurs. With 1 tick it could still occur.
		final Player player = (Player) passengerEntity;
		final Location vehicleLoc = vehicle.getLocation();
		Bukkit.getScheduler().runTaskLater(SabrePlugin.getPlugin(),
				new Runnable() {
			@Override
			public void run() {
				if (!tryToTeleport(player, vehicleLoc,
						"exiting vehicle")) {
					player.setHealth(0.000000D);
				}
			}
		}, 2L);
	}

	public void onFixMinecartReenterBug(VehicleDestroyEvent event) {
		final Vehicle vehicle = event.getVehicle();
		if (vehicle == null
				|| !(vehicle instanceof Minecart || vehicle instanceof Horse)) {
			return;
		}
		final Entity passengerEntity = vehicle.getPassenger();
		if (passengerEntity == null || !(passengerEntity instanceof Player)) {
			return;
		}
		// Must delay the teleport 2 ticks or else the player's mis-managed
		// movement still occurs. With 1 tick it could still occur.
		final Player player = (Player) passengerEntity;
		final Location vehicleLoc = vehicle.getLocation();
		Bukkit.getScheduler().runTaskLater(SabrePlugin.getPlugin(),
				new Runnable() {
			@Override
			public void run() {
				if (!tryToTeleport(player, vehicleLoc,
						"in destroyed vehicle")) {
					player.setHealth(0.000000D);
				}
			}
		}, 2L);
	}

	// ================================================
	// Prevent tree growth wrap-around

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onStructureGrowEvent(StructureGrowEvent event) {
		int maxY = 0, minY = 257;
		for (BlockState bs : event.getBlocks()) {
			final int y = bs.getLocation().getBlockY();
			maxY = Math.max(maxY, y);
			minY = Math.min(minY, y);
		}
		if (maxY - minY > 240) {
			event.setCancelled(true);
		}
	}

	// ================================================
	// Equipping banners
	public void onEquippingBanners(PlayerInteractEvent event) {
		if (event.getItem() == null
				|| !event.getItem().getType().equals(Material.BANNER)
				|| (event.getAction() != Action.LEFT_CLICK_AIR && event
				.getAction() != Action.LEFT_CLICK_BLOCK)) {
			return;
		}
		Player player = event.getPlayer();
		ItemStack banner = new ItemStack(event.getItem());
		banner.setAmount(1);
		player.getInventory().removeItem(banner);
		if (player.getEquipment().getHelmet() != null) {
			if (player.getInventory()
					.addItem(player.getEquipment().getHelmet()).size() != 0) {
				player.getWorld().dropItem(player.getLocation(),
						player.getEquipment().getHelmet());
			}
		}
		player.getEquipment().setHelmet(banner);
	}

	// ================================================
	// Disable changing spawners with eggs

	public void onChangingSpawners(PlayerInteractEvent event) {
		if ((event.getClickedBlock() != null) && (event.getItem() != null)
				&& (event.getClickedBlock().getType() == Material.MOB_SPAWNER)
				&& (event.getItem().getType() == Material.MONSTER_EGG)) {
			event.setCancelled(true);
		}
	}

	// there is a bug in minecraft 1.8, which allows fire and vines to spread
	// into unloaded chunks
	// where they can replace any existing block
	@EventHandler(priority = EventPriority.LOWEST)
	public void fixSpreadInUnloadedChunks(BlockSpreadEvent e) {
		if (!e.getBlock().getChunk().isLoaded()) {
			e.setCancelled(true);
		}
	}


	/**
	 * Checks if a material can mine ores other than iron
	 * @param m The material
	 * @return true if it's a pick type
	 */
	public static boolean isOrePick(Material m) {
		switch (m) {
		case STONE_PICKAXE:
		case IRON_PICKAXE:
		case GOLD_PICKAXE:
		case DIAMOND_PICKAXE:
			return true;
		default:
			return false;
		}
	}


	/**
	 * A better version of dropNaturally so the dropped item is placed
	 * where you would expect it to be
	 * @param l The location
	 * @param is The item to drop
	 */
	public static void dropItemAtLocation(Location l, ItemStack is) {
		l.getWorld().dropItem(l.add(0.5, 0.5, 0.5), is).setVelocity(new Vector(0, 0.05, 0));
	}

	/**
	 * A better version of dropNaturally so the dropped item is placed
	 * where you would expect it to be
	 * @param b The block to drop it at
	 * @param is The item to drop
	 */
	public static void dropItemAtLocation(Block b, ItemStack is) {
		dropItemAtLocation(b.getLocation(), is);
	}


	/**
	 * Gets the item stack for lore
	 * @param m The ore material
	 * @return The stack to drop
	 */
	private ItemStack getOreFortuneStack(Material m) {
		switch (m)
		{
		case GLOWING_REDSTONE_ORE:
			return new ItemStack(Material.REDSTONE, 5);
		case GOLD_ORE:
			return new ItemStack(Material.GOLD_INGOT, 1);
		case IRON_ORE:
			return new ItemStack(Material.IRON_INGOT, 1);
		case DIAMOND_ORE:
			return new ItemStack(Material.DIAMOND, 1);
		case LAPIS_ORE:
			return new ItemStack(Material.INK_SACK, 3, (short)4);
		case QUARTZ_ORE:
			return new ItemStack(Material.QUARTZ, 4);
		case COAL_ORE:
			return new ItemStack(Material.COAL, 6);
		case EMERALD_ORE:
			return new ItemStack(Material.EMERALD, 1);
		default:
			return null;
		}
	}

	/**
	 * Ores always drop the ore, not the mineral item
	 * 
	 * @param e The event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent e) {
		Material m = e.getBlock().getType();
		Material dropItem = Material.AIR;

		// Ignore if not using a pick
		if (!isOrePick(e.getPlayer().getItemInHand().getType())) {
			return;
		}

		// Ignore players in creative mode
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}

		switch (m) {
		case GLOWING_REDSTONE_ORE:
			dropItem = Material.REDSTONE_ORE;
			break;

		case DIAMOND_ORE:
		case LAPIS_ORE:
		case QUARTZ_ORE:
		case COAL_ORE:
		case EMERALD_ORE:
			dropItem = m;
			break;
		default:
			break;
		}

		if (dropItem != Material.AIR) {
			int fortuneLevel = e.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

			// Fortune gives odds to drop the mineral as well
			// F1 = 10%
			// F2 = 20%
			// F3 = 30%
			if (fortuneLevel > 0) {
				int randInt = rand.nextInt(25) + 1;

				if (fortuneLevel >= randInt) {
					ItemStack toDrop = getOreFortuneStack(m);
					if (toDrop != null) {
						dropItemAtLocation(e.getBlock(), toDrop);
						e.getBlock().setType(Material.AIR);
						e.setCancelled(true);
						return;
					}
				}
			}

			e.setCancelled(true);
			e.getBlock().setType(Material.AIR);
			dropItemAtLocation(e.getBlock(), new ItemStack(dropItem, 1));
		}
	}


	/**
	 * Blocks certain items from being cooked/smelted in vanilla furnaces
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFurnaceBurn(FurnaceBurnEvent e) {

		Furnace f = (Furnace)e.getBlock().getState();
		ItemStack smelting = f.getInventory().getSmelting();

		// Loop through the disabled items and cancel the burn if it's found
		for(SabreItemStack is : config.getDisabledSmelts()) {
			if (is.isSimilar(smelting)) {

				e.setBurning(false);
				e.setCancelled(true);
				break;
			}
		}
	}


	/**
	 * Blocks certain items from being cooked/smelted in vanilla furnaces
	 * @param e
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFurnaceSmelt(FurnaceSmeltEvent e) {
		ItemStack smelting = e.getSource();

		// Loop through the disabled items and cancel the burn if it's found
		for(SabreItemStack is : config.getDisabledSmelts()) {
			if (is.isSimilar(smelting)) {
				e.setResult(new ItemStack(Material.COAL, 1, (short)1));
				//e.setCancelled(true);
				break;
			}
		}
	}

	/**
	 * Fixes Teleporting through walls and doors
	 * ** and **
	 * Ender Pearl Teleportation disabling
	 * ** and **
	 * Ender pearl cooldown timer
	 */
	private class PearlTeleportInfo {
		public long last_teleport;
		public long last_notification;
	}


	/**
	 * Throttles how often an ender pearl can be thrown
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void throttlePearlTeleport(PlayerInteractEvent event) {
		if (event.getItem() == null || !event.getItem().getType().equals(Material.ENDER_PEARL)) {
			return;
		}
		
		final Action action = event.getAction();
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		
		final Block clickedBlock = event.getClickedBlock();
		BlockState clickedState = null;
		Material clickedMaterial = null;
		if (clickedBlock != null) {
			clickedState = clickedBlock.getState();
			clickedMaterial = clickedState.getType();
		}
		
		if (clickedState != null && (
				clickedState instanceof InventoryHolder
				|| clickedMaterial.equals(Material.ANVIL)
				|| clickedMaterial.equals(Material.ENCHANTMENT_TABLE)
				|| clickedMaterial.equals(Material.ENDER_CHEST)
				|| clickedMaterial.equals(Material.WORKBENCH))) {
			// Prevent Combat Tag/Pearl cooldown on inventory access
			return;
		}
		
		final long current_time = System.currentTimeMillis();
		final Player player = event.getPlayer();
		final String player_name = player.getName();
		PearlTeleportInfo teleport_info = pearlTeleportInfo.get(player_name);
		long time_diff = 0;
		
		if (teleport_info == null) {
			// New pearl thrown outside of throttle window
			teleport_info = new PearlTeleportInfo();
			teleport_info.last_teleport = current_time;
			teleport_info.last_notification =
					current_time - (PEARL_NOTIFICATION_WINDOW + 100);  // Force notify
			combatTag.tagPlayer(player);
		} else {
			time_diff = current_time - teleport_info.last_teleport;
			if (PEARL_THROTTLE_WINDOW > time_diff) {
				// Pearl throw throttled
				event.setCancelled(true);
			} else {
				// New pearl thrown outside of throttle window
				combatTag.tagPlayer(player);
				teleport_info.last_teleport = current_time;
				teleport_info.last_notification =
						current_time - (PEARL_NOTIFICATION_WINDOW + 100);  // Force notify
				time_diff = 0;
			}
		}
		
		final long notify_diff = current_time - teleport_info.last_notification;
		if (notify_diff > PEARL_NOTIFICATION_WINDOW) {
			teleport_info.last_notification = current_time;
			Integer tagCooldown = combatTag.remainingSeconds(player);
			
			if (tagCooldown != null) {
				player.sendMessage(String.format(
						"Pearl in %d seconds. Combat Tag in %d seconds.",
						(PEARL_THROTTLE_WINDOW - time_diff + 500) / 1000,
						tagCooldown));
			} else {
				player.sendMessage(String.format(
						"Pearl Teleport Cooldown: %d seconds",
						(PEARL_THROTTLE_WINDOW - time_diff + 500) / 1000));
			}
		}
		
		pearlTeleportInfo.put(player_name, teleport_info);
		return;
	}
	
	
	// ================================================
	// Hunger Changes

	// Keep track if the player just ate.
	private Map<Player, Double> playerLastEat_ = new HashMap<Player, Double>();

	@EventHandler
	public void setSaturationOnFoodEat(PlayerItemConsumeEvent event) {
		// Each food sets a different saturation.
		final Player player = event.getPlayer();
		ItemStack item = event.getItem();
		Material mat = item.getType();
		double multiplier = config.getFoodSaturationMultiplier();
		if (multiplier <= 0.000001 && multiplier >= -0.000001) {
			return;
		}
		switch(mat) {
		case APPLE:
			playerLastEat_.put(player, multiplier*2.4);
		case BAKED_POTATO:
			playerLastEat_.put(player, multiplier*7.2);
		case BREAD:
			playerLastEat_.put(player, multiplier*6);
		case CAKE:
			playerLastEat_.put(player, multiplier*0.4);
		case CARROT_ITEM:
			playerLastEat_.put(player, multiplier*4.8);
		case COOKED_FISH:
			playerLastEat_.put(player, multiplier*6);
		case GRILLED_PORK:
			playerLastEat_.put(player, multiplier*12.8);
		case COOKIE:
			playerLastEat_.put(player, multiplier*0.4);
		case GOLDEN_APPLE:
			playerLastEat_.put(player, multiplier*9.6);
		case GOLDEN_CARROT:
			playerLastEat_.put(player, multiplier*14.4);
		case MELON:
			playerLastEat_.put(player, multiplier*1.2);
		case MUSHROOM_SOUP:
			playerLastEat_.put(player, multiplier*7.2);
		case POISONOUS_POTATO:
			playerLastEat_.put(player, multiplier*1.2);
		case POTATO:
			playerLastEat_.put(player, multiplier*0.6);
		case RAW_FISH:
			playerLastEat_.put(player, multiplier*1);
		case PUMPKIN_PIE:
			playerLastEat_.put(player, multiplier*4.8);
		case RAW_BEEF:
			playerLastEat_.put(player,  multiplier*1.8);
		case RAW_CHICKEN:
			playerLastEat_.put(player, multiplier*1.2);
		case PORK:
			playerLastEat_.put(player,  multiplier*1.8);
		case ROTTEN_FLESH:
			playerLastEat_.put(player, multiplier*0.8);
		case SPIDER_EYE:
			playerLastEat_.put(player, multiplier*3.2);
		case COOKED_BEEF:
			playerLastEat_.put(player, multiplier*12.8);
		default:
			playerLastEat_.put(player, multiplier);
			Bukkit.getServer().getScheduler().runTaskLater(SabrePlugin.getPlugin(), new Runnable() {
				// In case the player ingested a potion, this removes the
				// saturation from the list. Unsure if I have every item
				// listed. There is always the other cases of like food
				// that shares same id
				@Override
				public void run() {
					playerLastEat_.remove(player);
				}
			}, 80);
		}
	}

	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		final Player player = (Player) event.getEntity();
		final double mod = config.getHungerSlowdown();
		Double saturation;
		if (playerLastEat_.containsKey(player)) { // if the player just ate
			saturation = playerLastEat_.get(player);
			if (saturation == null) {
				saturation = ((Float)player.getSaturation()).doubleValue();
			}
		} else {
			saturation = Math.min(
					player.getSaturation() + mod,
					20.0D + (mod * 2.0D));
		}
		player.setSaturation(saturation.floatValue());
	}
	
	
	private static int SIGN_LIMIT = 100;
	

	/**
	 * Enforce good sign data length
	 * @param e The event args
	 */
	@EventHandler(ignoreCancelled=true)
	public void onSignFinalize(SignChangeEvent e) {
		String[] signdata = e.getLines();

		for (int i = 0; i < signdata.length; i++) {
			if (signdata[i] != null && signdata[i].length() > SIGN_LIMIT) {
				Player p = e.getPlayer();
				Location location = e.getBlock().getLocation();
				SabrePlugin.getPlugin().log(Level.WARNING, String.format(
						"Player '%s' [%s] attempted to place sign at ([%s] %d, %d, %d) with line %d having length %d > %d. Preventing.", 
						p.getPlayerListName(), p.getUniqueId(), location.getWorld().getName(), 
						location.getBlockX(), location.getBlockY(), location.getBlockZ(),
						i, signdata[i].length(), SIGN_LIMIT));

				e.setLine(i, "");
			}
		}
	}

	private HashMap<String, Set<Long>> signs_scanned_chunks_ = new HashMap<String, Set<Long>>();

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
	public void onSignLoads(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();
		String world = chunk.getWorld().getName();

		long chunk_id = ((long)chunk.getX() << 32L) + (long)chunk.getZ();
		if (signs_scanned_chunks_.containsKey(world)) {
			if (signs_scanned_chunks_.get(world).contains(chunk_id)) {
				return;
			}
		} else {
			signs_scanned_chunks_.put(world, new TreeSet<Long>()); 
		}
		signs_scanned_chunks_.get(world).add(chunk_id);

		BlockState[] allTiles = chunk.getTileEntities();

		for(BlockState tile: allTiles) {
			if (tile instanceof Sign) {
				Sign sign = (Sign) tile;
				String[] signdata = sign.getLines();
				for (int i = 0; i < signdata.length; i++) {
					if (signdata[i] != null && signdata[i].length() > SIGN_LIMIT) {
						Location location = sign.getLocation();
						SabrePlugin.getPlugin().log(Level.WARNING, String.format(
								"Line %d in sign at ([%s] %d, %d, %d) is length %d > %d. Curating.", i,
								world, location.getBlockX(), location.getBlockY(), location.getBlockZ(),
								signdata[i].length(), SIGN_LIMIT));


						sign.setLine(i, "");

						sign.update(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void adminAccessBlockedChest(PlayerInteractEvent e) {
		if (!e.getPlayer().hasPermission("admin") && !e.getPlayer().isOp()) {
			return;
		}
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			Set <Material> s = new TreeSet<Material>();
			s.add(Material.AIR);
			s.add(Material.OBSIDIAN); //probably in a vault
			List <Block> blocks = p.getLineOfSight(s, 8);
			for(Block b:blocks) {
				Material m = b.getType();
				if(m == Material.CHEST || m == Material.TRAPPED_CHEST) {
					if(b.getRelative(BlockFace.UP).getType().isOccluding()) {
						//dont show inventory twice if a normal chest is opened
						final Inventory che_inv = ((InventoryHolder)b.getState()).getInventory();
						p.openInventory(che_inv);
						p.updateInventory();	  
					}
					break;
				}
			}
		}
	}
	
	
    /**
     * Checks for boats that collide with cacti. This is needed as CraftBukkit does not
     * appear to pass cactus damage source/events to VehicleDestroyEvent.
     * @param e The event args
     */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleCollide(VehicleBlockCollisionEvent e)
    {
        if (e.getVehicle().getType() == EntityType.BOAT && e.getBlock().getType() == Material.CACTUS) {
            e.getVehicle().setMetadata(CACTUS_HIT_TAG, CACTUS_HIT);
        }
    }
	
	
    /**
     * Prevents boats from breaking easily
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onVehicleDestroyed(VehicleDestroyEvent e)
    {
        if ( e.getVehicle().getType() != EntityType.BOAT || e.isCancelled() )
            return;

        // Do not cancel fire damage
        if (e.getVehicle().getFireTicks() != -1)
            return;

        // If damage is from cactus, don't stop it. This allows boat collection systems
        // to work as intended. Relies on metadata set in onVehicleCollide
        if ( e.getVehicle().hasMetadata(CACTUS_HIT_TAG) ) {
            return;
        }

        // If attacked by a player or entity, don't stop it
        if (e.getAttacker() != null)
            return;

        e.setCancelled(true);
    }
}
