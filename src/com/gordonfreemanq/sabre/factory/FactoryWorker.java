package com.gordonfreemanq.sabre.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Updates running factories
 * @author GFQ
 */
public class FactoryWorker implements Runnable {

	private final HashMap<Location, BaseFactory> runningFactories;
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
		runningFactories = new HashMap<Location, BaseFactory>();
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
		//SabrePlugin.getPlugin().log(Level.INFO, "Running Factories: %d", runningFactories.size());
		for(BaseFactory f : runningFactories.values()) {
			if (f.getRunning()) {
				f.update();
			}
		}
		
		// Remove old ones
		if (pendingRemove.size() > 0) {
			runningFactories.values().removeAll(pendingRemove);
			pendingRemove.clear();
		}
	}
	
	
	/**
	 * Adds a new running factory
	 * @param f The factory to add
	 */
	public void addRunning(BaseFactory f) {
		// Make sure the same factory isn't running twice
		for(BaseFactory run : runningFactories.values()) {
			if (run.getLocation().equals(f.getLocation())) {
				pendingRemove.add(f);
			}
		}
		
		runningFactories.put(f.getLocation(), f);
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
		return new HashSet<BaseFactory>(runningFactories.values());
	}
}
