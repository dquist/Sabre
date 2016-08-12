package com.gordonfreemanq.sabre.prisonpearl;

import org.bukkit.Bukkit;

import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Interval task that deducts strength from existing prison pearls
 * @author GFQ
 */
public class PearlWorker implements Runnable {

	private final SabrePlugin plugin;
	
	private boolean enabled = false;
	
	// 20 * 60 ticks/ minute
	private static final int TICKS_PER_MINUTE = 1200;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public PearlWorker(SabrePlugin plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
		long tickInterval = plugin.config().getPearlWeakenInterval() * TICKS_PER_MINUTE;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.instance(), this, 0, tickInterval);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		int weakenAmount = plugin.config().getPearlWeakenAmount();
		int daysInactiveThreshold = plugin.config().getPearlDaysInactiveThreshold();
		
		PearlManager pearls = plugin.getPearlManager();
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
