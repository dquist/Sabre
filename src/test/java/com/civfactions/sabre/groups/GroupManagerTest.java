package com.civfactions.sabre.groups;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.BuildMode;
import com.civfactions.sabre.blocks.BuildState;
import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.groups.GroupManager;
import com.civfactions.sabre.groups.Rank;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;
import com.civfactions.sabre.test.MockDataAccess;

public class GroupManagerTest {
	
	private static SabrePlugin plugin;
	private static MockDataAccess db;
	
	private GroupManager gm;
	private PlayerManager pm;
	private BlockManager bm;
	
	private SabrePlayer p1;
	private SabrePlayer p2;
	
	@BeforeClass
	public static void setUpClass() {
		plugin = mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
	}
	
	@Before
	public void setUp() {
		db = spy(new MockDataAccess());
        pm = mock(PlayerManager.class);
        bm = mock(BlockManager.class);
        gm = spy(new GroupManager(plugin, pm, bm, db));
		
		p1 = mock(SabrePlayer.class);
		when(p1.getName()).thenReturn("Player1");
		when(p1.getID()).thenReturn(UUID.randomUUID());
		
		p2 = mock(SabrePlayer.class);
		when(p2.getName()).thenReturn("Player2");
		when(p2.getID()).thenReturn(UUID.randomUUID());
	}
	
	@Test
	public void testGroupManager() {
		Throwable e = null;
		try { new GroupManager(plugin, null, bm, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(plugin, pm, null, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(plugin, pm, bm, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

	@Test
	public void testLoad() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		SabreGroup g2 = gm.createNewGroup(p1, "Group2");
		
		gm.load();
		verify(db, times(1)).groupGetAll();
		assertEquals(gm.getAllGroups().size(), 2);
		assertTrue(gm.getAllGroups().contains(g1));
		assertTrue(gm.getAllGroups().contains(g2));
	}

	@Test
	public void testGetPlayerGroups() {
		Throwable e = null;
		try { gm.getPlayerGroups(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		SabreGroup g2 = gm.createNewGroup(p1, "Group2");
		
		Collection<SabreGroup> groups = gm.getPlayerGroups(p1);
		assertEquals(groups.size(), 2);
		assertTrue(groups.contains(g1));
		assertTrue(groups.contains(g2));
	}

	@Test
	public void testGetInvitedGroups() {
		Throwable e = null;
		try { gm.getInvitedGroups(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		SabreGroup g2 = gm.createNewGroup(p1, "Group2");
		
		assertEquals(gm.getInvitedGroups(p1).size(), 0);
		assertEquals(gm.getInvitedGroups(p2).size(), 0);
		
		g1.addInvited(p2);
		g2.addInvited(p2);
		
		assertEquals(gm.getInvitedGroups(p1).size(), 0);
		assertEquals(gm.getInvitedGroups(p2).size(), 2);
		assertTrue(gm.getInvitedGroups(p2).contains(g1));
		assertTrue(gm.getInvitedGroups(p2).contains(g2));
	}

	@Test
	public void testGetGroupByID() {
		Throwable e = null;
		try { gm.getGroupByID(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		SabreGroup g2 = gm.createNewGroup(p1, "Group2");
		
		assertEquals(gm.getGroupByID(g1.getID()), g1);
		assertEquals(gm.getGroupByID(g2.getID()), g2);
	}

	@Test
	public void testGetGroupByName() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		SabreGroup g2 = gm.createNewGroup(p1, "Group2");
		
		Throwable e = null;
		try { gm.getGroupByName(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.getGroupByName(null, g1.getName()); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		assertEquals(gm.getGroupByName(p1, g1.getName()), g1);
		assertEquals(gm.getGroupByName(p1, g2.getName()), g2);
	}

	@Test
	public void testRemoveGroup() {
		SabreGroup g1 = spy(gm.createNewGroup(p1, "Group1"));
		
		Throwable e = null;
		try { gm.removeGroup(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.removeGroup(g1);
		verify(db, times(1)).groupDelete(g1);
		assertFalse(gm.getAllGroups().contains(g1));
	}

	@Test
	public void testRenameGroup() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		
		Throwable e = null;
		try { gm.renameGroup(null, "test"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.renameGroup(g1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.renameGroup(g1, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		String newName = "updatedName";
		gm.renameGroup(g1, newName);
		verify(db, times(1)).groupUpdateName(g1, newName);
		assertEquals(gm.getGroupByName(p1, newName), g1);
	}

	@Test
	public void testCreateNewGroup() {
		Throwable e = null;
		try { gm.createNewGroup(null, "test"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.createNewGroup(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		SabreGroup g1 = spy(gm.createNewGroup(p1, "Group1"));
		
		assertNotNull(g1);
		assertEquals(g1.getOwner().getPlayer(), p1);
		assertTrue(g1.isMember(p1));
	}

	@Test
	public void testAddRemovePlayer() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		
		Throwable e = null;
		try { gm.addPlayer(null, p1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.addPlayer(g1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		assertFalse(g1.isMember(p2));
		
		gm.addPlayer(g1, p2);
		assertTrue(g1.isMember(p2));
		
		SabreMember member = g1.getMember(p2);
		assertNotNull(member);
		verify(db, times(1)).groupAddMember(g1, member);
		
		// Adding twice throws exception
		e = null;
		try { gm.addPlayer(g1, p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		// Set chat channel to group
		when(p2.getChatChannel()).thenReturn(g1);
		
		BuildState bs = spy(new BuildState());
		bs.setGroup(g1);
		bs.setMode(BuildMode.FORTIFY);
		
		when (p2.getBuildState()).thenReturn(bs);
		
		assertEquals(gm.removePlayer(g1, p2), member);
		assertFalse(g1.isMember(p2));
		verify(db, times(1)).groupRemoveMember(g1, member);
		verify(bs).reset();
		verify(bs).setMode(BuildMode.OFF);
		verify(p2).msg(Lang.blockBuildMode, BuildMode.OFF);
		
		
		// Player is moved to global chat
		verify(p2).moveToGlobalChat();
		verify(p2).msg(Lang.chatMovedGlobal);
		
		// Removing twice throws exception
		e = null;
		try { gm.removePlayer(g1, p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		e = null;
		try { gm.removePlayer(null, p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.removePlayer(g1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

	@Test
	public void testInviteUninvitePlayer() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		
		Throwable e = null;
		try { gm.invitePlayer(null, p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.invitePlayer(g1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.uninvitePlayer(null, p2); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.uninvitePlayer(g1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.uninvitePlayer(g1, p2);
		verify(db, times(0)).groupRemoveInvited(g1, p2.getID());
		
		gm.invitePlayer(g1, p2);
		verify(db, times(1)).groupAddInvited(g1, p2.getID());
		
		gm.uninvitePlayer(g1, p2);
		verify(db, times(1)).groupRemoveInvited(g1, p2.getID());
	}

	@Test
	public void testSetPlayerRank() {
		SabreGroup g1 = gm.createNewGroup(p1, "Group1");
		
		SabreMember member = g1.addMember(p2, Rank.MEMBER);
		assertNotNull(member);
		
		Throwable e = null;
		try { gm.setPlayerRank(null, Rank.OFFICER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.setPlayerRank(member, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.setPlayerRank(member, Rank.ADMIN);
		verify(db, times(1)).groupUpdateMemberRank(g1, member);
	}

	@Test
	public void testCreateNewFaction() {
		Throwable e = null;
		try { gm.createNewFaction(null, "testFaction"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.createNewFaction(p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		SabreFaction f = gm.createNewFaction(p1, "testFaction");
		assertNotNull(f);
		assertTrue(f.isFaction());
		assertEquals(f.getOwner().getPlayer(), p1);
		assertTrue(f.isMember(p1));
		
		SabreFaction f2 = gm.getFactionByName("testFaction");
		assertEquals(f, f2);
	}
}
