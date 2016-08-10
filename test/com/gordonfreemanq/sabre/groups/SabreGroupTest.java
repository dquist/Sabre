package com.gordonfreemanq.sabre.groups;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import com.gordonfreemanq.sabre.SabreAPI;
import com.gordonfreemanq.sabre.SabrePlayer;

public class SabreGroupTest {
	
	private static String OWNER_NAME = "DutOwner";
	private static String ADMIN_NAME = "DutAdmin";
	private static String OFFICER_NAME = "DutOfficer";
	private static String BUILDER_NAME = "DutBuilder";
	private static String MEMBER_NAME = "DutMember";
	
	private final UUID m_GroupUuid = UUID.randomUUID();
	private final String m_GroupName = "DUT_GROUP";
	private final SabreGroup m_Group;
	private final HashMap<SabrePlayer, Rank> m_Members;
	

	public SabreGroupTest() {
		m_Group = new SabreGroup(m_GroupUuid, m_GroupName);
		
		// Add members to the group
		m_Members = new HashMap<SabrePlayer, Rank>();

		m_Members.put(new SabrePlayer(UUID.randomUUID(), OWNER_NAME), Rank.OWNER);
		m_Members.put(new SabrePlayer(UUID.randomUUID(), ADMIN_NAME), Rank.ADMIN);
		m_Members.put(new SabrePlayer(UUID.randomUUID(), OFFICER_NAME), Rank.OFFICER);
		m_Members.put(new SabrePlayer(UUID.randomUUID(), BUILDER_NAME), Rank.BUILDER);
		m_Members.put(new SabrePlayer(UUID.randomUUID(), MEMBER_NAME), Rank.MEMBER);
		
		for(Entry<SabrePlayer, Rank> entry : m_Members.entrySet()) {
			m_Group.addMember(entry.getKey(), entry.getValue());
		}
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		SabreAPI.IsUnitTesting = true;
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertEquals(m_Group.getID(), m_GroupUuid);
		assertFalse(m_Group.isFaction());
	}

	@Test
	public void testGetSetName() {
		String oldName = m_Group.getName();
		String testName = "NewName";
		
		m_Group.setName(testName);
		assertEquals(m_Group.getName(), testName);
		m_Group.setName(oldName);
		assertEquals(m_Group.getName(), oldName);
		
		// Full name
		assertEquals(m_Group.getFullName(), String.format("%s#%s", m_Group.getName(), m_Group.getOwner().getName()));
	}

	@Test
	public void testMembers() {
		
		Collection<SabreMember> members = m_Group.getMembers();
		assertEquals(members.size(), m_Members.size());
		
		for(SabreMember member : members) {
			assertEquals(m_Members.get(member.getPlayer()), member.getRank());
		}
		
		SabrePlayer testPlayer = new SabrePlayer(UUID.randomUUID(), "TestPlayer");
		assertFalse(m_Group.isMember(testPlayer));
		assertFalse(m_Group.isInvited(testPlayer));
		
		m_Group.addMember(testPlayer, Rank.MEMBER);
		assertTrue(m_Group.isMember(testPlayer));
		assertFalse(m_Group.isInvited(testPlayer));
		assertFalse(m_Group.isBuilder(testPlayer));
		
		m_Group.removePlayer(testPlayer);
		assertFalse(m_Group.isMember(testPlayer));
		assertFalse(m_Group.isInvited(testPlayer));
		assertFalse(m_Group.isBuilder(testPlayer));
		assertFalse(m_Group.isOfficer(testPlayer));
		
		m_Group.addMember(testPlayer, Rank.MEMBER);
		assertTrue(m_Group.isMember(testPlayer));
		assertFalse(m_Group.isBuilder(testPlayer));
		assertFalse(m_Group.isOfficer(testPlayer));
		
		SabreMember testMember = m_Group.getMember(testPlayer);
		assertNotNull(testMember);
		assertTrue(m_Group.isRank(testPlayer, Rank.MEMBER));
		assertFalse(m_Group.isRank(testPlayer, Rank.OFFICER));
		
		testMember.setRank(Rank.BUILDER);
		assertTrue(m_Group.isBuilder(testPlayer));
		assertFalse(m_Group.isOfficer(testPlayer));
		assertTrue(m_Group.isRank(testPlayer, Rank.BUILDER));
		
		testMember.setRank(Rank.OFFICER);
		assertTrue(m_Group.isBuilder(testPlayer));
		assertTrue(m_Group.isOfficer(testPlayer));
		assertTrue(m_Group.isRank(testPlayer, Rank.OFFICER));
		
		testMember.setRank(Rank.ADMIN);
		assertTrue(m_Group.isBuilder(testPlayer));
		assertTrue(m_Group.isOfficer(testPlayer));
		assertTrue(m_Group.isRank(testPlayer, Rank.ADMIN));
		
		m_Group.removeMember(testMember);
		assertFalse(m_Group.isMember(testPlayer));
		
		assertFalse(m_Group.isInvited(testPlayer));
		
		m_Group.addInvited(testPlayer);
		assertTrue(m_Group.isInvited(testPlayer));
		
		m_Group.removeInvited(testPlayer);
		assertFalse(m_Group.isInvited(testPlayer));
	}

}
