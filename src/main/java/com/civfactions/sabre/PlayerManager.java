package com.civfactions.sabre;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.civfactions.sabre.data.IDataAccess;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.util.Guard;

/**
 * Class for managing all the player records
 * @author GFQ
 */
public class PlayerManager {
	
	private final SabrePlugin plugin;
	private final IDataAccess db;
	
	private final HashMap<UUID, SabrePlayer> players;
	private final HashMap<UUID, SabrePlayer> onlinePlayers;

	
	/**
	 * Creates a new PlayerManager instance 
	 */
	public PlayerManager(SabrePlugin plugin, IDataAccess db) {
		Guard.ArgumentNotNull(plugin, "plugin");
		Guard.ArgumentNotNull(db, "db");
		
		this.plugin = plugin;
		this.db = db;
		
		this.players = new HashMap<UUID, SabrePlayer>();
		this.onlinePlayers = new HashMap<UUID, SabrePlayer>();
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
	 * Removes a player from all records
	 * @param player
	 */
	public void removePlayer(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		onlinePlayers.remove(player.getID());
		players.remove(player.getID());
		db.playerDelete(player);
		SabrePlugin.log(Level.INFO, "Removed player: Name=%s, ID=%s", player.getName(), player.getID().toString());
	}
	
	
	/**
	 * Gets a SabrePlayer instance by ID
	 * @param id The ID of the player
	 * @return The player instance if it exists
	 */
	public SabrePlayer getPlayerById(UUID uid) {
		Guard.ArgumentNotNull(uid, "uid");
		
		// Check online players first
		SabrePlayer p = onlinePlayers.get(uid);
		if (p == null) {
			p = players.get(uid);
		}
		return p;
	}
	
	
	/**
	 * Gets a SabrePlayer instance by name
	 * @param id The name of the player
	 * @return The player instance if it exists
	 */
	public SabrePlayer getPlayerByName(String name) {
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
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
	 * @param player The bukkit player
	 * @return The new SabrePlayer instance
	 */
	public SabrePlayer createNewPlayer(Player player) {
		Guard.ArgumentNotNull(player, "player");
		
		String originalName = player.getName();
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
		sp = new SabrePlayer(plugin, player.getUniqueId(), name);
		sp.setPlayer(player);
		sp.setFirstLogin(new Date());
		sp.setName(name);
		players.put(sp.getID(), sp);
		db.playerInsert(sp);
		SabrePlugin.log(Level.INFO, "Created new player %s with ID %s", name, sp.getID());
		return sp;
	}
	
	
	/**
	 * Handles a player connecting
	 * @param player The player instance
	 */
	public void onPlayerConnect(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		this.onlinePlayers.put(player.getID(), player);
	}
	
	
	/**
	 * Handles a player disconnecting
	 * @param player The player instance
	 */
	public void onPlayerDisconnect(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		this.onlinePlayers.remove(player.getID());
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
	 * @param player The player to update
	 * @param lastLogin The first login time
	 */
	public void setLastLogin(SabrePlayer player, Date lastLogin) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(lastLogin, "lastLogin");
		
		
		player.setLastLogin(lastLogin);
		db.playerUpdateLastLogin(player);
	}
	
	
	/**
	 * Sets the auto-join status
	 * @param player The player to update
	 * @param autoJoin The new status
	 */
	public void setAutoJoin(SabrePlayer player, boolean autoJoin) {
		Guard.ArgumentNotNull(player, "player");
		
		player.setAutoJoin(autoJoin);
		db.playerUpdateAutoJoin(player);
	}
	
	
	/**
	 * Sets the player's faction
	 * @param player The player to update
	 * @param faction The new faction
	 */
	public void setFaction(SabrePlayer player, SabreFaction faction) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(faction, "faction");
		
		
		player.setFaction(faction);
		db.playerUpdateFaction(player);
	}
	
	
	/**
	 * Sets the player's display name
	 * @param player The player to update
	 * @param name The new player name
	 */
	public void setDisplayName(SabrePlayer player, String name) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
		
		player.setName(name);
		db.playerUpdateName(player);
	}
	
	
	/**
	 * Sets the player's ban status
	 * @param player The player to update
	 * @param banned The player's ban status
	 * @param reason The ban reason
	 */
	public void setBanStatus(SabrePlayer player, boolean banned, String reason) {
		Guard.ArgumentNotNull(player, "player");
		
		player.setBanned(banned);
		player.setBanMessage(reason);
		
		if (banned && player.isOnline()) {
			String fullBanMessage = String.format("%s\n%s", Lang.youAreBanned, reason);
			player.getPlayer().kickPlayer(fullBanMessage);
		}
		
		db.playerUpdateBan(player);
	}
	
	
	/**
	 * Updates the freed offline status
	 * @param player The player to update
	 * @param status The freed offline status
	 */
	public void setFreedOffline(SabrePlayer player, boolean status) {
		Guard.ArgumentNotNull(player, "player");
		
		player.setFreedOffline(status);
		db.playerUpdateFreedOffline(player);
	}
	
	/**
	 * Updates the bed location
	 * @param player The player to update
	 * @param status The freed offline status
	 */
	public void setBedLocation(SabrePlayer player, Location l) {
		Guard.ArgumentNotNull(player, "player");
		
		player.setBedLocation(l);
		db.playerUpdateBed(player);
	}
	
	
	/**
	 * Adds an offline message for the player
	 * @param player The player to update
	 * @param message The message to add
	 */
	public void addOfflineMessage(SabrePlayer player, String message) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNullOrEmpty(message, "message");
		
		player.addOfflineMessage(message);
		db.playerAddOfflineMessage(player, message);
	}
	
	
	/**
	 * Clears the offline messages for a player
	 * @param player The player to update
	 */
	public void clearOfflineMessages(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		player.getOfflineMessages().clear();
		db.playerClearOfflineMessages(player);
	}
	
	
	/**
	 * Print offline messages
	 * @param player The player to message
	 */
	public void printOfflineMessages(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		if (player.isOnline()) {
			
			// Print the offline messages for the player if there are any
			List<String> messages = player.getOfflineMessages();
			if (messages.size() > 0) {
				player.msg(Lang.chatOfflineActivity);
				for (String msg : player.getOfflineMessages()) {
					player.msg(msg);
				}
			}
		}
	}
	
	
	/**
	 * Adds playtime to the player
	 * @param player The player to update
	 * @param time The time to add
	 */
	public void addPlayTime(SabrePlayer player, long time) {
		Guard.ArgumentNotNull(player, "player");
		
		player.addPlayTime(time);
		db.playerUpdatePlayTime(player);
	}
}
