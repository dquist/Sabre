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
	
	private Set<BaseFactory> pendingRemove;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public FactoryWorker() {
		runningFactories = new HashMap<Location, BaseFactory>();
		pendingRemove = new HashSet<BaseFactory>();
		enabled = false;
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.instance(), this, 0, 20);
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
			} else {
				pendingRemove.add(f);
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
		BaseFactory removed = runningFactories.put(f.getLocation(), f);
		
		if (removed != null) {
			f.copyFrom(removed);
		}
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
	
	
	/**
	 * Gets a running factory by location
	 * @param l The location
	 * @return The base factory
	 */
	public BaseFactory getRunningByLocation(Location l) {
		return runningFactories.get(l);
	}
}
