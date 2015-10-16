package com.gordonfreemanq.sabre.factory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;

import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlugin;

/**
 * Schedules the periodic survey of farm factories
 * @author GFQ
 */
public class FarmSurveyScheduler implements Runnable {

	private boolean enabled;
	private int surveyPeriodMinutes;
	private int surveyTickSpacing;
	
	private final FarmSurveyWorker surveyWorker;
	
    private static final int TICKS_PER_MINUTE = 1200;
	
	/**
	 * Creates a new FactoryWorker instance
	 */
	public FarmSurveyScheduler(FarmSurveyWorker surveyWorker, SabreConfig config) {
		this.surveyWorker = surveyWorker;
		this.enabled = false;
		this.surveyPeriodMinutes = config.getFarmSurveyPeriod();
		this.surveyTickSpacing = config.getFarmSurveyTickSpacing();
	}
	
	
	/**
	 * Starts the worker
	 */
	public void start() {
		enabled = true;
        long tickInterval = surveyPeriodMinutes * TICKS_PER_MINUTE;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SabrePlugin.getPlugin(), this, 0, tickInterval);
	}
	
	
	@Override
	public void run() {
		
		if (!enabled) {
			return;
		}
		
		// Don't start it again if it's not done yet
		if (surveyWorker.isEnabled()) {
			return;
		}
		
		// Create a set of all the running farms to process
		Queue<FarmFactory> runningFarms = new LinkedBlockingQueue<FarmFactory>();
		for (BaseFactory factory : FactoryWorker.getInstance().getRunningFactories()) {
			if (factory instanceof FarmFactory) {
				runningFarms.add((FarmFactory) factory);
			}
		}
		
		// Process the running farms
		if (runningFarms.size() > 0) {
			surveyWorker.start(runningFarms, surveyTickSpacing);
		}
	}
}
