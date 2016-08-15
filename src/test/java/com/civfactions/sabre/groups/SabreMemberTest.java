package com.civfactions.sabre.groups;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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

import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.util.TextUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class })
public class SabreMemberTest {
	
	private static SabrePlugin plugin;
	private static TextUtil txt;
	
	private static SabreGroup group;
	
	private static SabrePlayer p1;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		txt = spy(new TextUtil());
		
		plugin = PowerMockito.mock(SabrePlugin.class);
		when(plugin.getGlobalChat()).thenReturn(mock(GlobalChat.class));
		when(plugin.logger()).thenReturn(mock(SabreLogger.class));
		when(plugin.getPlayerManager()).thenReturn(mock(PlayerManager.class));
		when(plugin.txt()).thenReturn(txt);
	}

	@Before
	public void setUp() throws Exception {
		p1 = new SabrePlayer(plugin, UUID.randomUUID(), "Player1");
		group = new SabreGroup(plugin, UUID.randomUUID(), "TestGroup");
		group.addMember(p1, Rank.OWNER);
	}

	@Test
	public void test() {
		SabreMember member = new SabreMember(group, p1, Rank.MEMBER);
		SabreMember member2 = new SabreMember(group, p1, Rank.MEMBER);
		
		assertEquals(member.getGroup(), group);
		assertEquals(member.getID(), p1.getID());
		assertEquals(member.getName(), p1.getName());
		assertEquals(member.getPlayer(), p1);
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
