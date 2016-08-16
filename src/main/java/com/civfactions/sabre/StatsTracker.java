package com.civfactions.sabre;

/**
 * Runnable class that tracks player stats
 */
public class StatsTracker implements Runnable {
	
	public static int RUN_PERIOD_TICKS = 200;
	
	private final SabrePlugin plugin;
	private final PlayerManager pm;
	
	private int taskId;
	private long lastUpdate;
	private boolean enabled;
	
	
	/**
	 * Creates a new StatsTracker instance
	 * @param pm
	 */
	public StatsTracker(SabrePlugin plugin, PlayerManager pm) {
		this.plugin = plugin;
		this.pm = pm;
		
		lastUpdate = System.currentTimeMillis();
	}
	
	
	/**
	 * Starts the tracker
	 */
	public void start() {
		if (!enabled) {
			lastUpdate = System.currentTimeMillis();
			taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 200);
			enabled = true;
		}
	}
	
	
	/**
	 * Stops the tracker
	 */
	public void stop() {
		if (enabled) {
			plugin.getServer().getScheduler().cancelTask(taskId);
			enabled = false;
		}
	}
	
	/**
	 * Gets whether the service is enabled
	 * @return true if it's enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
	

	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		long timeToAdd = System.currentTimeMillis() - lastUpdate;
		
		for (IPlayer p : pm.getOnlinePlayers()) {
			pm.addPlayTime(p, timeToAdd);
		}
		
		lastUpdate = System.currentTimeMillis();
	}
}
