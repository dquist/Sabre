package com.gordonfreemanq.sabre;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.gordonfreemanq.sabre.chat.GlobalChat;
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.core.ISabreLog;
import com.gordonfreemanq.sabre.util.SabreUtil;

public class PlayerListener implements Listener {
	
	private static final String LOAD_ERR = "The server isn't loaded yet.";
	
	private final PlayerManager pm;
	private final GlobalChat globalChat;
	private final ISabreLog logger;
	private boolean pluginLoaded;
	
	public PlayerListener(PlayerManager pm, GlobalChat globalChat, ISabreLog logger)
	{
		this.pm = pm;
		this.globalChat = globalChat;
		this.logger = logger;
		this.pluginLoaded = false;
	}
	
	
	/**
	 * Sets the plugin loaded status which allows players to join
	 * @param pluginLoaded The plugin loaded status
	 */
	public void setPluginLoaded(boolean pluginLoaded) {
		this.pluginLoaded = pluginLoaded;
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e)
	{
		// Don't let players join if things didn't start up correctly
		if (!pluginLoaded) {
			e.setKickMessage(LOAD_ERR);
			e.setLoginResult(Result.KICK_OTHER);
		}
		
		SabrePlayer sp = pm.getPlayerById(e.getUniqueId());
		if (sp != null && sp.getBanned()) {
			e.setLoginResult(Result.KICK_BANNED);
			String fullBanMessage = String.format("%s\n%s", Lang.youAreBanned, sp.getBanMessage());
			e.setKickMessage(fullBanMessage);
		}
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent e) {
		
		// Default to failure
		e.setKickMessage(Lang.exceptionLogin);
		e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
		
		try {
			
			// Ok we're good! Let 'em in
			e.setResult(PlayerLoginEvent.Result.ALLOWED);
			e.setKickMessage(null);
		} catch (Exception ex) {
			 logger.log(Level.SEVERE, SabreUtil.getExceptionMessage("onPlayerLogin", ex));
			 throw ex;
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		onPlayerDisconnect(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKicked(PlayerKickEvent e) {
        e.setLeaveMessage(null);
		onPlayerDisconnect(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		// Force survival for players that are not admins
		if (!e.getPlayer().hasPermission("sabre.admin")) {
			p.setGameMode(GameMode.SURVIVAL);
			p.setFlying(false);
		}
		
		e.setJoinMessage(null);
		handlePlayerJoin(e.getPlayer());
	}
	
	
	/**
	 * Handles a joining player
	 * @param pThe player that is joining
	 */
	public void handlePlayerJoin(Player p) {
		SabrePlayer sp = pm.getPlayerById(p.getUniqueId());
		if (sp == null) {
			// This player has never logged in before, make a new instance
			sp = pm.createNewPlayer(p);
			
			Location spawnLocation = SabreUtil.chooseSpawn(p.getWorld(), 5000);
			SabreUtil.sendGround(p, spawnLocation);
			p.teleport(spawnLocation);
			p.sendMessage("You wake up in an unfamiliar place.");
		}
		
		
		// Update the player model
		sp.setPlayer(p);
		sp.setLastLogin(new Date());
		
		// This ensures the player name always stays the same
		p.setDisplayName(sp.getName());
		p.setCustomName(sp.getName());
		p.setPlayerListName(sp.getName());
		
		
		sp.msg("<h>This server is running Sabre v%s.", SabrePlugin.version);
		sp.msg("<h>Type \"<p>/s help<h>\" for more info.");
		
		pm.printOfflineMessages(sp);
		pm.clearOfflineMessages(sp);
		
		pm.onPlayerConnect(sp);
	}
	
	
	/**
	 * Handles a disconnecting player
	 * @param p The player that is disconnecting
	 */
	public void onPlayerDisconnect(Player p) {
		SabrePlayer sp = pm.getPlayerById(p.getUniqueId());
		
		// Removes the player from the online list
		pm.onPlayerDisconnect(sp);
		
		// Remove the bukkit player instance from the model
		sp.setPlayer(null);
	}
	
	
	/**
	 * Handles players that are already online when the plugin is loaded
	 */
	public void handleOnlinePlayers() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (pluginLoaded) {
				handlePlayerJoin(p);
			} else {
				p.kickPlayer(LOAD_ERR);
			}
		}
	}
	
	 @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
    }
	 
	 
	 @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	    public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		 try {
	        e.setCancelled(true);
	        
	        SabrePlayer p = pm.getPlayerById(e.getPlayer().getUniqueId());
	        
	        IChatChannel channel = p.getChatChannel();
	        if (channel == null) {
	        	channel = globalChat;
	        	p.setChatChannel(channel);
	        }
	        
	        channel.chat(p, e.getMessage());
			 
		 } catch (Exception ex) {
			 e.getPlayer().sendMessage(SabrePlugin.getPlugin().txt.parse(Lang.exceptionGeneral));
			 logger.log(Level.SEVERE, SabreUtil.getExceptionMessage("onPlayerChatEvent", ex));
			 throw ex;
		 }
	 }
	 
	 
	/**
	 * Handles the respawn point
	 * @param e The event args
	 */
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		/*
		SabrePlayer p = pm.getPlayerById(e.getPlayer().getUniqueId());
		Location l = p.getBedLocation();
		boolean useBed = false;
		
		if (l != null) {
			
			if (l.getBlock().getType() == Material.BED_BLOCK) {
				SabreBlock b = BlockManager.getInstance().getBlockAt(SabreUtil.getRealBlock(l.getBlock()).getLocation());
				if (b == null || b.canPlayerAccess(p)) {
					useBed = true;
				}
			}
			
			if (useBed) {
				e.getPlayer().setBedSpawnLocation(l, true);
			} else {
				p.msg(Lang.playerBedMissing);
				Location loc = SabreUtil.fuzzLocation(PearlManager.getInstance().getFreeWorld().getSpawnLocation());
				e.getPlayer().setBedSpawnLocation(loc, true);
			}
			
		}*/
	}
	

	/**
	 * Sets the bed location by right-clicking
	 * @param e The event args
	 */
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent e) {
		SabrePlayer p = pm.getPlayerById(e.getPlayer().getUniqueId());
		
		Action a = e.getAction();
		if (a.equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.BED_BLOCK)) {
			Location l = e.getClickedBlock().getLocation();
			pm.setBedLocation(p, l);
			p.msg(Lang.playerSetBed);
			e.getPlayer().setBedSpawnLocation(l, true);
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onRandomSpawn(PlayerRespawnEvent event){
		
		Player player = event.getPlayer();
		
		World world = event.getRespawnLocation().getWorld();
				
		if (event.isBedSpawn()) {
			return; 
		}
		
		Location spawnLocation = SabreUtil.chooseSpawn(world, 5000);
		
		SabreUtil.sendGround(player, spawnLocation);
		
		event.setRespawnLocation(spawnLocation);
		player.sendMessage("You wake up in an unfamiliar place.");
	}
}
