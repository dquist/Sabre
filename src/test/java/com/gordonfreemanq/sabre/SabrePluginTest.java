package com.gordonfreemanq.sabre;

import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.util.TestInstanceCreator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, SabrePlugin.class, Permission.class, Bukkit.class, PluginDescriptionFile.class,
	PlayerJoinEvent.class	
})
public class SabrePluginTest {
	
    private TestInstanceCreator creator;
    private SabrePlugin plugin;
    private CommandSender mockCommandSender;
    private PlayerManager pm;
    private PlayerListener playerListener;
    
    private MockPlayer newPlayer;
    private PlayerJoinEvent playerJoinEvent;
    
	
	@Before
	public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        plugin = creator.getPlugin();
        mockCommandSender = creator.getCommandSender();
        
        pm = plugin.getPlayerManager();
        playerListener = plugin.getPlayerListener();
        
        
        newPlayer = createMockPlayer();
        createEvents();
	}
	
	@After
	public void tearDown() throws Exception {
		creator.tearDown();
	}
	
	@Test
	public void testPlayerListener() throws Exception {
        
        // New player join
		assertNull(pm.getPlayerById(newPlayer.getUniqueId()));
		
		newPlayer.isOnline = true;
		playerListener.onPlayerJoin(playerJoinEvent);
		SabrePlayer sp = pm.getPlayerById(newPlayer.getUniqueId());
		assertNotNull("Player added to player manager", sp);
		assertTrue("Player is online", pm.getOnlinePlayers().contains(sp));
		assertTrue("Player is online", sp.isOnline());
		assertFalse("Player not admin", sp.isAdmin());
		assertFalse("Player not admin bypass", sp.getAdminBypass());
		assertFalse("Player not set to auto join", sp.getAutoJoin());
		assertEquals("No offline messages", sp.getOfflineMessages().size(), 0);
		assertEquals("Spawn in free world", sp.getPlayer().getWorld(), plugin.getServer().getWorld(plugin.getSabreConfig().getFreeWorldName()));
		verify(newPlayer, times(1)).teleport(any(Location.class));
	}
	
	
	/**
	 * Create events
	 */
	private void createEvents() {
        
        playerJoinEvent = PowerMockito.mock(PlayerJoinEvent.class);
        when(playerJoinEvent.getPlayer()).thenReturn(newPlayer);
	}
	
	
	/**
	 * Creates a mock player instance
	 */
	private MockPlayer createMockPlayer() {
        // Create a mock player
		MockPlayer mockPlayer = mock(MockPlayer.class, Mockito.CALLS_REAL_METHODS);
		
		Player player = mock(Player.class);
		mockPlayer.ID = UUID.randomUUID();
        when(player.hasPlayedBefore()).thenReturn(false);
        when(player.getUniqueId()).thenReturn(mockPlayer.ID);
        when(player.getName()).thenReturn(mockPlayer.name);
        
        return mockPlayer;
	}
}
