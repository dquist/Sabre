package com.civfactions.sabre;

import static org.junit.Assert.*;

import org.bukkit.Server;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.civfactions.sabre.test.MockScheduler;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class })
public class StatsTrackerTest {
	
	private static MockScheduler scheduler;
	private static StatsTracker tracker;
	private static PlayerManager pm;
	
	@BeforeClass
	public static void setUpClass() {
		
		scheduler = MockScheduler.create();
		Server server = mock(Server.class);
		when(server.getScheduler()).thenReturn(scheduler);
		
		// Need to use PowerMock because getServer() is a final method
		SabrePlugin plugin = PowerMockito.mock(SabrePlugin.class);
		when(plugin.getServer()).thenReturn(server);
		
		pm = mock(PlayerManager.class);
		
		ArrayList<SabrePlayer> onlinePlayers = new ArrayList<SabrePlayer>();
		onlinePlayers.add(mock(SabrePlayer.class));
		onlinePlayers.add(mock(SabrePlayer.class));
		when(pm.getOnlinePlayers()).thenReturn(onlinePlayers);
		
        tracker = spy(new StatsTracker(plugin, pm));
	}

	@Test
	public void test() {
		tracker.start();
		assertTrue("Stats Tracker is started", tracker.isEnabled());
		
		verify(tracker, times(0)).run();
		verify(pm, never()).addPlayTime(any(SabrePlayer.class), anyLong());
		scheduler.doTicks(1);
		verify(tracker, times(1)).run();
		verify(pm, times(2)).addPlayTime(any(SabrePlayer.class), anyLong());
		
		scheduler.doTicks(StatsTracker.RUN_PERIOD_TICKS - 1);
		verify(tracker, times(1)).run();
		
		scheduler.doTicks(1);
		verify(tracker, times(2)).run();
		verify(pm, times(4)).addPlayTime(any(SabrePlayer.class), anyLong());

		tracker.stop();
		assertFalse("Stats Tracker is disabled", tracker.isEnabled());

		scheduler.doTicks(StatsTracker.RUN_PERIOD_TICKS - 1);
		verify(tracker, times(2)).run();
		verify(pm, times(4)).addPlayTime(any(SabrePlayer.class), anyLong());
	}
}
