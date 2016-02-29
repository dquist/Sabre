package com.gordonfreemanq.sabre.snitch;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SabreBlock;

public class SnitchListener implements Listener {

	
	private final PlayerManager pm;
	private final BlockManager bm;
    private final SnitchLogger snitchLogger;
	private final SnitchCollection snitches;
    private final TreeMap<String, Set<Snitch>> playersInSnitches;
	
	public SnitchListener(SnitchLogger snitchLogger) {
		this.pm = PlayerManager.getInstance();
		this.bm = BlockManager.getInstance();
		this.snitchLogger = snitchLogger;
		this.snitches = bm.getSnitches();
		
		this.playersInSnitches = new TreeMap<String, Set<Snitch>>();
	}
	
	/**
	 * Handles player login events
	 * @param e the event args
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerJoinEvent(PlayerJoinEvent e) {
		
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
		Location l = sp.getPlayer().getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
        
		String playerName = sp.getName();
		Set<Snitch> inList = new TreeSet<Snitch>();
		playersInSnitches.put(playerName, inList);
		
		Reinforcement r;
        Location snitchL;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null && !inList.contains(snitch)) {
        		inList.add(snitch);
        		
        		// Alert the players in the group
        		if  (!snitch.canPlayerAccess(sp)) {
        			snitchL = snitch.getLocation();
        			if (snitch.getNotify()) {
                		r.getGroup().msgAllSnitch(Lang.snitchLoggedIn, playerName, snitch.getSnitchName(), 
                				snitchL.getBlockX(), snitchL.getBlockY(), snitchL.getBlockZ(), snitchL.getWorld().getName());
        			}
            		snitchLogger.logLogin(snitch, sp, l);
        		}
        	}
        }
	}
	
	
	/**
	 * Handles player logging out of snitches
	 * @param event
	 */
	public void handlePlayerExit(PlayerEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
		Location l = e.getPlayer().getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
		String playerName = sp.getName();
		playersInSnitches.remove(playerName);

		Set<Snitch> inList = new TreeSet<Snitch>();
		playersInSnitches.put(playerName, inList);
		
		Reinforcement r;
        Location snitchL;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null && !inList.contains(snitch)) {
        		inList.add(snitch);
        		
        		// Alert the players in the group
        		if  (!snitch.canPlayerAccess(sp)) {
        			snitchL = snitch.getLocation();
        			if (snitch.getNotify()) {
        				r.getGroup().msgAllSnitch(Lang.snitchLoggedOut, playerName, snitch.getSnitchName(), 
            				snitchL.getBlockX(), snitchL.getBlockY(), snitchL.getBlockZ(), snitchL.getWorld().getName());
        			}
            		snitchLogger.logLogout(snitch, sp, l);
        		}
        	}
        }
    }
	
	
	/**
	 * Handles player kick event
	 * @param e The event args
	 */
	@EventHandler(ignoreCancelled = true)
    public void playerKickEvent(PlayerKickEvent e) {
		handlePlayerExit(e);
	}

	
	/**
	 * Handles player quit event
	 * @param e The event args
	 */
    @EventHandler(ignoreCancelled = true)
    public void playerQuitEvent(PlayerQuitEvent e) {
		handlePlayerExit(e);
	}
	
	
	/**
	 * Handles player entry into snitch fields
	 * @param e The event args
	 */
    @EventHandler(priority = EventPriority.HIGH)
    public void enterSnitchProximity(PlayerMoveEvent e) {
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()
                && from.getWorld().equals(to.getWorld())) {
            // Player didn't move by at least one block.
            return;
        }
        
        Player p = e.getPlayer();
        SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
        Location l = p.getLocation();
        String playerName = sp.getName();
        
        // Ignore vanished players
 		if (sp.getVanished()) {
 			return;
 		}
        

        Set<Snitch> inList = playersInSnitches.get(playerName);
        if (inList == null) {
            inList = new TreeSet<Snitch>();
            playersInSnitches.put(playerName, inList);
        }
        
        Reinforcement r;
        Location snitchL;
        
        // Log entries
        Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null && !inList.contains(snitch)) {
        		inList.add(snitch);
        		
        		// Alert the players in the group
        		if  (!snitch.canPlayerAccess(sp)) {
        			snitchL = snitch.getLocation();
        			if (snitch.getNotify()) {
	            		r.getGroup().msgAllSnitch(Lang.snitchEntry, playerName, snitch.getSnitchName(), 
	            				snitchL.getBlockX(), snitchL.getBlockY(), snitchL.getBlockZ(), snitchL.getWorld().getName());
        			}
            		snitchLogger.logEntry(snitch, sp, l);
        		}
        	}
        }
        
        
        // This removes players from the field once they move out of the hysteresis zone
        set = snitches.findSnitches(l, true);
        Set<Snitch> rmList = new TreeSet<Snitch>();
        for (Snitch snitch : inList) {
            if (set.contains(snitch)) {
                continue;
            }
            rmList.add(snitch);
        }
        inList.removeAll(rmList);
    }

    
    /**
     * Handles item use event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
		Location l = sp.getPlayer().getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
		Block block;
        if (e.getInventory().getHolder() instanceof Chest) {
            Chest chest = (Chest) e.getInventory().getHolder();
            block = chest.getBlock();
        } else if (e.getInventory().getHolder() instanceof DoubleChest) {
            DoubleChest chest = (DoubleChest) e.getInventory().getHolder();
            block = chest.getLocation().getBlock();
        } else {
        	return;
        }

        Reinforcement r;
        
        Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(sp)) {
            		snitchLogger.logUsed(snitch, sp, block);
        		}
        	}
        }
    }
    
    
    /**
     * Handles player kill event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerKillPlayer(PlayerDeathEvent e) {
    	
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        
        // Need to get by name here since CombatTag entity doesn't keep UUID
		SabrePlayer killed = pm.getPlayerByName(e.getEntity().getName());
		String killerName = e.getEntity().getKiller().getName();
		SabrePlayer killer = pm.getPlayerByName(killerName);

		// Ignore vanished players
		if (killer.getVanished()) {
			return;
		}

        Reinforcement r;
		
		Set<Snitch> set = snitches.findSnitches(e.getEntity().getLocation());
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(killer)) {
            		snitchLogger.logPlayerKill(snitch, killer, killed);
        		}
        	}
        }
    }
    
    
    /**
     * Handles the block break event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void playerBreakBlock(BlockBreakEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
        Block block = e.getBlock();
		Location l = e.getBlock().getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
        Reinforcement r;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(sp)) {
            		snitchLogger.logBlockBreak(snitch, sp, block);
        		}
        	}
        }
    }
    
    
    /**
     * Handles the block place event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerPlaceBlock(BlockPlaceEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
        Block block = e.getBlock();
		Location l = e.getBlock().getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
        Reinforcement r;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(sp)) {
            		snitchLogger.logBlockPlace(snitch, sp, block);
        		}
        	}
        }
    }
    
    
    /**
     * Handles the bucket fill event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerFillBucket(PlayerBucketFillEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
        Block block = e.getBlockClicked();
		Location l = block.getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
        Reinforcement r;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(sp)) {
            		snitchLogger.logBucketFill(snitch, sp, block);
        		}
        	}
        }
    }
    
    
    /**
     * Handles the bucket fill event
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerEmptyBucket(PlayerBucketEmptyEvent e) {
		SabrePlayer sp = pm.getPlayerById(e.getPlayer().getUniqueId());
        Block block = e.getBlockClicked();
		Location l = block.getLocation();
		
		// Ignore vanished players
		if (sp.getVanished()) {
			return;
		}
		
        Reinforcement r;
		
		Set<Snitch> set = snitches.findSnitches(l);
        for (Snitch snitch : set) {
        	r = snitch.getReinforcement();
        	
        	if (r != null) {
        		
        		if  (!snitch.canPlayerAccess(sp)) {
            		snitchLogger.logBucketEmpty(snitch, sp, l, sp.getPlayer().getItemInHand());
        		}
        	}
        }
    }
    
    
    /**
     * Handles placing a snitch and setting who placed it
     * @param e The event args
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSnitchPlace(BlockPlaceEvent e) {
    	SabreBlock sb = bm.getBlockAt(e.getBlock().getLocation());
    	if (sb == null) {
    		return;
    	}
    	
    	if (sb.isSpecial() && sb instanceof Snitch) {
    		Snitch snitch = (Snitch)sb;
    		snitch.setPlacedBy(e.getPlayer().getUniqueId());
    	}
    }
}
