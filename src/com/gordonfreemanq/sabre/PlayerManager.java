package com.gordonfreemanq.sabre;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.gordonfreemanq.sabre.core.ISabreLog;
import com.gordonfreemanq.sabre.data.IDataAccess;


public class PlayerManager {

	private final IDataAccess db;
	private final ISabreLog logger;
	private final HashMap<UUID, SabrePlayer> players;
	private final HashMap<UUID, SabrePlayer> onlinePlayers;
	
	private static PlayerManager instance;
	
	public static PlayerManager getInstance() {
		return instance;
	}
	
	/**
	 * Constructor
	 * @param db The data access object
	 * @param logger The logger
	 */
	public PlayerManager(IDataAccess db, ISabreLog logger) {
		this.db = db;
		this.logger = logger;
		this.players = new HashMap<UUID, SabrePlayer>();
		this.onlinePlayers = new HashMap<UUID, SabrePlayer>();
		
		instance = this;
	}
	
	
	/**
	 * Loads all the player data from the database
	 */
	public void load() {
		this.players.clear();
		
		for (SabrePlayer p : db.playerGetAll()) {
			this.players.put(p.getID(), p);
		}
	}
	
	
	/**
	 * Adds a new player to the player manager
	 * @param player The new player to add
	 */
	public void addPlayer(SabrePlayer player) {
		this.players.put(player.getID(), player);
		db.playerInsert(player);
	}
	
	
	/**
	 * Removes a player from all records
	 * @param p
	 */
	public void removePlayer(SabrePlayer p) {
		onlinePlayers.remove(p.getID());
		players.remove(p.getID());
		db.playerDelete(p);
		logger.log(Level.INFO, "Removed player: Name=%s, ID=%s", p.getName(), p.getID().toString());
	}
	
	
	/**
	 * Gets a SabrePlayer instance by ID
	 * @param id The ID of the player
	 * @return The player instance if it exists
	 */
	public SabrePlayer getPlayerById(UUID id) {
		// Check online players first
		SabrePlayer p = onlinePlayers.get(id);
		if (p == null) {
			p = players.get(id);
		}
		return p;
	}
	
	
	/**
	 * Gets a SabrePlayer instance by name
	 * @param id The name of the player
	 * @return The player instance if it exists
	 */
	public SabrePlayer getPlayerByName(String name) {
		// Check online players first
		for (SabrePlayer p : onlinePlayers.values()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		
		// Then check all players
		for (SabrePlayer p : players.values()) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Creates a new player instance
	 * @param p The bukkit player
	 * @return The new SabrePlayer instance
	 */
	public SabrePlayer createNewPlayer(Player p) {
		
		String originalName = p.getName();
		String name = originalName;
		SabrePlayer sp = getPlayerByName(originalName);
		
		// If there is a conflict, add numbers to the end to get a name
		// that hasn't been used before
		Integer i = 0;
		while (sp != null) {
			name = originalName + i.toString();
			sp = getPlayerByName(name);
		}
		
		// Now we should have a unique name for the new player
		sp = new SabrePlayer(p.getUniqueId(), name);
		sp.setFirstLogin(new Date());
		sp.setName(name);
		this.addPlayer(sp);
		logger.log(Level.INFO, "Created new player %s with ID %s", name, sp.getID());
		return sp;
	}
	
	
	/**
	 * Handles a player connecting
	 * @param p The player instance
	 */
	public void onPlayerConnect(SabrePlayer p) {
		this.onlinePlayers.put(p.getID(), p);
	}
	
	
	/**
	 * Handles a player disconnecting
	 * @param p The player instance
	 */
	public void onPlayerDisconnect(SabrePlayer p) {
		this.onlinePlayers.remove(p.getID());
	}
	
	
	/**
	 * Gets the collection of all players ever
	 * @return All the players
	 */
	public Collection<SabrePlayer> getPlayers() {
		return this.players.values();
	}
	
	
	/**
	 * Gets the collection of online players
	 * @return The online players
	 */
	public Collection<SabrePlayer> getOnlinePlayers() {
		return this.onlinePlayers.values();
	}
	
	
	/**
	 * Sets the last login time
	 * @param p The player to update
	 * @param lastLogin The first login time
	 */
	public void setLastLogin(SabrePlayer p, Date lastLogin) {
		p.setLastLogin(lastLogin);
		db.playerUpdateLastLogin(p);
	}
	
	
	/**
	 * Sets the auto-join status
	 * @param p The player to update
	 * @param autoJoin The new status
	 */
	public void setAutoJoin(SabrePlayer p, boolean autoJoin) {
		p.setAutoJoin(autoJoin);
		db.playerUpdateAutoJoin(p);
	}
	
	
	/**
	 * Sets the player's displayname
	 * @param p The player to update
	 * @param name The new player name
	 */
	public void setDisplayName(SabrePlayer p, String name) {
		p.setName(name);
		db.playerUpdateName(p);
	}
	
	
	/**
	 * Sets the player's ban status
	 * @param p The player to update
	 * @param banned The player's ban status
	 * @param reason The ban reason
	 */
	public void setBanStatus(SabrePlayer p, boolean banned, String reason) {
		p.setBanned(banned);
		p.setBanMessage(reason);
		
		if (banned && p.isOnline()) {
			String fullBanMessage = String.format("%s\n%s", Lang.youAreBanned, reason);
			p.getPlayer().kickPlayer(fullBanMessage);
		}
		
		db.playerUpdateBan(p);
	}
	
	
	/**
	 * Updates the freed offline status
	 * @param p The player to update
	 * @param status The freed offline status
	 */
	public void setFreedOffline(SabrePlayer p, boolean status) {
		p.setFreedOffline(status);
		db.playerUpdateFreedOffline(p);
	}
	
	/**
	 * Updates the bed location
	 * @param p The player to update
	 * @param status The freed offline status
	 */
	public void setBedLocation(SabrePlayer p, Location l) {
		p.setBedLocation(l);
		db.playerUpdateBed(p);
	}
	
	
	/**
	 * Adds an offline message for the player
	 * @param p The player to update
	 * @param message The message to add
	 */
	public void addOfflineMessage(SabrePlayer p, String message) {
		p.addOfflineMessage(message);
		db.playerAddOfflineMessage(p, message);
	}
	
	
	/**
	 * Clears the offline messages for a player
	 * @param p The player to update
	 */
	public void clearOfflineMessages(SabrePlayer p) {
		p.getOfflineMessages().clear();
		db.playerClearOfflineMessages(p);
	}
	
	
	/**
	 * Print offline messages
	 * @param p The player to message
	 */
	public void printOfflineMessages(SabrePlayer p) {
		if (p.isOnline()) {
			
			// Print the offline messages for the player if there are any
			List<String> messages = p.getOfflineMessages();
			if (messages.size() > 0) {
				p.msg(Lang.chatOfflineActivity);
				for (String msg : p.getOfflineMessages()) {
					p.msg(msg);
				}
			}
		}
	}
	
	
	/**
	 * Adds playtime to the player
	 * @param p The player to update
	 * @param time The time to add
	 */
	public void addPlayTime(SabrePlayer p, long time) {
		p.addPlayTime(time);
		db.playerUpdatePlayTime(p);
	}
}
