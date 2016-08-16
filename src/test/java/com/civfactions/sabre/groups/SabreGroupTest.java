package com.civfactions.sabre.groups;
import org.apache.commons.lang.NullArgumentException;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.UUID;

import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.groups.Rank;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;

import static org.mockito.Mockito.*;

public class SabreGroupTest {
	
	private static SabrePlugin plugin;
	
	private SabrePlayer p1;
	private SabrePlayer p2;
	private SabrePlayer p3;
	private SabreGroup group;
	
	private static String testMsg = "Test chat message";
	private static String testSnitchMsg = "<c>%s <i>entered snitch.";
	
	@BeforeClass
	public static void setUpClass() {		
		plugin = mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
	}
	
	@Before
	public void setUp() {
		p1 = mock(SabrePlayer.class);
		when(p1.getName()).thenReturn("Player1");
		when(p1.getID()).thenReturn(UUID.randomUUID());
		
		p2 = mock(SabrePlayer.class);
		when(p2.getName()).thenReturn("Player2");
		when(p2.getID()).thenReturn(UUID.randomUUID());
		
		p3 = mock(SabrePlayer.class);
		when(p3.getName()).thenReturn("Player3");
		when(p3.getID()).thenReturn(UUID.randomUUID());
		
		group = new SabreGroup(plugin, UUID.randomUUID(), "testGroup");
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
		
		group.addMember(p2, Rank.ADMIN);
		
		// Can't add twice
		e = null;
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
	}
	
	@Test
	public void testInviteUninvite() {
		Throwable e = null;
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
		
		assertFalse(group.isInvited(p1));
		assertFalse(group.isInvited(p3));
		
		group.addInvited(p2);
		assertTrue(group.isInvited(p2));
		assertFalse(group.isInvited(p3));
		
		group.addInvited(p3);
		assertTrue(group.isInvited(p2));
		assertTrue(group.isInvited(p3));
		
		group.removeInvited(p2);
		assertFalse(group.isInvited(p2));
		assertTrue(group.isInvited(p3));
		
		group.removeInvited(p3);
		assertFalse(group.isInvited(p2));
		assertFalse(group.isInvited(p3));
	}
	
	@Test
	public void testMsgAll() {
		Throwable e = null;
		try { group.msgAll(null, false); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.isOnline()).thenReturn(true);
		when(p2.isOnline()).thenReturn(true);
		when(p3.isOnline()).thenReturn(true);
		
		group.msgAll(testMsg, true);
		for(SabreMember member : group.getMembers()) {
			verify(member.getPlayer()).msg(testMsg);
		}
	}
	
	@Test
	public void testMsgAllBut() {
		Throwable e = null;
		try { group.msgAllBut(null, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.msgAllBut(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.isOnline()).thenReturn(true);
		when(p2.isOnline()).thenReturn(true);
		when(p3.isOnline()).thenReturn(true);
		
		group.msgAllBut(p3, testMsg);
		verify(p1).msg(testMsg);
		verify(p2).msg(testMsg);
		verify(p3, never()).msg(testMsg);
	}
	
	@Test
	public void testChatMute() {
		Throwable e = null;
		try { group.setChatMutedBy(null, true); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isChatMutedBy(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.isOnline()).thenReturn(true);
		when(p2.isOnline()).thenReturn(true);
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
		Throwable e = null;
		try { group.setSnitchMutedBy(null, true); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.isSnitchMutedBy(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		group.addMember(p2, Rank.OFFICER);
		group.addMember(p3, Rank.MEMBER);
		
		when(p1.isOnline()).thenReturn(true);
		when(p2.isOnline()).thenReturn(true);
		when(p3.isOnline()).thenReturn(true);
		
		assertFalse(group.isSnitchMutedBy(p1));
		group.setSnitchMutedBy(p1, true);
		assertTrue(group.isSnitchMutedBy(p1));
		
		group.msgAllSnitch(testSnitchMsg, p1);
		verify(p1, never()).msg(testSnitchMsg);
		verify(p2).msg(testSnitchMsg, p1);
		
		// unmute
		group.setSnitchMutedBy(p1, false);
		assertFalse(group.isSnitchMutedBy(p1));
		
		group.msgAllSnitch(testSnitchMsg, p1);
		verify(p1).msg(testSnitchMsg, p1);
	}
	
	@Test
	public void testTransferTo() {
		Throwable e = null;
		try { group.transferTo(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		assertTrue(group.isMember(p1));
		group.transferTo(p2);
		assertTrue(group.isMember(p1));
		assertTrue(group.isMember(p2));
		assertTrue(group.getMember(p2).getRank() == Rank.OWNER);
		assertTrue(group.getMember(p1).getRank() == Rank.ADMIN);
		
		// Can't transfer to self
		e = null;
		try { group.transferTo(p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
	}
}
