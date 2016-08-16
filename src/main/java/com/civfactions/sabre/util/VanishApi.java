package com.civfactions.sabre.util;

//import org.kitteh.vanish.VanishManager;
//import org.kitteh.vanish.VanishPlugin;

import com.civfactions.sabre.SabrePlayer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Interfaces to the VanishNoPacket API
 * @author GFQ
 *
 */
public class VanishApi {
	
	// Temp
	private class VanishManager {
		public void vanish(Player p, boolean arg1, boolean arg2) {
		}
		public void reveal(Player p, boolean arg1, boolean arg2) {
		}
		public boolean isVanished(Player p) {
			return false;
		}
	}
	
	private class VanishPlugin {
		public VanishManager getManager() {
			return null;
		}
	}
	
	private VanishManager vm;
	
	
	
	/**
	 * Creates a new VanishApi instance
	 */
	public VanishApi() {
	}
	
	
	/**
	 * Initialize the class
	 */
	public void initialize() {
		if(Bukkit.getPluginManager().getPlugin("VanishNoPacket") != null) {
			VanishPlugin vp = (VanishPlugin) Bukkit.getPluginManager().getPlugin("VanishNoPacket");
			vm = vp.getManager();
		}
	}
	
	
	/**
	 * Vanishes a player
	 * @param sp The player
	 */
	public void vanish(SabrePlayer sp) {
		if (vm == null) {
			return;
		}
		
		vm.vanish(sp.getPlayer(), true, false);
	}
	
	
	/**
	 * Unvanishes a player
	 * @param sp The player
	 */
	public void unVanish(SabrePlayer sp) {
		if (vm == null) {
			return;
		}
		
		vm.reveal(sp.getPlayer(), true, false);
	}
	
	
	/**
	 * Gets whether a player in vanished
	 * @param sp The player
	 * @return true if the player is vanished
	 */
	public boolean isVanished(SabrePlayer sp) {
		if (vm == null) {
			return false;
		}
		
		if (vm.isVanished(sp.getPlayer())) {
			return true;
		}
		return false;
	}

}
