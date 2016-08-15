package com.civfactions.sabre;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.test.MockDataAccess;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class })
public class PlayerManagerTest {
	
	private static SabrePlugin plugin;
	private static MockDataAccess db;
	
	private static String p1name = "Player1";
	private static String p2name = "Player2";
	
	private static SabrePlayer p1;
	private static SabrePlayer p2;
	
	private PlayerManager pm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		plugin = PowerMockito.mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
		when(plugin.getPlayerManager()).thenReturn(mock(PlayerManager.class));
		
		db = spy(new MockDataAccess());
	}

	@Before
	public void setUp() throws Exception {
		pm = spy(new PlayerManager(plugin, db));
		
		Player mock1 = mock(Player.class);
		when(mock1.getName()).thenReturn(p1name);
		when(mock1.getUniqueId()).thenReturn(UUID.randomUUID());
		
		Player mock2 = mock(Player.class);
		when(mock2.getName()).thenReturn(p2name);
		when(mock2.getUniqueId()).thenReturn(UUID.randomUUID());
		
		p1 = pm.createNewPlayer(mock1);
		p2 = pm.createNewPlayer(mock2);
	}

	@Test
	public void testPlayerManager() {
		Throwable e = null;
		try { new PlayerManager(null, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		try { new PlayerManager(plugin, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

	@Test
	public void testLoad() {
		db.players.add(p1);
		pm.load();
		verify(db).playerGetAll();
		assertTrue(pm.getPlayers().contains(p1));
		
		db.players.clear();
		db.players.add(p2);
		pm.load();
		assertFalse(pm.getPlayers().contains(p1));
		assertTrue(pm.getPlayers().contains(p2));
	}

	@Test
	public void testRemovePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayerById() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayerByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateNewPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testOnPlayerConnect() {
		fail("Not yet implemented");
	}

	@Test
	public void testOnPlayerDisconnect() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPlayers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetOnlinePlayers() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLastLogin() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAutoJoin() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDisplayName() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBanStatus() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetFreedOffline() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBedLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddOfflineMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testClearOfflineMessages() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrintOfflineMessages() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPlayTime() {
		fail("Not yet implemented");
	}

}
