package com.gordonfreemanq.sabre;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.util.TestFixture;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, SabrePlugin.class, Permission.class, Bukkit.class, PluginDescriptionFile.class })
public class SabreGroupTest {
	
	private static String OWNER_NAME = "DutOwner";
	private static String ADMIN_NAME = "DutAdmin";
	private static String OFFICER_NAME = "DutOfficer";
	private static String BUILDER_NAME = "DutBuilder";
	private static String MEMBER_NAME = "DutMember";

	private static TestFixture testFixture;
	private static UUID GroupUuid = UUID.randomUUID();
	private static String GroupName = "DUT_GROUP";
	private static SabreGroup group;
	private static HashMap<SabrePlayer, Rank> groupMembers;
	private static SabrePlayer groupOwner;
	
	@BeforeClass
	public static void setUp() throws Exception {
		testFixture = TestFixture.instance();
		testFixture.getPlugin();
		
		group = Mockito.spy(new SabreGroup(GroupUuid, GroupName));
		
		// Add members to the group
		groupMembers = new HashMap<SabrePlayer, Rank>();
		
		groupOwner = new SabrePlayer(UUID.randomUUID(), OWNER_NAME);
		groupMembers.put(groupOwner, Rank.OWNER);
		groupMembers.put(new SabrePlayer(UUID.randomUUID(), ADMIN_NAME), Rank.ADMIN);
		groupMembers.put(new SabrePlayer(UUID.randomUUID(), OFFICER_NAME), Rank.OFFICER);
		groupMembers.put(new SabrePlayer(UUID.randomUUID(), BUILDER_NAME), Rank.BUILDER);
		groupMembers.put(new SabrePlayer(UUID.randomUUID(), MEMBER_NAME), Rank.MEMBER);
		
		for(Entry<SabrePlayer, Rank> entry : groupMembers.entrySet()) {
			group.addMember(entry.getKey(), entry.getValue());
		}
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws Exception {
		assertEquals(group.getID(), GroupUuid);
		assertFalse(group.isFaction());
	}

	@Test
	public void testGetSetName() throws Exception {
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
	public void testMembers() throws Exception {
		
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
	public void testMessaging() throws Exception {
		
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
		
		group.msgAll("Test", true);
	}
}
