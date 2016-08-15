package com.civfactions.sabre.prisonpearl;

import org.bukkit.Bukkit;

import com.civfactions.sabre.SabreConfig;
import com.civfactions.sabre.SabrePlugin;

/**
 * Interval task that deducts strength from existing prison pearls
 * @author GFQ
 */
public class PearlWorker implements Runnable {

	private final SabrePlugin plugin;
	private final PearlManager pearls;
	private final SabreConfig config;
	
	private boolean enabled = false;
	
	// 20 * 60 ticks/ minute
	private static final int TICKS_PER_MINUTE = 1200;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public PearlWorker(SabrePlugin plugin, PearlManager pearls, SabreConfig config) {
		this.plugin = plugin;
		this.pearls = pearls;
		this.config = config;
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
		long tickInterval = config.getPearlWeakenInterval() * TICKS_PER_MINUTE;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, tickInterval);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		int weakenAmount = config.getPearlWeakenAmount();
		int daysInactiveThreshold = config.getPearlDaysInactiveThreshold();
		
		// Iterate through all the pearls and reduce the strength
		// This will free any pearls that reach zero strength
		for (PrisonPearl pp : pearls.getPearls()) {
			
			// Only weaken the pearls of the players that have logged in within the threshold
			if (pp.getPlayer().getDaysSinceLastLogin() < daysInactiveThreshold) {
				pearls.decreaseSealStrength(pp, weakenAmount);
				pp.verifyLocation();
			}
		}
	}
}
