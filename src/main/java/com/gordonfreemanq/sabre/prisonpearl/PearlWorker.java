package com.gordonfreemanq.sabre.prisonpearl;

import org.bukkit.Bukkit;

import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Interval task that deducts strength from existing prison pearls
 * @author GFQ
 */
public class PearlWorker implements Runnable {

	private final PearlManager pm;
	private boolean enabled = false;
	private int weakenIntervalMin;
	private int weakenAmount;
	private int daysInactiveThreshold;
	
	// 20 * 60 ticks/ minute
	private static final int TICKS_PER_MINUTE = 1200;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public PearlWorker(PearlManager pm, SabreConfig config) {
		this.pm = pm;
		this.weakenIntervalMin = config.getPearlWeakenInterval();
		this.weakenAmount = config.getPearlWeakenAmount();
		this.daysInactiveThreshold = config.getPearlDaysInactiveThreshold();
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
		long tickInterval = weakenIntervalMin * TICKS_PER_MINUTE;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.getPlugin(), this, 0, tickInterval);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		// Iterate through all the pearls and reduce the strength
		// This will free any pearls that reach zero strength
		for (PrisonPearl pp : pm.getPearls()) {
			
			// Only weaken the pearls of the players that have logged in within the threshold
			if (pp.getPlayer().getDaysSinceLastLogin() < daysInactiveThreshold) {
				pm.decreaseSealStrength(pp, weakenAmount);
				pp.verifyLocation();
			}
		}
	}
}
