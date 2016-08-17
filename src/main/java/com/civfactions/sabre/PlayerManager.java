package com.civfactions.sabre;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
	 * @param player The player to remove
	 */
	public void removePlayer(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		assertValidPlayer(player);
		
		onlinePlayers.remove(player.getID());
		players.remove(player.getID());
		db.playerDelete(player);
		plugin.logger().log(Level.INFO, "Removed player: Name=%s, ID=%s", player.getName(), player.getID().toString());
	}
	
	
	/**
	 * Gets a SabrePlayer instance by ID
	 * @param id The ID of the player
	 * @return The player instance if it exists
	 */
	public IPlayer getPlayerById(UUID uid) {
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
	public IPlayer getPlayerByName(String name) {
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
	public IPlayer createNewPlayer(Player player) {
		Guard.ArgumentNotNull(player, "player");
		
		String originalName = player.getName();
		String name = originalName;
		IPlayer sp = getPlayerByName(originalName);
		
		// If there is a conflict, add numbers to the end to get a name
		// that hasn't been used before
		Integer i = 0;
		while (sp != null) {
			name = originalName + i.toString();
			sp = getPlayerByName(name);
		}
		
		// Now we should have a unique name for the new player
		SabrePlayer sPlayer = new SabrePlayer(plugin, player.getUniqueId(), name);
		sPlayer.setPlayer(player);
		sPlayer.setFirstLogin(new Date());
		sPlayer.setName(name);
		players.put(sPlayer.getID(), sPlayer);
		db.playerInsert(sPlayer);
		plugin.logger().log(Level.INFO, "Created new player %s with ID %s", name, sPlayer.getID());
		return sPlayer;
	}
	
	
	/**
	 * Handles a player connecting
	 * @param player The player instance
	 */
	public void onPlayerConnect(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		this.onlinePlayers.put(sp.getID(), sp);
	}
	
	
	/**
	 * Handles a player disconnecting
	 * @param player The player instance
	 */
	public void onPlayerDisconnect(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		this.onlinePlayers.remove(sp.getID());
	}
	
	
	/**
	 * Sets the last login time
	 * @param player The player to update
	 * @param lastLogin The first login time
	 */
	public void setLastLogin(IPlayer player, Date lastLogin) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(lastLogin, "lastLogin");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setLastLogin(lastLogin);
		db.playerUpdateLastLogin(sp);
	}
	
	
	/**
	 * Sets the auto-join status
	 * @param player The player to update
	 * @param autoJoin The new status
	 */
	public void setAutoJoin(IPlayer player, boolean autoJoin) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setAutoJoin(autoJoin);
		db.playerUpdateAutoJoin(sp);
	}
	
	
	/**
	 * Sets the player's faction
	 * @param player The player to update
	 * @param faction The new faction
	 */
	public void setFaction(IPlayer player, SabreFaction faction) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(faction, "faction");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setFaction(faction);
		db.playerUpdateFaction(sp);
	}
	
	
	/**
	 * Sets the player's display name
	 * @param player The player to update
	 * @param name The new player name
	 */
	public void setDisplayName(IPlayer player, String name) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setName(name);
		db.playerUpdateName(sp);
	}
	
	
	/**
	 * Sets the player's ban status
	 * @param player The player to update
	 * @param banned The player's ban status
	 * @param reason The ban reason
	 */
	public void setBanStatus(IPlayer player, boolean banned, String reason) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setBanned(banned);
		sp.setBanMessage(reason);
		
		if (banned && player.isOnline()) {
			String fullBanMessage = String.format("%s\n%s", Lang.youAreBanned, reason);
			player.getPlayer().kickPlayer(fullBanMessage);
		}
		
		db.playerUpdateBan(sp);
	}
	
	
	/**
	 * Updates the freed offline status
	 * @param player The player to update
	 * @param status The freed offline status
	 */
	public void setFreedOffline(IPlayer player, boolean status) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setFreedOffline(status);
		db.playerUpdateFreedOffline(sp);
	}
	
	/**
	 * Updates the bed location
	 * @param player The player to update
	 * @param status The freed offline status
	 */
	public void setBedLocation(IPlayer player, Location l) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.setBedLocation(l);
		db.playerUpdateBed(sp);
	}
	
	
	/**
	 * Adds an offline message for the player
	 * @param player The player to update
	 * @param message The message to add
	 */
	public void addOfflineMessage(IPlayer player, String message) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNullOrEmpty(message, "message");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.addOfflineMessage(message);
		db.playerAddOfflineMessage(sp, message);
	}
	
	
	/**
	 * Clears the offline messages for a player
	 * @param player The player to update
	 */
	public void clearOfflineMessages(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.getOfflineMessages().clear();
		db.playerClearOfflineMessages(sp);
	}
	
	
	/**
	 * Print offline messages
	 * @param player The player to message
	 */
	public void printOfflineMessages(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		assertValidPlayer(player);
		
		if (player.isOnline()) {
			
			// Print the offline messages for the player if there are any
			Collection<String> messages = player.getOfflineMessages();
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
	public void addPlayTime(IPlayer player, long time) {
		Guard.ArgumentNotNull(player, "player");
		SabrePlayer sp = assertValidPlayer(player);
		
		sp.addPlayTime(time);
		db.playerUpdatePlayTime(sp);
	}
	
	/**
	 * Validates that a player instance is registered in the manager
	 * @param p The player to validate
	 * @return The matching SabrePlayer instance
	 */
	private SabrePlayer assertValidPlayer(IPlayer p) {
		UUID uid = p.getID();
		
		// Check online players first
		SabrePlayer player = onlinePlayers.get(uid);
		
		// Then check all players
		if (player == null) {
			player = players.get(uid);
		}
		
		if (player == null) {
			throw new RuntimeException(String.format("Tried to access invalid player '%s' with ID %s", p.getName(), p.getID()));
		}
		
		return player;
	}

	public Collection<IPlayer> getPlayers() {
		Set<IPlayer> players = this.players.values().stream().map(p -> (IPlayer)p).collect(Collectors.toSet());
		return Collections.unmodifiableSet(players);
	}


	public Collection<IPlayer> getOnlinePlayers() {
		Set<IPlayer> onlinePlayers = this.onlinePlayers.values().stream().map(p -> (IPlayer)p).collect(Collectors.toSet());
		return Collections.unmodifiableSet(onlinePlayers);
	}
}
