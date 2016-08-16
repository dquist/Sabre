package com.civfactions.sabre.prisonpearl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minelink.ctplus.Npc;
import net.minelink.ctplus.event.NpcDespawnEvent;
import net.minelink.ctplus.event.NpcDespawnReason;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.prisonpearl.PrisonPearlEvent.Type;
import com.civfactions.sabre.util.SabreUtil;
import com.mojang.authlib.GameProfile;

/**
 * Handles events related to prison pearls
 * @author GFQ
 */
public class PearlListener implements Listener {

	private final SabrePlugin plugin;
	private final PlayerManager pm;
	private final PearlManager pearls;

	/**
	 * Creates a new PearlListener instance
	 * @param pearls The pearl manager
	 * @param pm The player manager
	 */
	public PearlListener(SabrePlugin plugin, PlayerManager pm, PearlManager pearls) {
		this.plugin = plugin;
		this.pm = pm;
		this.pearls = pearls;
	}


	/**
	 * Announce the person in a pearl when a player holds it
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onItemHeldChange(PlayerItemHeldEvent event) {

		Inventory inv = event.getPlayer().getInventory();
		ItemStack item = inv.getItem(event.getNewSlot());
		ItemStack newitem = validatePearl(event.getPlayer(), item);
		if (newitem != null) {
			inv.setItem(event.getNewSlot(), newitem);
		}
	}


	/**
	 * Announces a pearl change
	 * @param player 
	 * @param item
	 * @return
	 */
	private ItemStack validatePearl(Player player, ItemStack item) {
		if (item == null) {
			return null;
		}

		if (item.getType() == Material.ENDER_PEARL && PrisonPearl.getIDFromItemStack(item) != null) {
			PrisonPearl pp = pearls.getPearlByItem(item);
			if (pp == null) {
				return new ItemStack(Material.ENDER_PEARL, 1);
			}
			pp.markMove();
			//generatePearlEvent(pp, Type.HELD);
		}

		return null;
	}


	/**
	 * Track the location of a pearl if it spawns as an item for any reason
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemSpawn(ItemSpawnEvent e) {
		Item item = e.getEntity();

		PrisonPearl pp = pearls.getPearlByItem(item.getItemStack());
		if (pp == null) {
			return;
		}

		pp.markMove();
		pp.setHolder(item.getLocation());
		pearls.updatePearl(pp);
		updatePearl(pp, e.getEntity());
	}


	/**
	 * Handles a combattag NPC despawn event
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onNpcDespawn(NpcDespawnEvent e) {
    	NpcDespawnReason reason = e.getDespawnReason();
    	Npc npc = e.getNpc();
    	Player p = npc.getEntity();
    	Location loc = p.getLocation();
    	if (reason == NpcDespawnReason.DESPAWN) {
        	handleNpcDespawn(p.getUniqueId(), loc);
    	}
	}


	/**
	 * Handles an NPC despawn event
	 * @param plruuid
	 * @param loc
	 */
	public void handleNpcDespawn(UUID plruuid, Location loc) {
		World world = loc.getWorld();
		Server bukkitServer = SabrePlugin.instance().getServer();
		Player player = bukkitServer.getPlayer(plruuid);
		if (player == null) { // If player is offline
			MinecraftServer server = ((CraftServer)SabrePlugin.instance().getServer()).getServer();
			GameProfile prof = new GameProfile(plruuid, null);
			EntityPlayer entity = new EntityPlayer(
					server, server.getWorldServer(0), prof,
					new PlayerInteractManager(server.getWorldServer(0)));
			player = (entity == null) ? null : (Player) entity.getBukkitEntity();
			if (player == null) {
				return;
			}
			player.loadData();
		}

		Inventory inv = player.getInventory();
		int end = inv.getSize();
		for (int slot = 0; slot < end; ++slot) {
			ItemStack item = inv.getItem(slot);
			if (item == null) {
				continue;
			}
			if (!item.getType().equals(Material.ENDER_PEARL)) {
				continue;
			}

			PrisonPearl pp = pearls.getPearlByItem(item);
			if (pp == null){
				continue;
			}

			// Drops pearl to ground.
			inv.clear(slot);
			world.dropItemNaturally(loc, item);  
		}
		player.saveData();
	}


	/**
	 * Drops a pearl when the player leaves the game
	 * @param event The event args
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player imprisoner = event.getPlayer();

		if (SabrePlugin.instance().getCombatTag().isTagged(imprisoner.getUniqueId())) {
			return; // if player is tagged
		}


		Location loc = imprisoner.getLocation();
		World world = imprisoner.getWorld();
		Inventory inv = imprisoner.getInventory();
		for (Entry<Integer, ? extends ItemStack> entry :
			inv.all(Material.ENDER_PEARL).entrySet()) {
			ItemStack item = entry.getValue();
			PrisonPearl pp = pearls.getPearlByItem(item);
			if (pp == null) {
				continue;
			}
			pp.markMove();
			int slot = entry.getKey();
			inv.clear(slot);
			world.dropItemNaturally(loc, item);
		}
		imprisoner.saveData();
	}



	/**
	 * Prevents a pearl from despawning
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemDespawn(ItemDespawnEvent e) {
		PrisonPearl pp = pearls.getPearlByItem(e.getEntity().getItemStack());
		if (pp != null) {
			e.setCancelled(true);
		}
	}


	/**
	 * Free the pearl if it burns up
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityCombustEvent(EntityCombustEvent e) {
		if (!(e.getEntity() instanceof Item)) {
			return;
		}

		PrisonPearl pp = pearls.getPearlByItem(((Item) e.getEntity()).getItemStack());
		if (pp == null) {
			return;
		}

		SabrePlugin.log(Level.INFO, "%s (%s) is being freed. Reason: PrisonPearl combusted(lava/fire).", pp.getName(), pp.getPlayerID());
		pearls.freePearl(pp);
	}


	/**
	 * Handle inventory dragging properly
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {

		Map<Integer, ItemStack> items = event.getNewItems();

		for(Integer slot : items.keySet()) {
			ItemStack item = items.get(slot);

			PrisonPearl pearl = pearls.getPearlByItem(item);
			if(pearl != null) {
				boolean clickedTop = event.getView().convertSlot(slot) == slot;

				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

				pearl.markMove();
				updatePearlHolder(pearl, holder, event);

				if(event.isCancelled()) {
					return;
				}
			}
		}
	}


	/**
	 * Updates the pearl holder
	 * @param pearl The pearl to update
	 * @param holder The pearl holder
	 * @param event The event
	 */
	private void updatePearlHolder(PrisonPearl pp, InventoryHolder holder, Cancellable event) {

		if (holder instanceof Chest) {
			updatePearl(pp, (Chest)holder);
		} else if (holder instanceof DoubleChest) {
			updatePearl(pp, (Chest) ((DoubleChest) holder).getLeftSide());
		} else if (holder instanceof Furnace) {
			updatePearl(pp, (Furnace) holder);
		} else if (holder instanceof Dispenser) {
			updatePearl(pp, (Dispenser) holder);
		} else if (holder instanceof BrewingStand) {
			updatePearl(pp, (BrewingStand) holder);
		} else if (holder instanceof Player) {
			updatePearl(pp, (Player) holder);
		}else {
			event.setCancelled(true);
		}
	}
	
	
	/**
	 * Updates the pearl status
	 * @param pp The prison pearl
	 * @param item The pearl item
	 */
	private void updatePearl(PrisonPearl pp, Item item) {
		pp.setHolder(item.getLocation());
		pearls.updatePearl(pp);
		generatePearlEvent(pp, Type.DROPPED);
	}


	/**
	 * Updates the pearl status
	 * @param pp The prison pearl
	 * @param block The block storing the pearl
	 */
	private <ItemBlock extends InventoryHolder & BlockState> void updatePearl(PrisonPearl pp, ItemBlock block) {
		pp.setHolder(block.getBlock());
		pearls.updatePearl(pp);
		generatePearlEvent(pp, Type.HELD);
	}

	
	/**
	 * Updates the pearl status
	 * @param pp The prison pearl
	 * @param player The player holding the pearl
	 */
	private void updatePearl(PrisonPearl pp, Player player) {
		pp.setHolder(pm.getPlayerById(player.getUniqueId()));
		pearls.updatePearl(pp);
		generatePearlEvent(pp, Type.HELD);
	}


	/**
	 * Prevent imprisoned players from placing PrisonPearls in their inventory.
	 * @param event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPrisonPearlClick(InventoryClickEvent e) {
		Player clicker = (Player) e.getWhoClicked();

		PrisonPearl pearl = pearls.getPearlByItem(e.getCurrentItem());
		if(pearl != null) {
			if (pearls.isImprisoned(clicker)) {
				pm.getPlayerById(clicker.getUniqueId()).msg(Lang.pearlCantHold);
				e.setCancelled(true);
			}
		}
	}


	/**
	 * Prevent imprisoned players from picking up Prisonpearls.
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupPearl(PlayerPickupItemEvent e) {
		Item item = e.getItem();

		PrisonPearl pp = pearls.getPearlByItem(item.getItemStack());
		if (pp == null) {
			return;
		}

		if (pearls.isImprisoned(e.getPlayer())) {
			e.setCancelled(true);
		}
	}


	/**
	 * Track the location of a pearl
	 * Forbid pearls from being put in storage minecarts
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {

		// Announce an prison pearl if it is clicked
		ItemStack newitem = validatePearl((Player) event.getWhoClicked(), event.getCurrentItem());
		if (newitem != null) {
			event.setCurrentItem(newitem);
		}

		InventoryAction a = event.getAction();
		if(a == InventoryAction.COLLECT_TO_CURSOR || a == InventoryAction.PICKUP_ALL 
				|| a == InventoryAction.PICKUP_HALF || a == InventoryAction.PICKUP_ONE) {
			PrisonPearl pearl = pearls.getPearlByItem(event.getCurrentItem());

			if(pearl != null) {
				pearl.markMove();
				updatePearl(pearl, (Player) event.getWhoClicked());
			}
		}
		else if(event.getAction() == InventoryAction.PLACE_ALL
				|| event.getAction() == InventoryAction.PLACE_SOME
				|| event.getAction() == InventoryAction.PLACE_ONE) {	
			PrisonPearl pearl = pearls.getPearlByItem(event.getCursor());

			if(pearl != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();

				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				if (holder==null) {
					pearl.markMove();
				}
				else {
					pearl.markMove();
					updatePearlHolder(pearl, holder, event);
				}
			}
		}
		else if(event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {			
			PrisonPearl pearl = pearls.getPearlByItem(event.getCurrentItem());

			if(pearl != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();

				InventoryHolder holder = !clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();
				if (holder==null) {
					pearl.markMove();
				}
				else if(holder.getInventory().firstEmpty() >= 0) {
					pearl.markMove();
					updatePearlHolder(pearl, holder, event);
				}
			}
		}
		else if(event.getAction() == InventoryAction.HOTBAR_SWAP) {
			PlayerInventory playerInventory = event.getWhoClicked().getInventory();
			PrisonPearl pearl = pearls.getPearlByItem(playerInventory.getItem(event.getHotbarButton()));

			if(pearl != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();

				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

				pearl.markMove();
				updatePearlHolder(pearl, holder, event);
			}

			if(event.isCancelled())
				return;

			pearl = pearls.getPearlByItem(event.getCurrentItem());

			if(pearl != null) {
				pearl.markMove();
				updatePearl(pearl, (Player) event.getWhoClicked());
			}
		}
		else if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
			PrisonPearl pearl = pearls.getPearlByItem(event.getCursor());

			if(pearl != null) {
				boolean clickedTop = event.getView().convertSlot(event.getRawSlot()) == event.getRawSlot();

				InventoryHolder holder = clickedTop ? event.getView().getTopInventory().getHolder() : event.getView().getBottomInventory().getHolder();

				pearl.markMove();
				updatePearlHolder(pearl, holder, event);
			}

			if(event.isCancelled())
				return;

			pearl = pearls.getPearlByItem(event.getCurrentItem());

			if(pearl != null) {
				pearl.markMove();
				updatePearl(pearl, (Player) event.getWhoClicked());
			}
		}
		else if(event.getAction() == InventoryAction.DROP_ALL_CURSOR
				|| event.getAction() == InventoryAction.DROP_ALL_SLOT
				|| event.getAction() == InventoryAction.DROP_ONE_CURSOR
				|| event.getAction() == InventoryAction.DROP_ONE_SLOT) {
			// Handled by onItemSpawn
		}
		else {
			if(pearls.getPearlByItem(event.getCurrentItem()) != null || pearls.getPearlByItem(event.getCursor()) != null) {
				((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "Error: PrisonPearl doesn't support this inventory functionality quite yet!");

				event.setCancelled(true);
			}
		}
	}


	/**
	 * Track the location of a pearl if a player picks it up
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Item item = e.getItem();

		PrisonPearl pp = pearls.getPearlByItem(item.getItemStack());
		if (pp == null) {
			return;
		}

		pp.markMove();
		pp.setHolder(pm.getPlayerById(e.getPlayer().getUniqueId()));
		pearls.updatePearl(pp);
		updatePearl(pp, (Player) e.getPlayer());
	}


	/**
	 * Imprison people upon death
	 * @param event The event args
	 */
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player)e.getEntity();
		
		PrisonPearl pp = pearls.getById(player.getUniqueId());
		if (pp != null) {
			if (pp.getSummoned()) {
				pp.setSummoned(false);
			}
		}


		Player killer = player.getKiller();
		if (killer != null) {
			IPlayer imprisoner = pm.getPlayerById(killer.getUniqueId());
			
			// Need to get by name b/c of combat tag entity
			IPlayer imprisoned = pm.getPlayerByName(e.getEntity().getName());

			int firstpearl = Integer.MAX_VALUE;
			for (Entry<Integer, ? extends ItemStack> entry : killer.getInventory().all(Material.ENDER_PEARL).entrySet()) {

				// Make sure we're holding a blank pearl
				if (PrisonPearl.getIDFromItemStack(entry.getValue()) == null) {
					firstpearl = Math.min(entry.getKey(), firstpearl);
				}
			}

			if (firstpearl  !=  Integer.MAX_VALUE) {
				pearls.imprisonPlayer(imprisoned, imprisoner);
			}

		}
	}


	/**
	 * Adjust player spawn point if necessary
	 * @param event
	 */
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e) {

		PrisonPearl pp = pearls.getById(e.getPlayer().getUniqueId());
		if (pp == null) {
			return;
		}

		prisonMotd(pp);
		Location spawnLocation = getRespawnLocation(pp, e.getRespawnLocation());
		e.setRespawnLocation(spawnLocation);
	}
	
	
	/**
	 * Handles logging in players
	 * @param e The event args
	 */
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		IPlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
		if (sp.getFreedOffline()) {

			pm.setFreedOffline(sp, false);
			plugin.getSpawner().spawnPlayerBed(sp);
		}
	}


	/**
	 * Sends the MOTD to the prisoner
	 * @param p The prison pearl
	 */
	private void prisonMotd(PrisonPearl pp) {

		if (!pp.getSummoned()) {
			pp.getPlayer().msg(Lang.pearlMotd);
			pp.getPlayer().msg(Lang.pearlMotd2);
		}
	}


	
	/**
	 * Gets where the player should be re-spawned at
	 * @param player The player
	 * @param curloc
	 * @return The respawn location, or null
	 */
	private Location getRespawnLocation(PrisonPearl pp, Location l) {

		// Don't modify the location for summoned player
		if (pp.getSummoned()) {
			return l;
		}

		// Should be respawned in prison
		if (!l.getWorld().equals(pearls.getPrisonWorld())) {
			return getPrisonSpawnLocation();
		}

		// Don't modify respawn location
		return l; 
	}



	/**
	 * Hill climbing algorithm which attempts to randomly spawn prisoners while actively avoiding pits
	 * the obsidian pillars, or lava.
	 * @return The new location
	 */
	private Location getPrisonSpawnLocation() {
		Random rand = new Random();

		// Start at spawn
		Location loc = pearls.getPrisonWorld().getSpawnLocation();

		// For up to 30 iterations
		for (int i = 0; i < 30; i++) {

			// if the current candidate looks reasonable and we've iterated at least 5 times
			if (loc.getY() > 40 && loc.getY() < 70 && i > 5 && !isObstructed(loc))
				return loc; // we're done

			// pick a new location near the current one
			Location newloc = loc.clone().add(rand.nextGaussian()*(2*i), 0, rand.nextGaussian()*(2*i));
			newloc = moveToGround(newloc);
			if (newloc == null)
				continue;

			// if its better in a fuzzy sense, or if the current location is too high
			if (newloc.getY() > loc.getY()+(int)(rand.nextGaussian()*3) || loc.getY() > 70)
				loc = newloc; // it becomes the new current location
		}

		return loc;
	}


	private Location moveToGround(Location loc) {
		Location ground = new Location(loc.getWorld(), loc.getX(), 100, loc.getZ());
		while (ground.getBlockY() >= 1) {
			if (!ground.getBlock().isEmpty())
				return ground;
			ground.add(0, -1, 0);
		}
		return null;
	}

	private boolean isObstructed(Location loc) {
		Location ground = new Location(loc.getWorld(), loc.getX(), 100, loc.getZ());
		while (ground.getBlockY() >= 1) {
			if (!ground.getBlock().isEmpty())
				break;

			ground.add(0, -1, 0);
		}

		for (int x=-2; x<=2; x++) {
			for (int y=-2; y<=2; y++) {
				for (int z=-2; z<=2; z++) {
					Location l = ground.clone().add(x, y, z);
					Material type = l.getBlock().getType();
					if (type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.ENDER_PORTAL || type == Material.BEDROCK)
						return true;
				}
			}
		}

		return false;
	}


	/**
	 * Prevent pearling with ender pearls
	 * @param e The event args
	 */
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getPlayer().getItemInHand().getType().equals(Material.ENDER_PEARL)) {
				PrisonPearl pp = pearls.getPearlByItem(e.getItem());
				if (pp != null) {
					// Update the pearl
					e.getPlayer().setItemInHand(pp.createItemStack());
					e.setCancelled(true);
				}
			}
		}
	}
	
	
	/**
	 * Generates a new prison pearl event
	 * @param pearl The pearl
	 * @param type The event type
	 */
	public void generatePearlEvent(PrisonPearl pp, PrisonPearlEvent.Type type) {
		Bukkit.getPluginManager().callEvent(
				new PrisonPearlEvent(pp, PrisonPearlEvent.Type.DROPPED));
	}
	
	
	/**
	 * Handles prison pearl events
	 * @param event
	 */
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void onPrisonPearlEvent(PrisonPearlEvent event) {
		
		PrisonPearl pp = event.getPrisonPearl();
		IPlayer imprisoned = pm.getPlayerById(pp.getPlayerID());
		
		if (event.getType() == PrisonPearlEvent.Type.NEW) {

			IPlayer imprisoner = pm.getPlayerById(event.getImprisoner().getUniqueId());
			// Log the capturing PrisonPearl event.
			SabrePlugin.log(Level.INFO, String.format("%s has bound %s to a PrisonPearl", imprisoner.getName(), imprisoned.getName()));
			
			imprisoner.msg(Lang.pearlYouBound, imprisoned.getName());
			imprisoned.msg(Lang.pearlYouWereBound, imprisoner.getName());
			
		} else if (event.getType() == PrisonPearlEvent.Type.DROPPED || event.getType() == PrisonPearlEvent.Type.HELD) {
			
			Location l = pp.getHolder().getLocation();
			String name = pp.getHolder().getName();
			imprisoned.msg(Lang.pearlPearlIsHeld, name, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());	
			
			String bcastMsg = plugin.txt().parse(Lang.pearlBroadcast, imprisoned.getName(), 
					name, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
			
			for(IPlayer p : imprisoned.getBcastPlayers()) {
				if (p.isOnline()) {
					p.msg(bcastMsg);
				}
			}

		} else if (event.getType() == PrisonPearlEvent.Type.FREED) {
			
			Player player = imprisoned.getPlayer();
			
			if (player != null) {
				Location currentLoc = player.getLocation();
				if (!player.isDead() && currentLoc.getWorld() == pearls.getPrisonWorld()) {
					
					// if the player isn't dead and is in prison world
					Location loc = null;
					
					// get the location of the pearl
					loc = SabreUtil.fuzzLocation(pp.getHolder().getLocation());
					
					// if we don't have a location yet
					if (loc == null) {
						loc = getRespawnLocation(pp, currentLoc); // get the respawn location for the player
					}
					
					player.teleport(loc); // otherwise teleport
				}
				
				dropInventory(player, currentLoc);
			}
			
			imprisoned.msg(Lang.pearlYouWereFreed);
		}
	}
	
	/**
	 * Clears out a player's inventory when being summoned from the end
	 * @param player The player instance
	 * @param loc The location
	 */
    public void dropInventory(Player player, Location loc) {
		if (loc == null) {
			loc = player.getLocation();
		}
		World world = loc.getWorld();
		Inventory inv = player.getInventory();
		int end = inv.getSize();
		for (int i = 0; i < end; ++i) {
			ItemStack item = inv.getItem(i);
			if (item == null) {
				continue;
			}
			if (item.getType().equals(Material.ENDER_PEARL)) {
				continue;
			}
			inv.clear(i);
			world.dropItemNaturally(loc, item);
		}
	}
    

    /**
     * Announce summon events
     * Teleport player when summoned or returned
     * @param e The event args
     */
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	public void onSummonEvent(SummonEvent e) {
		
		PrisonPearl pp = e.getPrisonPearl();
		IPlayer p = pp.getPlayer();
		if (p == null) {
			return;
		}

		switch (e.getType()) {
		case SUMMONED:
			p.msg("<b>You've been summoned to your prison pearl!");
			Location oldLoc = p.getPlayer().getLocation();
			p.getPlayer().teleport(SabreUtil.fuzzLocation(e.getLocation()));
			dropInventory(p.getPlayer(), oldLoc);
			break;

		case RETURNED:

			p.msg("<b>You've been returned to your prison.");
			if (p.getPlayer().isInsideVehicle())
				p.getPlayer().eject();
			p.getPlayer().teleport(e.getLocation());
			break;

		case KILLED:
			p.msg("<b>You've been struck down by your pearl!");
			p.getPlayer().setHealth(0.0);
			break;
		default:
			break;
		}
	}
	
	
    /**
     * Prevents summoned players from doing damage
     * @param e The event args
     */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageByEntityEvent e) {
		
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)e.getDamager();
        
        if (pearls.isSummoned(player)) {
        	e.setCancelled(true);
        }
	}
}
