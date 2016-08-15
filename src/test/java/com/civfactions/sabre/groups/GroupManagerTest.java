package com.civfactions.sabre.groups;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.groups.GroupManager;
import com.civfactions.sabre.groups.Rank;
import com.civfactions.sabre.groups.SabreFaction;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;
import com.civfactions.sabre.test.MockDataAccess;
import com.civfactions.sabre.test.MockPlayer;
import com.civfactions.sabre.test.MockWorld;
import com.civfactions.sabre.test.TestFixture;
import com.comphenix.protocol.ProtocolLibrary;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class, PluginDescriptionFile.class, ProtocolLibrary.class })
public class GroupManagerTest {
	
	private static TestFixture fixture;
	private static SabrePlugin plugin;
	private static MockDataAccess db;
	private static GroupManager gm;
	
	private SabreGroup group1;
	private SabreGroup group2;
	private SabreGroup group3;
	
	private SabrePlayer playerOwner;
	private SabrePlayer player1;
	private SabrePlayer player2;
	
	@BeforeClass
	public static void setUpClass() {
		fixture = new TestFixture();
        assertTrue(fixture.setUp());
        plugin = fixture.getPlugin();
		
        db = (MockDataAccess)plugin.getDataAccess();
        
        PlayerManager pm = mock(PlayerManager.class);
        BlockManager bm = mock(BlockManager.class);

		Throwable e = null;
		try { new GroupManager(plugin, null, bm, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(plugin, pm, null, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(plugin, pm, bm, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
        
        gm = new GroupManager(plugin, pm, bm, db);
	}
	
	@AfterClass
	public static void tearDown() {
		fixture.tearDown();
	}
	
	@Before
	public void setUp() {
		db.groups.clear();
		
		playerOwner = mock(SabrePlayer.class);
		when(playerOwner.getName()).thenReturn("playerOwner");
		when(playerOwner.getID()).thenReturn(UUID.randomUUID());
		
		player1 = mock(SabrePlayer.class);
		when(player1.getName()).thenReturn("Player1");
		when(player1.getID()).thenReturn(UUID.randomUUID());
		
		player2 = mock(SabrePlayer.class);
		when(player2.getName()).thenReturn("Player2");
		when(player2.getID()).thenReturn(UUID.randomUUID());

		group1 = mock(SabreGroup.class);
		when(group1.getName()).thenReturn("Group1");
		when(group1.getID()).thenReturn(UUID.randomUUID());
		when(group1.getOwner()).thenReturn(playerOwner);
		
		group2 = mock(SabreGroup.class);
		when(group2.getName()).thenReturn("Group2");
		when(group2.getID()).thenReturn(UUID.randomUUID());
		when(group2.getOwner()).thenReturn(playerOwner);
		
		group3 = mock(SabreGroup.class);
		when(group3.getName()).thenReturn("Group3");
		when(group3.getID()).thenReturn(UUID.randomUUID());
		when(group3.getOwner()).thenReturn(playerOwner);
		
		db.groups.add(group1);
		db.groups.add(group2);
		db.groups.add(group3);
		assertEquals(db.groups.size(), 3);
		
		reset(db);
	}

	@Test
	public void testLoad() {
		gm.load();
		verify(db, times(1)).groupGetAll();
	}

	@Test
	public void testGetAllGroups() {
		gm.load();
		
		Collection<SabreGroup> groups = gm.getAllGroups();
		assertEquals(groups.size(), 3);
	}

	@Test
	public void testGetPlayerGroups() {
		Throwable e = null;
		try { gm.getPlayerGroups(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		SabrePlayer sp = mock(SabrePlayer.class);
		
		when(group1.isMember(sp)).thenReturn(true);
		when(group2.isMember(sp)).thenReturn(true);
		gm.load();
		
		Collection<SabreGroup> groups = gm.getPlayerGroups(sp);
		assertEquals(groups.size(), 2);
		assertTrue(groups.contains(group1));
		assertTrue(groups.contains(group2));
	}

	@Test
	public void testGetInvitedGroups() {
		Throwable e = null;
		try { gm.getInvitedGroups(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.load();
		
		assertEquals(gm.getInvitedGroups(player1).size(), 0);
		assertEquals(gm.getInvitedGroups(player2).size(), 0);
		
		when(group1.isInvited(player1)).thenReturn(true);
		when(group2.isInvited(player2)).thenReturn(true);
		when(group3.isInvited(player2)).thenReturn(true);
		
		assertEquals(gm.getInvitedGroups(player1).size(), 1);
		assertEquals(gm.getInvitedGroups(player2).size(), 2);
	}

	@Test
	public void testGetGroupByID() {
		Throwable e = null;
		try { gm.getGroupByID(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.load();
		assertEquals(gm.getGroupByID(group1.getID()), group1);
		assertEquals(gm.getGroupByID(group2.getID()), group2);
		assertEquals(gm.getGroupByID(group3.getID()), group3);
	}

	@Test
	public void testGetGroupByName() {
		Throwable e = null;
		try { gm.getGroupByName(playerOwner, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.getGroupByName(null, group1.getName()); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.load();
		assertEquals(gm.getGroupByName(playerOwner, group1.getName()), group1);
		assertEquals(gm.getGroupByName(playerOwner, group2.getName()), group2);
		assertEquals(gm.getGroupByName(playerOwner, group3.getName()), group3);
	}

	@Test
	public void testRemoveGroup() {
		Throwable e = null;
		try { gm.removeGroup(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.load();
		gm.removeGroup(group1);
		verify(db, times(1)).groupDelete(group1);
		assertFalse(db.groups.contains(group1));
		assertFalse(gm.getAllGroups().contains(group1));
	}

	@Test
	public void testRenameGroup() {
		Throwable e = null;
		try { gm.renameGroup(null, "test"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.renameGroup(group1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		gm.load();
		
		group1 = gm.createNewGroup(playerOwner, "testGroup");
		
		e = null;
		try { gm.renameGroup(group1, ""); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		String newName = "updatedName";
		gm.renameGroup(group1, newName);
		verify(db, times(1)).groupUpdateName(group1, newName);
		assertEquals(gm.getGroupByName(playerOwner, newName), group1);
	}

	@Test
	public void testCreateNewGroup() {
		Throwable e = null;
		try { gm.createNewGroup(null, "test"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.createNewGroup(player1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		gm.load();
		
		SabreGroup g = gm.createNewGroup(player1, "newGroup");
		assertNotNull(g);
		assertEquals(g.getOwner(), player1);
		assertTrue(g.isMember(player1));
	}

	@Test
	public void testAddRemovePlayer() {
		MockWorld overWorld = fixture.getWorld(plugin.config().getFreeWorldName());
		group1 = gm.createNewGroup(playerOwner, "testGroup");
		MockPlayer testPlayer = MockPlayer.create(overWorld, "testPlayer");
		testPlayer.isOnline = true;
		player1 = plugin.getPlayerManager().createNewPlayer(testPlayer);
		
		Throwable e = null;
		try { gm.addPlayer(null, player1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.addPlayer(group1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		gm.load();
		
		assertFalse(group1.isMember(player1));
		gm.addPlayer(group1, player1);
		assertTrue(group1.isMember(player1));
		SabreMember member = group1.getMember(player1);
		assertNotNull(member);
		verify(db, times(1)).groupAddMember(group1, member);
		
		// Adding twice throws exception
		e = null;
		try { gm.addPlayer(group1, player1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		player1.setChatChannel(group1);
		
		assertEquals(gm.removePlayer(group1, player1), member);
		assertFalse(group1.isMember(player1));
		verify(db, times(1)).groupRemoveMember(group1, member);
		
		// Player is moved to global chat
		assertEquals(player1.getChatChannel(), plugin.getGlobalChat());
		assertEquals(testPlayer.messages.poll(), plugin.txt().parse(Lang.chatMovedGlobal));
		
		// Removing twice throws exception
		e = null;
		try { gm.removePlayer(group1, player1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
	}

	@Test
	public void testInvitePlayer() {
		Throwable e = null;
		try { gm.invitePlayer(null, player1); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.invitePlayer(group1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		group1 = gm.createNewGroup(playerOwner, "testGroup");
		gm.load();
		
		gm.uninvitePlayer(group1, player1);
		verify(db, times(0)).groupRemoveInvited(group1, player1.getID());
		
		gm.invitePlayer(group1, player1);
		verify(db, times(1)).groupAddInvited(group1, player1.getID());
		
		gm.uninvitePlayer(group1, player1);
		verify(db, times(1)).groupRemoveInvited(group1, player1.getID());
	}

	@Test
	public void testSetPlayerRank() {

		gm.load();
		group1 = gm.createNewGroup(playerOwner, "TestGroup");
		SabreMember member = group1.getMember(playerOwner);
		assertNotNull(member);
		
		Throwable e = null;
		try { gm.setPlayerRank(null, Rank.OFFICER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.setPlayerRank(member, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		gm.setPlayerRank(member, Rank.ADMIN);
		verify(db, times(1)).groupUpdateMemberRank(group1, member);
	}

	@Test
	public void testCreateNewFaction() {
		Throwable e = null;
		try { gm.createNewFaction(null, "testFaction"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.createNewFaction(player1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		gm.load();
		
		SabreFaction f = gm.createNewFaction(player1, "testFaction");
		assertNotNull(f);
		assertTrue(f.isFaction());
		assertEquals(f.getOwner(), player1);
		assertTrue(f.isMember(player1));
		
		SabreFaction f2 = gm.getFactionByName("testFaction");
		assertEquals(f, f2);
	}
}
