package com.gordonfreemanq.sabre;

import org.bukkit.Bukkit;

/**
 * Runnable class that tracks player stats
 */
public class StatsTracker implements Runnable {
	
	private final SabrePlugin plugin;
	private long lastUpdate;
	private boolean enabled;
	
	
	/**
	 * Starts the tracker
	 */
	public void start() {
		enabled = true;
		lastUpdate = System.currentTimeMillis();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.instance(), this, 0, 200);
	}
	
	
	/**
	 * Stops the tracker
	 */
	public void stop() {
		enabled = false;
	}
	
	
	/**
	 * Creates a new StatsTracker instance
	 * @param pm
	 */
	public StatsTracker(SabrePlugin plugin) {
		this.plugin = plugin;
		lastUpdate = System.currentTimeMillis();
	}

	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		PlayerManager pm = plugin.getPlayerManager();
		long timeToAdd = System.currentTimeMillis() - lastUpdate;
		
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			pm.addPlayTime(p,  timeToAdd);
		}
		
		lastUpdate = System.currentTimeMillis();
	}
}
