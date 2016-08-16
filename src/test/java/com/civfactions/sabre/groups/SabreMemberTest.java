package com.civfactions.sabre.groups;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class })
public class SabreMemberTest {
	
	private static SabrePlugin plugin;
	
	private SabreGroup group;
	
	private SabrePlayer p1;
	private SabrePlayer p2;
	private SabrePlayer p3;
	
	private SabreMember m1;
	private SabreMember m2;
	private SabreMember m3;
	
	@BeforeClass
	public static void setUpClass() {		
		plugin = PowerMockito.mock(SabrePlugin.class);
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
		
		group = new SabreGroup(plugin, UUID.randomUUID(), "TestGroup");
		m1 = group.addMember(p1, Rank.OWNER);
		m2 = group.addMember(p2, Rank.MEMBER);
		m3 = group.addMember(p3, Rank.MEMBER);
	}

	@Test
	public void test() {
		
		assertEquals(m2.getGroup(), group);
		assertEquals(m2.getID(), p2.getID());
		assertEquals(m2.getName(), p2.getName());
		assertEquals(m2.getPlayer(), p2);
		assertEquals(m2.getRank(), Rank.MEMBER);
		assertFalse(m2.canBuild());
		assertFalse(m2.canInvite());
		assertFalse(m2.canKickMember(m3));
		
		m2.setRank(Rank.BUILDER);
		assertEquals(m2.getRank(), Rank.BUILDER);
		assertTrue(m2.canBuild());
		assertFalse(m2.canInvite());
		assertFalse(m2.canKickMember(m3));
		
		m2.setRank(Rank.OFFICER);
		assertEquals(m2.getRank(), Rank.OFFICER);
		assertTrue(m2.canBuild());
		assertTrue(m2.canInvite());
		assertTrue(m2.canKickMember(m3));
		
		m3.setRank(Rank.BUILDER);
		assertEquals(m3.getRank(), Rank.BUILDER);
		assertTrue(m2.canKickMember(m3));
		
		m3.setRank(Rank.OFFICER);
		assertEquals(m3.getRank(), Rank.OFFICER);
		assertFalse(m2.canKickMember(m3));
		
		m2.setRank(Rank.ADMIN);
		assertEquals(m2.getRank(), Rank.ADMIN);
		assertTrue(m2.canBuild());
		assertTrue(m2.canInvite());
		assertTrue(m2.canKickMember(m3));
		
		m3.setRank(Rank.ADMIN);
		assertEquals(m3.getRank(), Rank.ADMIN);
		assertFalse(m2.canKickMember(m3));
		
		// Can't set multiple owners
		Throwable e = null;
		try { m2.setRank(Rank.OWNER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
		
		// Can't demote owner
		e = null;
		try { m1.setRank(Rank.ADMIN); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof RuntimeException);
	}
	
	@Test
	public void testExceptions() {
		SabreMember member = new SabreMember(group, p1, Rank.MEMBER);
		
		Throwable e = null;
		try { new SabreMember(null, p1, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);

		e = null;
		try { new SabreMember(group, null, Rank.MEMBER); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { new SabreMember(group, p1, null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { member.setRank(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
		
		e = null;
		try { member.canKickMember(null); } catch (Throwable ex) { e = ex; }
		assertTrue(e instanceof NullArgumentException);
	}

}
