package com.gordonfreemanq.sabre;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.comphenix.protocol.ProtocolLibrary;
import com.gordonfreemanq.sabre.PlayerListener;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.util.MockPlayer;
import com.gordonfreemanq.sabre.util.MockWorld;
import com.gordonfreemanq.sabre.util.TestFixture;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class, PluginDescriptionFile.class, ProtocolLibrary.class })
public class SabreGroupTest {
	
	private static String OWNER_NAME = "PlayerOwner";
	private static String ADMIN_NAME = "PlayerAdmin";
	private static String OFFICER_NAME = "PlayerOfficer";
	private static String BUILDER_NAME = "PlayerBuilder";
	private static String MEMBER_NAME = "PlayerMember";
	private static String groupName = "TestGroup";

	private static TestFixture fixture;
	private static SabrePlugin plugin;
	private static PlayerManager pm;
	private static GroupManager gm;
	private static PlayerListener pl;
	
	private static SabreGroup group;
	private static HashMap<SabrePlayer, Rank> groupMembers;
	private static SabrePlayer groupOwner;
	
	@BeforeClass
	public static void setUp() {		
		fixture = new TestFixture();
        assertTrue(fixture.setUp());
        plugin = fixture.getPlugin();
        pl = plugin.getPlayerListener();
        pm = plugin.getPlayerManager();
        gm = plugin.getGroupManager();
		MockWorld overWorld = fixture.getWorld(plugin.config().getFreeWorldName());
		
		// Add some players to the server
		SabrePluginTest.newPlayerJoinServer(overWorld, pl, OWNER_NAME);
		SabrePluginTest.newPlayerJoinServer(overWorld, pl, ADMIN_NAME);
		SabrePluginTest.newPlayerJoinServer(overWorld, pl, OFFICER_NAME);
		SabrePluginTest.newPlayerJoinServer(overWorld, pl, BUILDER_NAME);
		SabrePluginTest.newPlayerJoinServer(overWorld, pl, MEMBER_NAME);
		verify(pm, times(5)).createNewPlayer(any(Player.class));
        
		// Create the group
		groupOwner = pm.getPlayerByName(OWNER_NAME);
		assertNotNull(groupOwner);
		group = gm.createNewGroup(groupOwner, groupName);
		
		// Add members to the group
		groupMembers = new HashMap<SabrePlayer, Rank>();
		
		groupMembers.put(pm.getPlayerByName(ADMIN_NAME), Rank.ADMIN);
		groupMembers.put(pm.getPlayerByName(OFFICER_NAME), Rank.OFFICER);
		groupMembers.put(pm.getPlayerByName(BUILDER_NAME), Rank.BUILDER);
		groupMembers.put(pm.getPlayerByName(MEMBER_NAME), Rank.MEMBER);
		
		for(Entry<SabrePlayer, Rank> entry : groupMembers.entrySet()) {
			group.addMember(entry.getKey(), entry.getValue());
		}

		groupMembers.put(groupOwner, Rank.OWNER);
	}
	

	@AfterClass
	public static void tearDown() {
		fixture.tearDown();
	}
	
	@Test
	public void testGroupManager() {
		
		
		
	}
	
	@Test
	public void testGroup(){
		assertFalse(group.isFaction());
	}

	@Test
	public void testGetSetName() {
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
		
		Collection<SabreMember> members = group.getMembers();
		assertEquals(members.size(), groupMembers.size());
		
		for(SabreMember member : members) {
			assertEquals(groupMembers.get(member.getPlayer()), member.getRank());
		}
		
		SabrePlayer testPlayer = new SabrePlayer(UUID.randomUUID(), "TestPlayer");
		assertFalse(group.isMember(testPlayer));
		assertFalse(group.isInvited(testPlayer));
		
		group.addMember(testPlayer, Rank.MEMBER);
		assertTrue(group.isMember(testPlayer));
		assertFalse(group.isInvited(testPlayer));
		assertFalse(group.isBuilder(testPlayer));
		
		group.removePlayer(testPlayer);
		assertFalse(group.isMember(testPlayer));
		assertFalse(group.isInvited(testPlayer));
		assertFalse(group.isBuilder(testPlayer));
		assertFalse(group.isOfficer(testPlayer));
		
		group.addMember(testPlayer, Rank.MEMBER);
		assertTrue(group.isMember(testPlayer));
		assertFalse(group.isBuilder(testPlayer));
		assertFalse(group.isOfficer(testPlayer));
		
		SabreMember testMember = group.getMember(testPlayer);
		assertNotNull(testMember);
		assertTrue(group.isRank(testPlayer, Rank.MEMBER));
		assertFalse(group.isRank(testPlayer, Rank.OFFICER));
		
		testMember.setRank(Rank.BUILDER);
		assertTrue(group.isBuilder(testPlayer));
		assertFalse(group.isOfficer(testPlayer));
		assertTrue(group.isRank(testPlayer, Rank.BUILDER));
		
		testMember.setRank(Rank.OFFICER);
		assertTrue(group.isBuilder(testPlayer));
		assertTrue(group.isOfficer(testPlayer));
		assertTrue(group.isRank(testPlayer, Rank.OFFICER));
		
		testMember.setRank(Rank.ADMIN);
		assertTrue(group.isBuilder(testPlayer));
		assertTrue(group.isOfficer(testPlayer));
		assertTrue(group.isRank(testPlayer, Rank.ADMIN));
		
		group.removeMember(testMember);
		assertFalse(group.isMember(testPlayer));
		
		assertFalse(group.isInvited(testPlayer));
		
		group.addInvited(testPlayer);
		assertTrue(group.isInvited(testPlayer));
		
		group.removeInvited(testPlayer);
		assertFalse(group.isInvited(testPlayer));
	}
	
	@Test
	public void testMessaging() {
		
		// Clear existing messages
		for(SabreMember member : group.getMembers()) {
			((MockPlayer)member.getPlayer().getPlayer()).messages.clear();
		}
		
		String testMsg = "Test chat message";
		String testSnitchMsg = "Test snitch message";
		
		group.msgAll(testMsg, true);
		for(SabreMember member : group.getMembers()) {
			assertEquals(((MockPlayer)member.getPlayer().getPlayer()).messages.poll(), testMsg);
		}
		
		// Mute all
		for(SabreMember member : group.getMembers()) {
			group.setChatMutedBy(member.getPlayer(), true);
		}
		
		group.msgAll(testMsg, true);
		for(SabreMember member : group.getMembers()) {
			assertNull(((MockPlayer)member.getPlayer().getPlayer()).messages.poll());
		}
		
		group.msgAll(testMsg, false);
		for(SabreMember member : group.getMembers()) {
			assertEquals(((MockPlayer)member.getPlayer().getPlayer()).messages.poll(), testMsg);
		}
		
		// Unmute all
		for(SabreMember member : group.getMembers()) {
			group.setChatMutedBy(member.getPlayer(), false);
		}
		
		group.msgAll(testMsg, true);
		for(SabreMember member : group.getMembers()) {
			assertEquals(((MockPlayer)member.getPlayer().getPlayer()).messages.poll(), testMsg);
		}
		
		group.msgAllSnitch(testSnitchMsg);
		for(SabreMember member : group.getMembers()) {
			assertEquals(((MockPlayer)member.getPlayer().getPlayer()).messages.poll(), testSnitchMsg);
		}
		
		// Unmute all
		for(SabreMember member : group.getMembers()) {
			group.setSnitchMutedBy(member.getPlayer(), true);
		}
		
		group.msgAllSnitch(testSnitchMsg);
		for(SabreMember member : group.getMembers()) {
			assertNull(((MockPlayer)member.getPlayer().getPlayer()).messages.poll());
		}
		
		// Unmute all
		for(SabreMember member : group.getMembers()) {
			group.setSnitchMutedBy(member.getPlayer(), false);
		}
		
		group.msgAllSnitch(testSnitchMsg);
		for(SabreMember member : group.getMembers()) {
			assertEquals(((MockPlayer)member.getPlayer().getPlayer()).messages.poll(), testSnitchMsg);
		}
		
		// Snitch mute
		assertFalse(group.isSnitchMutedBy(groupOwner));
		group.setSnitchMutedBy(groupOwner, true);
		assertTrue(group.isSnitchMutedBy(groupOwner));
		group.setSnitchMutedBy(groupOwner, false);
		assertFalse(group.isSnitchMutedBy(groupOwner));
		
		// Chat mute
		assertFalse(group.isChatMutedBy(groupOwner));
		group.setChatMutedBy(groupOwner, true);
		assertTrue(group.isChatMutedBy(groupOwner));
		group.setChatMutedBy(groupOwner, false);
		assertFalse(group.isChatMutedBy(groupOwner));
	}
	
	@Test
	public void testMember() {
		SabreMember member = new SabreMember(group, groupOwner, Rank.MEMBER);
		SabreMember member2 = new SabreMember(group, groupOwner, Rank.MEMBER);
		
		assertEquals(member.getGroup(), group);
		assertEquals(member.getID(), groupOwner.getID());
		assertEquals(member.getName(), groupOwner.getName());
		assertEquals(member.getPlayer(), groupOwner);
		assertEquals(member.getRank(), Rank.MEMBER);
		assertFalse(member.canBuild());
		assertFalse(member.canInvite());
		assertFalse(member.canKickMember(member2));
		
		member.setRank(Rank.BUILDER);
		assertEquals(member.getRank(), Rank.BUILDER);
		assertTrue(member.canBuild());
		assertFalse(member.canInvite());
		assertFalse(member.canKickMember(member2));
		
		member.setRank(Rank.OFFICER);
		assertEquals(member.getRank(), Rank.OFFICER);
		assertTrue(member.canBuild());
		assertTrue(member.canInvite());
		assertTrue(member.canKickMember(member2));
		
		member2.setRank(Rank.BUILDER);
		assertEquals(member2.getRank(), Rank.BUILDER);
		assertTrue(member.canKickMember(member2));
		
		member2.setRank(Rank.OFFICER);
		assertEquals(member2.getRank(), Rank.OFFICER);
		assertFalse(member.canKickMember(member2));
		
		member.setRank(Rank.ADMIN);
		assertEquals(member.getRank(), Rank.ADMIN);
		assertTrue(member.canBuild());
		assertTrue(member.canInvite());
		assertTrue(member.canKickMember(member2));
		
		member2.setRank(Rank.ADMIN);
		assertEquals(member2.getRank(), Rank.ADMIN);
		assertFalse(member.canKickMember(member2));
		
		member.setRank(Rank.OWNER);
		assertEquals(member.getRank(), Rank.OWNER);
		assertTrue(member.canBuild());
		assertTrue(member.canInvite());
		assertTrue(member.canKickMember(member2));
	}
	
	@Test
	public void testGroupExceptions() {
		
		Throwable e = null;
		try { new SabreGroup(UUID.randomUUID(), null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreGroup(null, "group"); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { group.setName(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { group.getMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { group.setName(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.addMember(null, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { group.addMember(groupOwner, null); } catch (Throwable ex) { e = ex; }
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
		try { group.isRank(groupOwner, null); } catch (Throwable ex) { e = ex; }
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
		try { group.msgAllBut(groupOwner, null); } catch (Throwable ex) { e = ex; }
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
	
	@Test
	public void testRankExceptions() {		
		Throwable e = null;
		try { Rank.fromString(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}
	
	@Test
	public void testMemberExceptions() {
		SabreMember member = new SabreMember(group, groupOwner, Rank.MEMBER);
		
		Throwable e = null;
		try { new SabreMember(null, groupOwner, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { new SabreMember(group, null, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreMember(group, groupOwner, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { member.setRank(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { member.canKickMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}
}
