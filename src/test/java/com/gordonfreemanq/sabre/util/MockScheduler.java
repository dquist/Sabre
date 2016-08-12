package com.gordonfreemanq.sabre.util;

import static org.mockito.Mockito.mock;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.Mockito;

public abstract class MockScheduler implements BukkitScheduler {

	public static MockScheduler create() {
		MockScheduler s = mock(MockScheduler.class, Mockito.CALLS_REAL_METHODS);
		return s;
	}
	
	@Override
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task, long delay) {
		task.run();
		return 0;
	}
	
	@Override
	public int scheduleSyncDelayedTask(Plugin plugin, Runnable task) {
		task.run();
		return 0;
	}
}
