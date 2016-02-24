package com.gordonfreemanq.sabre.factory;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Updates running factories
 * @author GFQ
 */
public class FactoryWorker implements Runnable {

	private final HashSet<BaseFactory> runningFactories;
	private boolean enabled;
	
	private static FactoryWorker instance;
	
	public static FactoryWorker getInstance() {
		return instance;
	}
	
	private Set<BaseFactory> pendingRemove;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public FactoryWorker() {
		runningFactories = new HashSet<BaseFactory>();
		enabled = false;
		this.pendingRemove = new HashSet<BaseFactory>();
		
		instance = this;
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.getPlugin(), this, 0, 20);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		// Update all running factories
		for(BaseFactory f : runningFactories) {
			if (f.getRunning()) {
				f.update();
			}
		}
	}
	
	
	/**
	 * Adds a new running factory
	 * @param f The factory to add
	 */
	public void addRunning(BaseFactory f) {
		
		// This prevents the same factory from being loaded twice.
		// This is a possibility because factories in unloaded chunks can
		// still run. When the chunk is loaded, then the running instance
		// will be replaced
		for (BaseFactory fac : this.runningFactories) {
			if (fac.getLocation().equals(f.getLocation())) {
				pendingRemove.add(fac);
			}
		}
		runningFactories.removeAll(pendingRemove);
		
		runningFactories.add(f);
	}
	
	
	/**
	 * Removes a running factory
	 * @param f The factory to remove
	 */
	public void removeRunning(BaseFactory f) {
		pendingRemove.add(f);
	}
	
	/**
	 * Gets the running factories
	 * @return The running factories
	 */
	public Set<BaseFactory> getRunningFactories() {
		return runningFactories;
	}
}
