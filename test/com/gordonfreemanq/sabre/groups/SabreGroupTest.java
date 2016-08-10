package com.gordonfreemanq.sabre.groups;
import org.junit.*;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
	public void testGetID() {
		assertEquals(m_Group.getID(), m_GroupUuid);
	}

	@Test
	public void testGetFullName() {
		assertEquals(m_Group.getFullName(), String.format("%s#%s", m_Group.getName(), m_Group.getOwner().getName()));
	}

	@Test
	public void testIsFaction() {
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
	}

	@Test
	public void testGetMembers() {
		
		Collection<SabreMember> members = m_Group.getMembers();
		assertEquals(members.size(), m_Members.size());
		Set<SabrePlayer> groupPlayers = members.stream().map(m -> m.getPlayer()).collect(Collectors.toSet());
		
		for(Entry<SabrePlayer, Rank> entry : m_Members.entrySet()) {
			assertTrue(groupPlayers.contains(entry.getKey()));
		}
	}

}
