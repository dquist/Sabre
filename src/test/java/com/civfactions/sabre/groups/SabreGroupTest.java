package com.civfactions.sabre.groups;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import java.util.UUID;

import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.groups.Rank;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;
import com.civfactions.sabre.util.TextUtil;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class })
public class SabreGroupTest {
	
	private static SabrePlugin plugin;
	private static TextUtil txt;
	
	private static SabreGroup group;
	
	private static String groupName = "TestGroup";
	
	private static String p1name = "Player1";
	private static String p2name = "Player2";
	private static String p3name = "Player3";
	
	private static SabrePlayer p1;
	private static SabrePlayer p2;
	private static SabrePlayer p3;
	
	private static String testMsg = "Test chat message";
	private static String testSnitchMsg = "Test snitch message";
	
	@BeforeClass
	public static void setUpClass() {
		txt = spy(new TextUtil());
		
		plugin = PowerMockito.mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
		when(plugin.getPlayerManager()).thenReturn(mock(PlayerManager.class));
		when(plugin.txt()).thenReturn(txt);
	}
	
	@Before
	public void setUp() {
		p1 = spy(new SabrePlayer(plugin, UUID.randomUUID(), p1name));
		p2 = spy(new SabrePlayer(plugin, UUID.randomUUID(), p2name));
		p3 = spy(new SabrePlayer(plugin, UUID.randomUUID(), p3name));
		
		group = new SabreGroup(plugin, UUID.randomUUID(), groupName);
		group.addMember(p1, Rank.OWNER);
		
	}
	
	@Test
	public void testGroup() {
		
		Throwable e = null;
		try { new SabreGroup(null, UUID.randomUUID(), "group"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreGroup(plugin, null, "group"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreGroup(plugin, UUID.randomUUID(), null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreGroup(plugin, UUID.randomUUID(), ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		assertFalse(group.isFaction());
	}
	
	@Test
	public void testGetSetName() {
		Throwable e = null;
		try { group.setName(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { group.setName(""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		String oldName = group.getName();
		String testName = "NewName";
		
		group.setName(testName);
		assertEquals(group.getName(), testName);
		group.setName(oldName);
		assertEquals(group.getName(), oldName);
		
		// Full name
		assertEquals(group.getFullName(), String.format("%s#%s", group.getName(), group.getOwner().getName()));
	}

	@Test
	public void testMembers() {
		
		group.addMember(p2, Rank.ADMIN);
		
		Throwable e = null;
		try { group.addMember(p2, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		
		group.addMember(p3, Rank.MEMBER);
		assertTrue(group.isMember(p3));
		assertFalse(group.isInvited(p3));
		assertFalse(group.isBuilder(p3));
		
		group.removePlayer(p3);
		assertFalse(group.isMember(p3));
		assertFalse(group.isInvited(p3));
		assertFalse(group.isBuilder(p3));
		assertFalse(group.isOfficer(p3));
		
		group.addMember(p3, Rank.MEMBER);
		assertTrue(group.isMember(p3));
		assertFalse(group.isBuilder(p3));
		assertFalse(group.isOfficer(p3));
		
		SabreMember p3Member = group.getMember(p3);
		assertNotNull(p3Member);
		assertTrue(group.isRank(p3, Rank.MEMBER));
		assertFalse(group.isRank(p3, Rank.OFFICER));
		
		p3Member.setRank(Rank.BUILDER);
		assertTrue(group.isBuilder(p3));
		assertFalse(group.isOfficer(p3));
		assertTrue(group.isRank(p3, Rank.BUILDER));
		
		p3Member.setRank(Rank.OFFICER);
		assertTrue(group.isBuilder(p3));
		assertTrue(group.isOfficer(p3));
		assertTrue(group.isRank(p3, Rank.OFFICER));
		
		p3Member.setRank(Rank.ADMIN);
		assertTrue(group.isBuilder(p3));
		assertTrue(group.isOfficer(p3));
		assertTrue(group.isRank(p3, Rank.ADMIN));
		
		group.removeMember(p3Member);
		assertFalse(group.isMember(p3));
		
		assertFalse(group.isInvited(p3));
		
		group.addInvited(p3);
		assertTrue(group.isInvited(p3));
		
		group.removeInvited(p3);
		assertFalse(group.isInvited(p3));
	}
	
	@Test
	public void testMessage() {
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.getPlayer()).thenReturn(mock(Player.class));
		when(p1.isOnline()).thenReturn(true);
		
		when(p2.getPlayer()).thenReturn(mock(Player.class));
		when(p2.isOnline()).thenReturn(true);
		
		when(p3.getPlayer()).thenReturn(mock(Player.class));
		when(p3.isOnline()).thenReturn(true);
		
		group.msgAll(testMsg, true);
		for(SabreMember member : group.getMembers()) {
			verify(member.getPlayer()).msg(testMsg);
		}
	}
	
	@Test
	public void testMute() {
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.getPlayer()).thenReturn(mock(Player.class));
		when(p1.isOnline()).thenReturn(true);
		
		when(p2.getPlayer()).thenReturn(mock(Player.class));
		when(p2.isOnline()).thenReturn(true);
		
		when(p3.getPlayer()).thenReturn(mock(Player.class));
		when(p3.isOnline()).thenReturn(true);
		
		assertFalse(group.isChatMutedBy(p1));
		group.setChatMutedBy(p1, true);
		assertTrue(group.isChatMutedBy(p1));
		
		group.msgAll(testMsg, true);
		verify(p1, never()).msg(testMsg);
		verify(p2).msg(testMsg);
		
		// unmute
		group.setChatMutedBy(p1, false);
		assertFalse(group.isChatMutedBy(p1));
		
		group.msgAll(testMsg, true);
		verify(p1).msg(testMsg);
	}
	
	@Test
	public void testSnitchMute() {
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.getPlayer()).thenReturn(mock(Player.class));
		when(p1.isOnline()).thenReturn(true);
		
		when(p2.getPlayer()).thenReturn(mock(Player.class));
		when(p2.isOnline()).thenReturn(true);
		
		when(p3.getPlayer()).thenReturn(mock(Player.class));
		when(p3.isOnline()).thenReturn(true);
		
		assertFalse(group.isSnitchMutedBy(p1));
		group.setSnitchMutedBy(p1, true);
		assertTrue(group.isSnitchMutedBy(p1));
		
		group.msgAllSnitch(testSnitchMsg, true);
		verify(p1, never()).msg(testSnitchMsg);
		verify(p2).msg(testSnitchMsg);
		
		// unmute
		group.setSnitchMutedBy(p1, false);
		assertFalse(group.isSnitchMutedBy(p1));
		
		group.msgAllSnitch(testSnitchMsg, true);
		verify(p1).msg(testSnitchMsg);
	}
	
	@Test
	public void testGroupExceptions() {
		
		Throwable e = null;
		try { group.getMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.addMember(null, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.addMember(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.removePlayer(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.removeMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.addInvited((UUID)null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.removeInvited((UUID)null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.removeInvited((SabrePlayer)null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isInvited(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isBuilder(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isOfficer(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isRank(null, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isRank(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.setOwner(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.msgAll(null, false); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.msgAllBut(null, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.msgAllBut(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.setChatMutedBy(null, true); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isChatMutedBy(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.setSnitchMutedBy(null, true); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isSnitchMutedBy(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}
}
