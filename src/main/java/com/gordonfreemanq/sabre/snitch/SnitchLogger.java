package com.gordonfreemanq.sabre.snitch;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;

public class SnitchLogger {
	
	private final SabrePlugin plugin;
	
	public SnitchLogger(SabrePlugin plugin) {
		this.plugin = plugin;
	}

	/**
     * logs a message that someone killed an entity
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that did the killing
     * @param entity - the entity that died
     */
    public void logEntityKill(Snitch snitch, SabrePlayer player, Entity entity) {
		this.logInfo(snitch, player.getID(), SnitchAction.KILL, new Date(), null, null, null, entity.getType().toString());
    }

    /**
     * Logs a message that someone killed another player
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that did the killing
     * @param victim - the player that died
     */
    public void logPlayerKill(Snitch snitch, SabrePlayer player, SabrePlayer victim) {
		this.logInfo(snitch, player.getID(), SnitchAction.KILL, new Date(), null, null, victim.getID(), null);
    }

    /**
     * Logs a message that someone ignited a block within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that did the ignition
     * @param block - the block that was ignited
     */
    public void logIgnite(Snitch snitch, SabrePlayer player, Block block) {
		this.logInfo(snitch, player.getID(), SnitchAction.IGNITED, new Date(), block.getLocation(), block.getType(), null, null);
    }

    /**
     * Logs a message that someone entered the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that entered the snitch's field
     * @param loc - the location of where the player entered
     */
    public void logEntry(Snitch snitch, SabrePlayer player, Location loc) {
		this.logInfo(snitch, player.getID(), SnitchAction.ENTRY, new Date(), loc, null, null, null);
    }

	/**
	 * Logs a message that someone logged in in the snitch's field
	 *
	 * @param snitch - the snitch that recorded this event
	 * @param player - the player that logged in in the snitch's field
	 * @param loc - the location of where the player logged in at
	 */
	public void logLogin(Snitch snitch, SabrePlayer player, Location loc) {
		this.logInfo(snitch, player.getID(), SnitchAction.LOGIN, new Date(), loc, null, null, null);
	}

	/**
	 * Logs a message that someone logged out in the snitch's field
	 *
	 * @param snitch - the snitch that recorded this event
	 * @param player - the player that logged out in the snitch's field
	 * @param loc - the location of where the player logged out at
	 */
	public void logLogout(Snitch snitch, SabrePlayer player, Location loc) {
		this.logInfo(snitch, player.getID(), SnitchAction.LOGOUT, new Date(), loc, null, null, null);
		
	}

    /**
     * Logs a message that someone broke a block within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that broke the block
     * @param block - the block that was broken
     */
    public void logBlockBreak(Snitch snitch, SabrePlayer player, Block block) {
		this.logInfo(snitch, player.getID(), SnitchAction.BLOCK_BREAK, new Date(), block.getLocation(), block.getType(), null, null);
    	
    }

    /**
     * Logs a message that someone placed a block within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that placed the block
     * @param block - the block that was placed
     */
    public void logBlockPlace(Snitch snitch, SabrePlayer player, Block block) {
		this.logInfo(snitch, player.getID(), SnitchAction.BLOCK_PLACE, new Date(), block.getLocation(), block.getType(), null, null);
    }

    /**
     * Logs a message that someone emptied a bucket within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that emptied the bucket
     * @param loc - the location of where the bucket empty occurred
     * @param item - the ItemStack representing the bucket that the player
     * emptied
     */
    public void logBucketEmpty(Snitch snitch, SabrePlayer player, Location loc, ItemStack item) {
		this.logInfo(snitch, player.getID(), SnitchAction.BUCKET_EMPTY, new Date(), loc, item.getType(), null, null);
    }

    /**
     * Logs a message that someone filled a bucket within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that filled the bucket
     * @param block - the block that was 'put into' the bucket
     */
    public void logBucketFill(Snitch snitch, SabrePlayer player, Block block) {
		this.logInfo(snitch, player.getID(), SnitchAction.BUCKET_FILL, new Date(), block.getLocation(), block.getType(), null, null);
    }
	
	/**
     * Logs a message that someone used a block within the snitch's field
     *
     * @param snitch - the snitch that recorded this event
     * @param player - the player that used something
     * @param block - the block that was used
     */
	public void logUsed(Snitch snitch, SabrePlayer player, Block block) {
		this.logInfo(snitch, player.getID(), SnitchAction.USED, new Date(), block.getLocation(), block.getType(), null, null);
    }
	
	
	/**
	 * Logs snitch info
	 * @param snitch The snitch
	 * @param player The player
	 * @param action The action
	 * @param date The date
	 * @param loc The location
	 * @param material The material
	 * @param victim The victim
	 * @param entity The entity
	 */
	public void logInfo(Snitch snitch, UUID player, SnitchAction action, Date date, Location loc, Material material, UUID victim, String entity) {
		
		SnitchLogEntry entry = new SnitchLogEntry(snitch.getID(), player, action, date);

		entry.loc = loc;
		entry.material = material;
		entry.victim = victim;
		entry.entity = entity;
		
		plugin.getDataAccess().snitchMakeLog(entry);
	}
	
	
	/**
	 * Clears all entries for a snitch
	 * @param snitch The snitch to clear
	 */
	public void clearEntries(Snitch snitch) {
		plugin.getDataAccess().snitchClear(snitch);
	}
	
	
	public void requestReport(SabrePlayer p, Snitch snitch, int page) {
		ReportReqeust request = new ReportReqeust(plugin.getDataAccess(), plugin.getPlayerManager(), p, snitch, page);
		Bukkit.getScheduler().runTaskAsynchronously(SabrePlugin.instance(), request);
	}
	
}
