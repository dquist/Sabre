package com.civfactions.sabre;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.chat.IChatChannel;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.util.TextUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class, Server.class })
public class SabrePlayerTest {

	private static UUID uid1 = UUID.randomUUID();
	private static UUID uid2 = UUID.randomUUID();
	private static String name1 = "Player1";
	private static String name2 = "Player2";
	
	private static SabrePlugin plugin;
	private static PlayerManager pm;
	private static TextUtil txt;
	private static Date now;
	
	private SabrePlayer p1;
	private SabrePlayer p2;
	
	@BeforeClass
	public static void setUpClass() {
		txt = spy(new TextUtil());
		now = new Date();
		
		pm = mock(PlayerManager.class);
		
		plugin = PowerMockito.mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
		when(plugin.getPlayerManager()).thenReturn(pm);
		when(plugin.txt()).thenReturn(txt);
		when(plugin.now()).thenReturn(now);
	}
	
	@Before
	public void setUp() {
		p1 = spy(new SabrePlayer(plugin, uid1, name1));
		p2 = spy(new SabrePlayer(plugin, uid2, name2));
	}
	
	@Test
	public void testSabrePlayer() {		
		Throwable e = null;
		try { new SabrePlayer(null, UUID.randomUUID(), "name"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		try { new SabrePlayer(plugin, null, "name"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		try { new SabrePlayer(plugin, UUID.randomUUID(), null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		try { new SabrePlayer(plugin, UUID.randomUUID(), ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
	}

	@Test
	public void testGetID() {
		assertEquals(p1.getID(), uid1);
		assertEquals(p2.getID(), uid2);
	}

	@Test
	public void testGetSetName() {
		assertEquals(p1.getName(), name1);
		assertEquals(p2.getName(), name2);
		
		Throwable e = null;
		try { p1.setName(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { p1.setName(""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		String testName = "TestName";
		p1.setName(testName);
		assertEquals(p1.getName(), testName);
	}

	@Test
	public void testGetSetPlayer() {
		assertNull(p1.getPlayer());
		Player mockPlayer = mock(Player.class);
		p1.setPlayer(mockPlayer);
		assertEquals(p1.getPlayer(), mockPlayer);
	}

	@Test
	public void testGetSetFaction() {
		assertNull(p1.getFaction());
		SabreFaction faction = mock(SabreFaction.class);
		p1.setFaction(faction);
		assertEquals(p1.getFaction(), faction);
	}

	@Test
	public void testIsOnline() {
		assertFalse(p1.isOnline());
		Player mockPlayer = mock(Player.class);
		p1.setPlayer(mockPlayer);
		assertFalse(p1.isOnline());
		when(mockPlayer.isOnline()).thenReturn(true);
		assertTrue(p1.isOnline());
	}

	@Test
	public void testGetSetFirstLogin() {
		Throwable e = null;
		try { p1.setFirstLogin(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Date firstLogin = new Date(50000);
		assertEquals(p1.getFirstLogin(), now);
		p1.setFirstLogin(firstLogin);
		assertEquals(p1.getFirstLogin(), firstLogin);
	}

	@Test
	public void testGetSetLastLogin() {
		Throwable e = null;
		try { p1.setLastLogin(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Date lastLogin = new Date(50000);
		assertEquals(p1.getLastLogin(), now);
		p1.setLastLogin(lastLogin);
		assertEquals(p1.getLastLogin(), lastLogin);
	}

	@Test
	public void testGetDaysSinceLastLogin() {
		Date lastLogin = new Date();
		p1.setLastLogin(lastLogin);
		assertEquals(p1.getDaysSinceLastLogin(), 0);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastLogin);
		cal.add(Calendar.DATE, -6);
		lastLogin = cal.getTime();
		p1.setLastLogin(lastLogin);
		assertEquals(p1.getDaysSinceLastLogin(), 5);
		
		cal.setTime(lastLogin);
		cal.add(Calendar.DATE, -395);
		lastLogin = cal.getTime();
		p1.setLastLogin(lastLogin);
		assertEquals(p1.getDaysSinceLastLogin(), 400);
	}

	@Test
	public void testGetSetPlaytime() {
		assertEquals(p1.getPlaytime(), 0);
		p1.setPlaytime(10000);
		assertEquals(p1.getPlaytime(), 10000);
	}

	@Test
	public void testGetSetAutoJoin() {
		assertEquals(p1.getAutoJoin(), false);
		p1.setAutoJoin(true);
		assertEquals(p1.getAutoJoin(), true);
		p1.setAutoJoin(false);
		assertEquals(p1.getAutoJoin(), false);
	}

	@Test
	public void testGetSetChatChannel() {
		Throwable e = null;
		try { p1.setChatChannel(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { p1.setChatChannel(p1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		IChatChannel mockChat = mock(IChatChannel.class);
		
		assertEquals(p1.getChatChannel(), plugin.getGlobalChat());
		p1.setChatChannel(mockChat);
		assertEquals(p1.getChatChannel(), mockChat);
		
		p1.moveToGlobalChat();
		assertEquals(p1.getChatChannel(), plugin.getGlobalChat());
	}

	@Test
	public void testGetSetLastMessaged() {
		assertNull(p1.getLastMessaged());
		p1.setLastMessaged(p2);
		assertEquals(p1.getLastMessaged(), p2);
		p1.setLastMessaged(null);
		assertNull(p1.getLastMessaged());
	}

	@Test
	public void testGetSetBanned() {
		assertEquals(p1.getBanned(), false);
		p1.setBanned(true);
		assertEquals(p1.getBanned(), true);
		p1.setBanned(false);
		assertEquals(p1.getBanned(), false);
	}

	@Test
	public void testGetSetBanMessage() {
		Throwable e = null;
		try { p1.setBanMessage(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		assertEquals(p1.getBanMessage(), "");
		p1.setBanMessage("banned");
		assertEquals(p1.getBanMessage(), "banned");
		p1.setBanMessage("");
		assertEquals(p1.getBanMessage(), "");
	}

	@Test
	public void testMsg() {
		Throwable e = null;
		try { p1.msg(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		String offlineMsg = "<c>Offline message";
		p1.msg(offlineMsg);
		verify(pm).addOfflineMessage(p1, txt.parse(offlineMsg));
		
		Player mockPlayer = mock(Player.class);
		when(mockPlayer.isOnline()).thenReturn(true);
		p1.setPlayer(mockPlayer);
		
		String onlineMsg = "<c>Online message";
		p1.msg(onlineMsg);
		verify(mockPlayer).sendMessage(txt.parse(onlineMsg));
	}

	@Test
	public void testChat() {
		Throwable e = null;
		try { p1.chat(null, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { p1.chat(p2, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Player mockPlayer = mock(Player.class);
		p1.setPlayer(mockPlayer);
		
		String chatMsg = "chat message";
		p1.chat(p2, chatMsg);
		verify(p2).msg(Lang.chatPlayerNowOffline, p1.getName());
		
		when(mockPlayer.isOnline()).thenReturn(true);
		p1.chat(p2, chatMsg);
		assertEquals(p1.getLastMessaged(), p2);
		assertEquals(p2.getLastMessaged(), p1);

		verify(p1).msg("<lp>From %s: %s", p2.getName(), chatMsg);
		verify(p2).msg("<lp>To %s: %s", p1.getName(), chatMsg);
		
		p2.setChatChannel(p1);
		p1.setIgnored(p2, true);
		p1.chat(p2, chatMsg);
		verify(p2).msg(Lang.chatYouAreIgnored, p1.getName());
		verify(p2).msg(Lang.chatMovedGlobal);
		assertEquals(p2.getChatChannel(), plugin.getGlobalChat());
	}

	@Test
	public void testChatMe() {
		Throwable e = null;
		try { p1.chatMe(null, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { p1.chatMe(p2, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Player mock1 = mock(Player.class);
		when(mock1.isOnline()).thenReturn(true);
		p1.setPlayer(mock1);
		
		Player mock2 = mock(Player.class);
		when(mock2.isOnline()).thenReturn(true);
		p2.setPlayer(mock2);
		
		String chatMe = "tips fedora";
		p1.chatMe(p2, chatMe);
		verify(p2).msg("<lp><it>%s %s", p1.getName(), chatMe);
		verify(p1).msg("<lp><it>%s %s", p2.getName(), chatMe);
		
	}

	@Test
	public void testGetDistanceFrom() {
		Throwable e = null;
		try { p1.getDistanceFrom(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Location l1 = mock(Location.class);
		when(l1.getBlockX()).thenReturn(0);
		when(l1.getBlockZ()).thenReturn(0);
		
		Location l2 = mock(Location.class);
		when(l2.getBlockX()).thenReturn(0);
		when(l2.getBlockZ()).thenReturn(10);
		
		Player mock1 = mock(Player.class);
		when(mock1.getLocation()).thenReturn(l1);
		
		Player mock2 = mock(Player.class);
		when(mock2.getLocation()).thenReturn(l2);
		
		p1.setPlayer(mock1);
		p2.setPlayer(mock2);
		
		assertEquals(p1.getDistanceFrom(p2), -1);
		assertEquals(p2.getDistanceFrom(p1), -1);

		when(mock1.isOnline()).thenReturn(true);
		assertEquals(p1.getDistanceFrom(p2), -1);
		assertEquals(p2.getDistanceFrom(p1), -1);

		when(mock2.isOnline()).thenReturn(true);
		assertEquals(p1.getDistanceFrom(p2), 10);
		assertEquals(p2.getDistanceFrom(p1), 10);
	}

	@Test
	public void testAddOfflineMessage() {
		Throwable e = null;
		try { p1.addOfflineMessage(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		assertNotNull(p1.getOfflineMessages());
		assertEquals(p1.getOfflineMessages().size(), 0);
		
		String offlineMsg = "offline message";
		p1.addOfflineMessage(offlineMsg);
		assertEquals(p1.getOfflineMessages().size(), 1);
		assertTrue(p1.getOfflineMessages().contains(offlineMsg));
	}

	@Test
	public void testGetBuildState() {
		assertNotNull(p1.getBuildState());
	}

	@Test
	public void testGetSetAdminBypass() {
		assertEquals(p1.getAdminBypass(), false);
		p1.setAdminBypass(true);
		assertEquals(p1.getAdminBypass(), true);
		p1.setAdminBypass(false);
		assertEquals(p1.getAdminBypass(), false);
	}

	@Test
	public void testGetSetVanished() {
		assertEquals(p1.getVanished(), false);
		p1.setVanished(true);
		assertEquals(p1.getVanished(), true);
		p1.setVanished(false);
		assertEquals(p1.getVanished(), false);
	}

	@Test
	public void testGetSetFreedOffline() {
		assertEquals(p1.getFreedOffline(), false);
		p1.setFreedOffline(true);
		assertEquals(p1.getFreedOffline(), true);
		p1.setFreedOffline(false);
		assertEquals(p1.getFreedOffline(), false);
	}

	@Test
	public void testGetSetBedLocation() {
		Location l = mock(Location.class);
		
		assertEquals(p1.getBedLocation(), null);
		p1.setBedLocation(l);
		assertEquals(p1.getBedLocation(), l);
		
		p1.setBedLocation(null);
		assertEquals(p1.getBedLocation(), null);
	}

	@Test
	public void testIgnored() {
		Throwable e = null;
		try { p1.setIgnored(null, false); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		SabrePlayer p3 = new SabrePlayer(plugin, UUID.randomUUID(), "p3");
		
		assertFalse(p1.isIgnoring(p2));
		assertFalse(p1.isIgnoring(p3));
		p1.setIgnored(p2, true);
		assertTrue(p1.isIgnoring(p2));
		assertFalse(p1.isIgnoring(p3));
		p1.setIgnored(p3, true);
		assertTrue(p1.isIgnoring(p2));
		assertTrue(p1.isIgnoring(p3));
		p1.setIgnored(p2, false);
		assertFalse(p1.isIgnoring(p2));
		assertTrue(p1.isIgnoring(p3));
		p1.setIgnored(p3, false);
		assertFalse(p1.isIgnoring(p2));
		assertFalse(p1.isIgnoring(p3));
	}

	@Test
	public void testBcastPlayers() {
		assertNotNull(p1.getBcastPlayers());
		assertEquals(p1.getBcastPlayers().size(), 0);
		assertNull(p1.getRequestedBcastPlayer());
		
		p1.setRequestedBcastPlayer(p2);
		assertEquals(p1.getRequestedBcastPlayer(), p2);
		
		p1.setRequestedBcastPlayer(null);
		assertEquals(p1.getRequestedBcastPlayer(), null);
	}

	@Test
	public void testIsAdmin() {
		assertFalse(p1.isAdmin());
		
		Player mockPlayer = mock(Player.class);
		when(mockPlayer.isOnline()).thenReturn(true);
		
		p1.setPlayer(mockPlayer);
		assertFalse(p1.isAdmin());
		
		when(mockPlayer.hasPermission(SabrePlayer.ADMIN_NODE)).thenReturn(true);
		assertTrue(p1.isAdmin());
	}

	@Test
	public void testTeleport() {
		Throwable e = null;
		try { p1.teleport(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		Player mockPlayer = mock(Player.class);
		p1.setPlayer(mockPlayer);
		Location l = mock(Location.class);
		
		assertFalse(p1.teleport(l));
		verify(mockPlayer, never()).teleport(any(Location.class));
		
		when(mockPlayer.isOnline()).thenReturn(true);
		when(mockPlayer.teleport(any(Location.class))).thenReturn(true);
		assertTrue(p1.teleport(l));
		verify(mockPlayer).teleport(l);
		
	}
}
