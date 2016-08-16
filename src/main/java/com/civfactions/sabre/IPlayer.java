package com.civfactions.sabre;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.civfactions.sabre.blocks.BuildState;
import com.civfactions.sabre.chat.IChatChannel;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.util.INamed;

public interface IPlayer extends INamed, IChatChannel {

	/**
	 * Gets the player ID
	 * @return The player ID
	 */
	UUID getID();
	
	/**
	 * Gets the Bukkit player instance
	 * @return The Bukkit player instance
	 */
	Player getPlayer();
	
	/**
	 * Sets the player instance
	 * @param player The player instance
	 */
	void setPlayer(Player player);
	
	/**
	 * Gets the player's faction
	 * @return The player's faction
	 */
	SabreFaction getFaction();
	
	/**
	 * Gets whether the player is online
	 * @return true if the player is online
	 */
	boolean isOnline();
	
	/**
	 * Gets the first login time
	 * @return The first login time
	 */
	public Date getFirstLogin();
	
	/**
	 * Gets the last login time
	 * @return The first login time
	 */
	 Date getLastLogin();
	
	/**
	 * Gets the number of days since last login
	 * @return The number of days since last login
	 */
	int getDaysSinceLastLogin();
	
	/**
	 * Gets the total play time
	 * @return The total play time
	 */
	 long getPlaytime();
	
	/**
	 * Gets the auto-join status
	 * @return The auto-join status
	 */
	 boolean getAutoJoin();
	
	/**
	 * Gets the current chat channel
	 * @return The current chat channel
	 */
	 IChatChannel getChatChannel();
	
	/**
	 * Sets the chat channel
	 * @param chatChannel The new chat channel
	 */
	void setChatChannel(IChatChannel chatChannel);
	
	/**
	 * Sets the chat channel to global chat
	 */
	void moveToGlobalChat();
	
	/**
	 * Sets the last messaged player
	 * @return The last messaged player
	 */
	IPlayer getLastMessaged();
	
	/**
	 * Sets the last messaged player
	 * @param lastMessaged The last messaged player
	 */
	void setLastMessaged(IPlayer lastMessaged);
	
	/**
	 * Gets the ban status
	 * @param banned Whether the player is banned
	 */
	boolean getBanned();
	
	/**
	 * Gets the ban message
	 * @param The ban message
	 */
	String getBanMessage();
	
	/**
	 * Sends a message to an online player
	 * @param str
	 * @param args
	 */
	void msg(String str, Object... args);
	
	/**
	 * Gets the distance from another player
	 * @param p The player to check
	 * @return The distance the players are away from each other
	 */
	int getDistanceFrom(IPlayer other);
	
	/**
	 * Gets the offline messages for the player
	 * @return The offline messages
	 */
	Collection<String> getOfflineMessages();
	
	/**
	 * Gets the build state
	 * @return The current build state
	 */
	BuildState getBuildState();
	
	/**
	 * Gets the admin bypass status
	 * @return true if bypass is enabled
	 */
	boolean getAdminBypass();

	/**
	 * Sets the ban status
	 * @param adminBypass The new bypass state
	 */
	void setAdminBypass(boolean adminBypass);
	
	/**
	 * Gets the admin vanished status
	 * @return true if the player is vanished
	 */
	public boolean getVanished();

	/**
	 * Sets the vanished status
	 * @param vanished The new vanished state
	 */
	void setVanished(boolean vanished);
	
	/**
	 * Gets whether the player was freed offline
	 * @return true if the player was freed offline
	 */
	boolean getFreedOffline();
	
	/**
	 * Gets the player bed location
	 * @return the bed location
	 */
	Location getBedLocation();
	
	/**
	 * Sets the ignored state of a player
	 * @param sp The player to set
	 * @param ignored The ignored status
	 */
	void setIgnored(IPlayer sp, boolean ignored);
	
	/**
	 * Gets whether a player is ignored
	 * @return Whether the player is ignored
	 */
	boolean isIgnoring(IPlayer sp);
	
	/**
	 * Gets the broadcasting players
	 * @return The broadcast players
	 */
	Iterable<IPlayer> getBcastPlayers();
	
	/**
	 * Adds a broadcast player
	 * @param sp The broadcast player
	 */
	void addBcastPlayer(IPlayer sp);
	
	/**
	 * Gets the player requested to broadcast pearl location
	 * @return the requested broadcast player
	 */
	IPlayer getRequestedBcastPlayer();
	
	/**
	 * Sets the requested broadcast player
	 * @param broadcastRequestPlayer the requested broadcast player
	 */
	void setRequestedBcastPlayer(IPlayer broadcastRequestPlayer);
	
	/**
	 * Gets whether the player has admin permissions
	 */
	boolean isAdmin();
	
	/**
	 * Teleports the player to a location
	 * @param l The location
	 * @return True if successful
	 */
	boolean teleport(Location location);
}
