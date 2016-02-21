package com.gordonfreemanq.sabre.prisonpearl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.data.IDataAccess;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearlEvent.Type;

/**
 * The prison pearl manager
 * @author GFQ
 */
public class PearlManager {

	private final IDataAccess db;
	private final SabreConfig config;
	private final HashMap<UUID, PrisonPearl> pearls;
	
	private static PearlManager instance;
	
	public static PearlManager getInstance() {
		return instance;
	}
	
	
	/**
	 * Creates a new PearlManager instance
	 * @param db The database connector
	 */
	public PearlManager(IDataAccess db, SabreConfig config) {
		this.db = db;
		this.config = config;
		this.pearls = new HashMap<UUID, PrisonPearl>();
		
		instance = this;
	}
	
	
	/**
	 * Loads all the pearls from the database
	 */
	public void load() {
		for (PrisonPearl p : db.pearlGetall()) {
			pearls.put(p.getPlayerID(), p);
		}
	}
	
	
	/**
	 * Gets the pearled players
	 * @return The collection of pearled players
	 */
	public Collection<PrisonPearl> getPearls() {
		return pearls.values();
	}
	
	
	/**
	 * Imprisons a player
	 * @param pearl The pearl instance
	 */
	public PrisonPearl imprisonPlayer(SabrePlayer imprisoned, SabrePlayer imprisoner) {
		
		if (this.isImprisoned(imprisoned)) {
			imprisoner.msg(Lang.pearlAlreadyPearled, imprisoned.getName());
			return null;
		}
		
		// set up the imprisoner's inventory
		Inventory inv = imprisoner.getPlayer().getInventory();
		ItemStack stack = null;
		int stacknum = -1;
		
		// Scan for the smallest stack of normal ender pearls
		for (Entry<Integer, ? extends ItemStack> entry :
				inv.all(Material.ENDER_PEARL).entrySet()) {
			ItemStack newstack = entry.getValue();
			int newstacknum = entry.getKey();
			if (newstack.getDurability() == 0) {
				if (stack != null) {
					// don't keep a stack bigger than the previous one
					if (newstack.getAmount() > stack.getAmount()) {
						continue;
					}
					// don't keep an identical sized stack in a higher slot
					if (newstack.getAmount() == stack.getAmount() &&
							newstacknum > stacknum) {
						continue;
					}
				}

				stack = newstack;
				stacknum = entry.getKey();
			}
		}
		
		
		int pearlnum;
		ItemStack dropStack = null;
		if (stacknum == -1) { // no pearl (admin command)
			// give him a new one at the first empty slot
			pearlnum = inv.firstEmpty();
		} else if (stack.getAmount() == 1) { // if he's just got one pearl
			pearlnum = stacknum; // put the prison pearl there
		} else {
			// otherwise, put the prison pearl in the first empty slot
			pearlnum = inv.firstEmpty();
			if (pearlnum > 0) {
				// and reduce his stack of pearls by one
				stack.setAmount(stack.getAmount() - 1);
				inv.setItem(stacknum, stack);
			} else { // no empty slot?
				dropStack = new ItemStack(Material.ENDER_PEARL, stack.getAmount() - 1);
				pearlnum = stacknum; // then overwrite his stack of pearls
			}
		}
		
		// Drop pearls that otherwise would be deleted
		Location l = imprisoner.getPlayer().getLocation();
		if (dropStack != null) {
			imprisoner.getPlayer().getWorld().dropItem(l, dropStack);
			SabrePlugin.getPlugin().log(Level.INFO, l + ", " + dropStack.getAmount());
		}
		
		
		PrisonPearl pp = new PrisonPearl(imprisoned, imprisoner);
		pp.setSealStrength(config.getPearlDefaultStrength());
		pp.markMove();
		

		PrisonPearlEvent e = new PrisonPearlEvent(pp, Type.NEW, imprisoner.getPlayer());
		Bukkit.getPluginManager().callEvent(e);
		if (e.isCancelled()) {
			return null;
		}
		
		inv.setItem(pearlnum, pp.createItemStack());
		pearls.put(pp.getPlayerID(), pp);
		db.pearlInsert(pp);
		
		return pp;
	}
	
	
	/**
	 * Frees a pearl's player
	 * @param pearl The pearl to free
	 */
	public boolean freePearl(PrisonPearl pp) {
		
		PrisonPearlEvent e = new PrisonPearlEvent(pp, Type.FREED, pp.getPlayer().getPlayer());
		Bukkit.getPluginManager().callEvent(e);
		
		if (!e.isCancelled()) {
			pearls.remove(pp.getPlayerID());
			db.pearlRemove(pp);
			return true;
		}
		return false;
	}
	
	
	/**
	 * Removes a player
	 * @param pearl The player instance to free
	 */
	public void freePlayer(SabrePlayer p) {
		PrisonPearl pp = pearls.remove(p.getID());
		db.pearlRemove(pp);
	}
	
	
	/**
	 * Updates a pearl
	 * @param pearl The pearl to update
	 */
	public void updatePearl(PrisonPearl pp) {
		db.pearlUpdate(pp);
	}
	
	
	/**
	 * Updates a pearl summoned status
	 * @param pearl The pearl to update
	 * @param summoned Whether the player is summoned
	 */
	public void setPearlSummoned(PrisonPearl pp, boolean summoned) {
		pp.setSummoned(summoned);
		db.pearlUpdateSummoned(pp);
	}
	
	
	/**
	 * Updates a pearl return location
	 * @param pearl The pearl to update
	 * @param returnLocation The pearl return location
	 */
	public void setReturnLocation(PrisonPearl pp, Location returnLocation) {
		pp.setReturnLocation(returnLocation);
		db.pearlUpdateReturnLocation(pp);
	}
	
	
	/**
	 * Updates a pearl seal strength
	 * @param pearl The pearl to update
	 * @param sealStrength The new strength
	 */
	public void setSealStrength(PrisonPearl pp, int sealStrength) {
    	if (sealStrength < 0) {
    		sealStrength = 0;
    	}
    	
    	if (sealStrength > 0) {
    		pp.setSealStrength(sealStrength);
    		db.pearlUpdateSealStrength(pp);
    	} else {
    		this.freePearl(pp);
    	}
	}
	
	
	/**
	 * Weakens a pearl seal strength
	 * @param pearl The pearl to update
	 * @param weakenAmount The amount to decrease the strength by
	 */
	public void decreaseSealStrength(PrisonPearl pp, int amount) {
		this.setSealStrength(pp, pp.getSealStrength() - amount);
	}
	
	/**
	 * Increases a pearl seal strength
	 * @param pearl The pearl to update
	 * @param weakenAmount The amount to increase the strength by
	 */
	public void increaseSealStrength(PrisonPearl pp, int amount) {
		this.setSealStrength(pp, pp.getSealStrength() + amount);
	}
	
	
	/**
	 * Gets a pearl by ID
	 * @param id The ID to match
	 * @return The prison pearl instance if it exists
	 */
	public PrisonPearl getById(UUID id) {
		return pearls.get(id);
	}
	
	
	/**
	 * Gets a pearl by an item stack
	 * @param is
	 */
	public PrisonPearl getPearlByItem(ItemStack is) {
		UUID id = PrisonPearl.getIDFromItemStack(is);
		if (id != null) {
			return pearls.get(id);
		}
		
		return null;
	}
	
	
	/**
	 * Gets whether a player is imprisoned
	 * @param p The player to check
	 * @return true if the player is imprisoned
	 */
	public boolean isImprisoned(Player p) {
		return pearls.get(p.getUniqueId()) != null;
	}
	
	
	/**
	 * Gets whether a player is imprisoned
	 * @param p The player to check
	 * @return true if the player is imprisoned and in prison
	 */
	public boolean isImprisonedInPrison(Player p) {
		PrisonPearl pp =  pearls.get(p.getUniqueId());
		if (pp != null) {
			return !pp.getSummoned();
		}
		
		return false;
	}
	
	
	/**
	 * Gets whether a player is summoned
	 * @param p The player to check
	 * @return true if the player is imprisoned
	 */
	public boolean isSummoned(Player p) {
		PrisonPearl pp =  pearls.get(p.getUniqueId());
		if (pp != null) {
			return pp.getSummoned();
		}
		
		return false;
	}
	
	
	/**
	 * Gets whether a player is imprisoned
	 * @param p The player to check
	 * @return true if the player is imprisoned
	 */
	public boolean isImprisoned(SabrePlayer p) {
		return pearls.get(p.getID()) != null;
	}
	
	
	/**
	 * Gets the free world
	 * @return The free world
	 */
	public World getFreeWorld() {
		return Bukkit.getWorld(config.getFreeWorldName());
	}
	
	
	/**
	 * Gets the prison world
	 * @return The prison world
	 */
	public World getPrisonWorld() {
		return Bukkit.getWorld(config.getPrisonWorldName());
	}
	
	
	/**
	 * Summons a pearl
	 * @param pp The pearl to summon
	 * @param p The player
	 * @return true if the player is summoned
	 */
	public boolean summonPearl(PrisonPearl pp, SabrePlayer p) {
		if (pp.getSummoned()) {
			p.msg(Lang.pearlAlreadySummoned, pp.getName());
			return false;
		}
		
		this.setPearlSummoned(pp, true);
		this.setReturnLocation(pp, pp.getPlayer().getPlayer().getLocation().add(0, -.5, 0));
		
		if (!summonEvent(pp, SummonEvent.Type.SUMMONED, p.getPlayer().getLocation())) {
			this.setPearlSummoned(pp, false);
			return false;
		}
		
		p.msg(Lang.pearlYouSummoned, pp.getName());
		return true;
	}
	
	
	/**
	 * Returns a pearl
	 * @param pp The prison pearl
	 * @param p The player
	 * @return true if the player was returned
	 */
	public boolean returnPearl(PrisonPearl pp, SabrePlayer p) {
		if (!pp.getSummoned()) {
			p.msg(Lang.pearlNotSummoned, pp.getName());
			return false;
		}

		this.setPearlSummoned(pp, false);
		
		if (!summonEvent(pp, SummonEvent.Type.RETURNED, pp.getReturnLocation())) {
			this.setPearlSummoned(pp, true);
			return false;
		}
		
		p.msg(Lang.pearlYouReturned, pp.getName());
		return true;
	}
	
	
	/**
	 * Kills a pearled player
	 * @param pp The prison pearl
	 * @param p The player
	 * @return true if the player was kill
	 */
	public boolean killPearl(PrisonPearl pp, SabrePlayer p) {

		this.setPearlSummoned(pp, false);
		
		if (!summonEvent(pp, SummonEvent.Type.KILLED, pp.getLocation())) {
			this.setPearlSummoned(pp, true);
			return false;
		}
		
		p.msg(Lang.pearlYouKilled, pp.getName());
		return true;
	}
	
    private boolean summonEvent(PrisonPearl pp, SummonEvent.Type type, Location loc) {
		SummonEvent event = new SummonEvent(pp, type, loc);
		Bukkit.getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
    

	/**
	 * Gets a list of pearls located in an inventory
	 * @param inv The inventory to search
	 * @return The list of contained pearls
	 */
	public List<PrisonPearl> getInventoryPrisonPearls(Inventory inv) {
		List<PrisonPearl> pearls = new ArrayList<PrisonPearl>();
		
		for (ItemStack is : inv.all(Material.ENDER_PEARL).values()) {
			if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
				UUID id = PrisonPearl.getIDFromItemStack(is);
				if (id != null) {
					PrisonPearl pp = this.getById(id);
					if (pp != null) {
						pearls.add(pp);
					}
				}
			}
		}
		
		return pearls;
	}
	
	
	/**
	 * Gets the prison pearl stacks located in an inventory
	 * @param inv The inventory to search
	 * @return The list of contained pearls
	 */
	public List<ItemStack> getInventoryPearlStacks(Inventory inv) {
		List<ItemStack> pearls = new ArrayList<ItemStack>();
		
		for (ItemStack is : inv.all(Material.ENDER_PEARL).values()) {
			if (is.hasItemMeta() && is.getItemMeta().hasLore()) {
				pearls.add(is);
			}
		}
		
		return pearls;
	}
}
