package com.civfactions.sabre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.civfactions.sabre.blocks.BuildState;
import com.civfactions.sabre.chat.IChatChannel;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.util.Guard;
import com.civfactions.sabre.util.Permission;

/**
 * Represents a player that has joined the server and may or may not be online
 * @author GFQ
 */
public class SabrePlayer implements IPlayer {
	
	public static final String ADMIN_NODE = Permission.ADMIN.node;

	private final SabrePlugin plugin;
	private final PlayerManager pm;
	
	// Unique ID of the player
	private final UUID uid;
	
	// The player's display name
	private String name;
	
	// The Bukkit player instance
	private Player player;
	
	//The player's faction
	private SabreFaction faction;
	
	// Login and play time data
	private Date firstLogin;
	private Date lastLogin;
	private long playTime;
	private boolean banned;
	private String banMessage;
	
	// Whether the player auto-joins groups
	private boolean autoJoin;
	
	// The current chat channel
	private IChatChannel chatChannel;
	
	// The player last messaged, used for replying
	private IPlayer lastMessaged;
	
	// Pending offline messages for the player
	private List<String> offlineMessages;
	
	// The current build mode of the player
	private BuildState buildState;
	
	// Whether admin bypass mode is enabled
	private boolean adminBypass;
	
	// Whether the player is vanished or not
	private boolean vanished;
	
	// Whether the player was freed from a prison pearl while offline
	private boolean freedOffline;
	
	// The player's bed location
	private Location bedLocation;
	
	// Other players that are being ignored by this player
	private Set<IPlayer> ignoredPlayers;
	
	// Players that are receiving prison pearl broadcast messages
	private Set<IPlayer> bcastPlayers;
	
	// The last player who requested a prison pearl broadcast
	private IPlayer broadcastRequestPlayer;
	
	
	/**
	 * Creates a new SabrePlayer instance
	 * @param uid The player's ID
	 * @param name The player's display name
	 */
	public SabrePlayer(SabrePlugin plugin, UUID uid, String name) {
		Guard.ArgumentNotNull(plugin, "plugin");
		Guard.ArgumentNotNull(uid, "uid");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		Guard.ArgumentNotNull(plugin.getPlayerManager(), "plugin.getPlayerManager()");
		
		this.plugin = plugin;
		this.pm = plugin.getPlayerManager();
		this.uid = uid;
		this.name = name;
		this.firstLogin = plugin.now();
		this.lastLogin = plugin.now();
		this.playTime = 0;
		this.autoJoin = false;
		this.chatChannel = plugin.getGlobalChat();
		this.lastMessaged = null;
		this.banned = false;
		this.banMessage = "";
		this.offlineMessages = new ArrayList<String>();
		this.buildState = new BuildState();
		this.adminBypass = false;
		this.vanished = false;
		this.freedOffline = false;
		this.ignoredPlayers = new HashSet<IPlayer>();
		this.bcastPlayers = new HashSet<IPlayer>();
		this.broadcastRequestPlayer = null;
		this.faction = null;
	}
	
	
	/**
	 * Gets the player ID
	 * @return The player ID
	 */
	@Override
	public UUID getID() {
		return this.uid;
	}
	
	
	/**
	 * Gets the player name
	 * @return The player name
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Sets the player name
	 * @param name The player name
	 */
	public void setName(String name) {
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
		this.name = name;
		
		if (player != null) {
			player.setDisplayName(name);
			player.setCustomName(name);
			player.setPlayerListName(name);
		}
	}
	
	
	/**
	 * Gets the Bukkit player instance
	 * @return The Bukkit player instance
	 */
	@Override
	public Player getPlayer() {
		return this.player;
	}
	
	
	/**
	 * Sets the player instance
	 * @param player The player instance
	 */
	@Override
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	
	/**
	 * Gets the player's faction
	 * @return The player's faction
	 */
	@Override
	public SabreFaction getFaction() {
		return this.faction;
	}
	
	
	/**
	 * Sets the player's faction
	 * @param faction The player's faction
	 */
	public void setFaction(SabreFaction faction) {
		this.faction = faction;
	}	
	
	
	/**
	 * Gets whether the player is online
	 * @return true if the player is online
	 */
	@Override
	public boolean isOnline() {
		return player != null && player.isOnline();
	}
	
	
	/**
	 * Gets the first login time
	 * @return The first login time
	 */
	@Override
	public Date getFirstLogin() {
		return this.firstLogin;
	}
	
	
	/**
	 * Sets the first login time
	 * @param firstLogin The first login time
	 */
	public void setFirstLogin(Date firstLogin) {
		Guard.ArgumentNotNull(firstLogin, "firstLogin");
		
		this.firstLogin = firstLogin;
	}
	
	
	/**
	 * Gets the last login time
	 * @return The first login time
	 */
	@Override
	public Date getLastLogin() {
		return this.lastLogin;
	}
	
	
	/**
	 * Sets the last login time
	 * @param lastLogin The first login time
	 */
	public void setLastLogin(Date lastLogin) {
		Guard.ArgumentNotNull(lastLogin, "lastLogin");
		
		this.lastLogin = lastLogin;
	}
	
	
	/**
	 * Gets the number of days since last login
	 * @return The number of days since last login
	 */
	@Override
	public int getDaysSinceLastLogin() {
		Date now = plugin.now();
		long timeDiff = now.getTime() - lastLogin.getTime();
		long diffDays = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);
		return (int)diffDays;
	}
	
	
	/**
	 * Gets the total play time
	 * @return The total play time
	 */
	@Override
	public long getPlaytime() {
		return this.playTime;
	}
	
	
	/**
	 * Sets the total play time
	 * @param lastLogin The total play time
	 */
	public void setPlaytime(long playTime) {
		this.playTime = playTime;
	}
	
	
	/**
	 * Adds to the total play time
	 * @param lastLogin The total play time
	 */
	public void addPlayTime(long playTime) {
		this.playTime += playTime;
	}
	
	
	/**
	 * Gets the auto-join status
	 * @return The auto-join status
	 */
	@Override
	public boolean getAutoJoin() {
		return this.autoJoin;
	}
	
	
	/**
	 * Sets the auto-join status
	 * @param autoJoin The auto-join status
	 */
	public void setAutoJoin(boolean autoJoin) {
		this.autoJoin = autoJoin;
	}
	
	
	/**
	 * Gets the current chat channel
	 * @return The current chat channel
	 */
	@Override
	public IChatChannel getChatChannel() {
		return this.chatChannel;
	}
	
	
	/**
	 * Sets the chat channel
	 * @param chatChannel The new chat channel
	 */
	@Override
	public void setChatChannel(IChatChannel chatChannel) {
		Guard.ArgumentNotNull(chatChannel, "chatChannel");
		Guard.ArgumentNotEquals(chatChannel, "chatChannel", this, "self");
		
		this.chatChannel = chatChannel;
	}
	
	
	/**
	 * Sets the chat channel to global chat
	 */
	@Override
	public void moveToGlobalChat() {
		this.chatChannel = plugin.getGlobalChat();
	}
	
	
	/**
	 * Sets the last messaged player
	 * @return The last messaged player
	 */
	@Override
	public IPlayer getLastMessaged() {
		return this.lastMessaged;
	}
	
	
	/**
	 * Sets the last messaged player
	 * @param lastMessaged The last messaged player
	 */
	@Override
	public void setLastMessaged(IPlayer lastMessaged) {		
		this.lastMessaged = lastMessaged;
	}
	
	
	/**
	 * Gets the ban status
	 * @param banned Whether the player is banned
	 */
	@Override
	public boolean getBanned() {
		return this.banned;
	}

	
	/**
	 * Sets the ban status
	 * @return banned true if the player is banned
	 */
	public void setBanned(boolean banned) {
		this.banned = banned;
	}
	
	
	/**
	 * Gets the ban message
	 * @param The ban message
	 */
	@Override
	public String getBanMessage() {
		return this.banMessage;
	}
	
	
	/**
	 * Sets the ban message
	 * @return banMessage the ban message
	 */
	public void setBanMessage(String banMessage) {
		Guard.ArgumentNotNull(banMessage, "banMessage");
		
		this.banMessage = banMessage;
	}
	
	/**
	 * Sends a message to an online player
	 * @param str
	 * @param args
	 */
	@Override
	public void msg(String str, Object... args) {
		Guard.ArgumentNotNull(str, "str");
		
		String msg = plugin.txt().parse(str, args);
		if (this.isOnline()) {
			this.getPlayer().sendMessage(msg);
		} else {
			plugin.getPlayerManager().addOfflineMessage(this, msg);
		}
	}


	/**
	 * Chats with a specific player as a private message
	 * @param sender The player sending the message
	 * @param msg The message
	 */
	@Override
	public void chat(IPlayer sender, String msg) {
		Guard.ArgumentNotNull(sender, "sender");
		Guard.ArgumentNotNull(msg, "msg");
		
		if (this.isOnline()) {
			if (this.ignoredPlayers.contains(sender)) {
				sender.msg(Lang.chatYouAreIgnored, this.name);
				
				// Move to global chat
				if (sender.getChatChannel().equals(this)) {
					sender.setChatChannel(plugin.getGlobalChat());
					sender.msg(Lang.chatMovedGlobal);
				}
				return;
			}
			
			sender.msg("<lp>To %s: %s", this.getName(), msg);
			this.msg("<lp>From %s: %s", sender.getName(), msg);
			this.setLastMessaged(sender);
			sender.setLastMessaged(this);
			plugin.logger().log(Level.INFO, "%s -> %s: %s", sender.getName(), this.getName(), msg);
		} else {
			sender.msg(Lang.chatPlayerNowOffline, this.getName());
			
			// Move to global chat
			if (sender.getChatChannel().equals(this)) {
				sender.setChatChannel(plugin.getGlobalChat());
				sender.msg(Lang.chatMovedGlobal);
			}
		}
	}
	
	
	
	@Override
	public void chatMe(IPlayer sender, String msg) {		
		Guard.ArgumentNotNull(sender, "sender");
		Guard.ArgumentNotNull(msg, "msg");
		
		if (this.isOnline()) {
			sender.msg("<lp><it>%s %s", this.getName(), msg);
			this.msg("<lp><it>%s %s", sender.getName(), msg);
			this.setLastMessaged(sender);
			sender.setLastMessaged(this);
			plugin.logger().log(Level.INFO, "%s -> %s: %s", sender.getName(), this.getName(), msg);
		} else {
			sender.msg(Lang.chatPlayerNowOffline, this.getName());
			sender.msg(Lang.chatMovedGlobal, this.getName());
		}
	}
	
	
	/**
	 * Gets the distance from another player
	 * @param p The player to check
	 * @return The distance the players are away from each other
	 */
	@Override
	public int getDistanceFrom(IPlayer other) {
		Guard.ArgumentNotNull(other, "other");
		
		if (!isOnline() || !other.isOnline()) {
			return -1;
		}
		
		int dx = other.getPlayer().getLocation().getBlockX() - this.getPlayer().getLocation().getBlockX();
		int dz = other.getPlayer().getLocation().getBlockZ() - this.getPlayer().getLocation().getBlockZ();
		
		return (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));
	}
	
	
	/**
	 * Gets the offline messages for the player
	 * @return The offline messages
	 */
	@Override
	public Collection<String> getOfflineMessages() {
		return this.offlineMessages;
	}
	
	
	/**
	 * Adds an offline message for the player
	 * @param message The message to add
	 */
	public void addOfflineMessage(String message) {
		Guard.ArgumentNotNullOrEmpty(message, "message");
		
		this.offlineMessages.add(message);
	}
	
	
	/**
	 * Gets the build state
	 * @return The current build state
	 */
	@Override
	public BuildState getBuildState() {
		return this.buildState;
	}
	
	
	/**
	 * Gets the admin bypass status
	 * @return true if bypass is enabled
	 */
	@Override
	public boolean getAdminBypass() {
		return this.adminBypass;
	}

	
	/**
	 * Sets the ban status
	 * @param adminBypass The new bypass state
	 */
	@Override
	public void setAdminBypass(boolean adminBypass) {
		this.adminBypass = adminBypass;
	}
	
	
	/**
	 * Gets the admin vanished status
	 * @return true if the player is vanished
	 */
	@Override
	public boolean getVanished() {
		return this.vanished;
	}

	
	/**
	 * Sets the vanished status
	 * @param vanished The new vanished state
	 */
	@Override
	public void setVanished(boolean vanished) {
		this.vanished = vanished;
	}
	
	
	/**
	 * Gets whether the player was freed offline
	 * @return true if the player was freed offline
	 */
	@Override
	public boolean getFreedOffline() {
		return this.freedOffline;
	}

	
	/**
	 * Gets whether the player was freed offline
	 * @param freedOffline The freedOffline value
	 */
	public void setFreedOffline(boolean freedOffline) {
		this.freedOffline = freedOffline;
	}
	
	
	/**
	 * Gets the player bed location
	 * @return the bed location
	 */
	@Override
	public Location getBedLocation() {
		return this.bedLocation;
	}

	
	/**
	 * Gets the bed location
	 * @param bedLocation The bed location
	 */
	public void setBedLocation(Location bedLocation) {
		this.bedLocation = bedLocation;
	}
	
	
	/**
	 * Sets the ignored state of a player
	 * @param sp The player to set
	 * @param ignored The ignored status
	 */
	@Override
	public void setIgnored(IPlayer sp, boolean ignored) {
		Guard.ArgumentNotNull(sp, "sp");
		
		if (ignored && !ignoredPlayers.contains(sp)) {
			ignoredPlayers.add(sp);
		} else {
			ignoredPlayers.remove(sp);
		}
	}
	
	
	/**
	 * Gets whether a player is ignored
	 * @return Whether the player is ignored
	 */
	@Override
	public boolean isIgnoring(IPlayer sp) {
		Guard.ArgumentNotNull(sp, "sp");
		
		return ignoredPlayers.contains(sp);
	}
	
	
	/**
	 * Gets the broadcasting players
	 * @return The broadcast players
	 */
	@Override
	public Set<IPlayer> getBcastPlayers() {
		return this.bcastPlayers;
	}
	
	
	/**
	 * Adds a broadcast player
	 * @param sp The broadcast player
	 */
	@Override
	public void addBcastPlayer(IPlayer sp) {
		this.bcastPlayers.add(sp);
	}
	
	
	/**
	 * Gets the player requested to broadcast pearl location
	 * @return the requested broadcast player
	 */
	@Override
	public IPlayer getRequestedBcastPlayer() {
		return this.broadcastRequestPlayer;
	}
	
	
	/**
	 * Sets the requested broadcast player
	 * @param broadcastRequestPlayer the requested broadcast player
	 */
	@Override
	public void setRequestedBcastPlayer(IPlayer broadcastRequestPlayer) {
		this.broadcastRequestPlayer = broadcastRequestPlayer;
	}
	
	
	/**
	 * Gets whether the player has admin permissions
	 */
	@Override
	public boolean isAdmin() {
		return isOnline() && player.hasPermission(ADMIN_NODE);
	}
	
	/**
	 * Teleports the player to a location
	 * @param l The location
	 * @return True if successful
	 */
	@Override
	public boolean teleport(Location location) {
		Guard.ArgumentNotNull(location, "location");
		
		if (isOnline()) {
			return player.teleport(location);
		}
		return false;
	}
}
