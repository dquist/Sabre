package com.gordonfreemanq.sabre.factory;

import java.util.Queue;

import org.bukkit.Bukkit;

import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Periodically surveys the farms and updates their production output
 * @author GFQ
 */
public class FarmSurveyWorker implements Runnable {

	private boolean enabled;
	private Queue<FarmFactory> runningFarms;
	private int taskID;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public FarmSurveyWorker() {
		this.enabled = false;
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start(Queue<FarmFactory> runningFarms, int tickSpacing) {
		this.runningFarms = runningFarms;
		this.enabled = true;
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.getPlugin(), this, 0, tickSpacing);
	}
	
	
	/**
	 * Stops the task
	 */
	public void stop() {
		this.enabled = false;
		Bukkit.getScheduler().cancelTask(taskID);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		// Process any remaining farms
		FarmFactory factory = runningFarms.poll();
		if (factory != null) {
			factory.survey();
		} else {
			// No more to process, so stop the task
			stop();
		}
	}
	
	
	/**
	 * Gets whether this task is enabled
	 * @return Whether the task is enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}
}
