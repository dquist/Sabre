package com.gordonfreemanq.sabre.groups;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.comphenix.protocol.ProtocolLibrary;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.util.MockDataAccess;
import com.gordonfreemanq.sabre.util.TestFixture;

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
		try { new GroupManager(null, bm, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(pm, null, db); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new GroupManager(pm, bm, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
        
        gm = new GroupManager(pm, bm, db);
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
	public void testAddGroup() {
		SabreGroup addGroup = mock(SabreGroup.class);
		when(addGroup.getID()).thenReturn(UUID.randomUUID());
		when(addGroup.getOwner()).thenReturn(player1);
		when(addGroup.getName()).thenReturn("addGroup");
		
		Throwable e = null;
		try { gm.addGroup(player1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { gm.addGroup(null, addGroup); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		gm.load();
		
		gm.addGroup(player1, addGroup);
		verify(db, times(1)).groupInsert(addGroup);
		
		e = null;
		try { gm.addGroup(player1, addGroup); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
	}

	@Test
	public void testGetGroupByID() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGroupByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFactionByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testRenameGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateNewGroup() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateNewFaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemovePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testInvitePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testUninvitePlayer() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPlayerRank() {
		fail("Not yet implemented");
	}

}
