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
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public FactoryWorker() {
		runningFactories = new HashSet<BaseFactory>();
		enabled = false;
		
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
		
		Set<BaseFactory> unloaded = new HashSet<BaseFactory>();
		
		// Update all running factories
		for(BaseFactory f : runningFactories) {
			if (f.getLocation().getChunk().isLoaded()) {
				if (f.getRunning()) {
					f.update();
				}
			} else {
				unloaded.add(f);
			}
		}
		
		// Remove the unloaded factories
		runningFactories.removeAll(unloaded);
	}
	
	
	/**
	 * Adds a new running factory
	 * @param f The factory to add
	 */
	public void addRunning(BaseFactory f) {
		runningFactories.add(f);
	}
	
	
	/**
	 * Removes a running factory
	 * @param f The factory to remove
	 */
	public void removeRunning(BaseFactory f) {
		runningFactories.remove(f);
	}
	
	/**
	 * Gets the running factories
	 * @return The running factories
	 */
	public Set<BaseFactory> getRunningFactories() {
		return runningFactories;
	}
}
