package com.gordonfreemanq.sabre;

import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.Inet4Address;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Bed;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.util.MockBlock;
import com.gordonfreemanq.sabre.util.MockPlayer;
import com.gordonfreemanq.sabre.util.MockWorld;
import com.gordonfreemanq.sabre.util.TestFixture;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, SabrePlugin.class, Permission.class, Bukkit.class, PluginDescriptionFile.class })
public class SabrePluginTest {
	
	private static String BAN_MESSAGE = "Test ban message";
	
    private static TestFixture testFixture;
    private static SabrePlugin plugin;
    private static PlayerManager pm;
    private static PlayerListener playerListener;
    
	
	@BeforeClass
	public static void setUp() throws Exception {
        testFixture = TestFixture.instance();
        plugin = testFixture.getPlugin();
        
        pm = plugin.getPlayerManager();
        playerListener = plugin.getPlayerListener();
	}
	
	@AfterClass
	public static void tearDown() throws Exception {
		testFixture.tearDown();
	}
	
	@Test
	public void testPlayerJoin() throws Exception {
		
		MockWorld overWorld = testFixture.getWorld(plugin.config().getFreeWorldName());
        MockPlayer newPlayer = MockPlayer.create("NewPlayer");
        
        AsyncPlayerPreLoginEvent playerPreLoginEvent = new AsyncPlayerPreLoginEvent(newPlayer.name, Inet4Address.getLocalHost(), newPlayer.ID);
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(newPlayer, null);
        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(newPlayer, null);
        
        // Player doesn't exist yet
		assertNull(pm.getPlayerById(newPlayer.getUniqueId()));
		
		// Prevent pre-login if plugin not loaded
		playerListener.onPlayerPreLogin(playerPreLoginEvent);
		assertEquals(playerPreLoginEvent.getLoginResult(), Result.KICK_OTHER);
		assertEquals(playerPreLoginEvent.getKickMessage(), Lang.serverNotLoaded);

		// Try again with plugin loaded
		playerPreLoginEvent.setLoginResult(Result.ALLOWED);
		playerPreLoginEvent.setKickMessage(null);
		playerListener.onPlayerPreLogin(playerPreLoginEvent);
		assertEquals(playerPreLoginEvent.getLoginResult(), Result.ALLOWED);
		assertEquals(playerPreLoginEvent.getKickMessage(), null);
		
		// Player join
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
		assertEquals("Spawn in free world", sp.getPlayer().getWorld(), overWorld);
		verify(newPlayer, times(1)).teleport(any(Location.class));
		assertEquals(newPlayer.messages.poll(), Lang.playerYouWakeUp);
		
		// Player quit
		playerListener.onPlayerQuit(playerQuitEvent);
		 sp = pm.getPlayerById(newPlayer.getUniqueId());
		assertFalse("Player is offline", pm.getOnlinePlayers().contains(sp));
		assertFalse("Player is online", sp.isOnline());
		
		// Banned player join
		sp.setBanned(true);
		sp.setBanMessage(BAN_MESSAGE);
		playerListener.onPlayerPreLogin(playerPreLoginEvent);
		assertEquals(playerPreLoginEvent.getLoginResult(), Result.KICK_BANNED);
		assertEquals(playerPreLoginEvent.getKickMessage(), String.format("%s\n%s", Lang.youAreBanned, BAN_MESSAGE));
		
		// Player re-join
		sp.setBanned(false);
		sp.setBanMessage(null);
		playerListener.onPlayerJoin(playerJoinEvent);
		 sp = pm.getPlayerById(newPlayer.getUniqueId());
		assertTrue("Player is online", pm.getOnlinePlayers().contains(sp));
		assertTrue("Player is online", sp.isOnline());
		assertEquals(newPlayer.messages.poll(), null);
		
		// Player dies
		PlayerDeathEvent playerDeathEvent = new PlayerDeathEvent(newPlayer, new ArrayList<ItemStack>(), 0, "Died");
		playerListener.onPlayerDeath(playerDeathEvent);
		assertNull("No death message", playerDeathEvent.getDeathMessage());
		
		// Player respawn
		PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(newPlayer, new Location(overWorld, 0, 64, 0), false);
		playerListener.onPlayerRespawn(playerRespawnEvent);
		assertEquals(newPlayer.messages.poll(), Lang.playerYouWakeUp);
		
		// Player respawn bed
		Location bedLocation = new Location(overWorld, 100, 50, 100);
		MockBlock bedBlock = overWorld.addBlock(MockBlock.create(bedLocation, Material.BED_BLOCK));
		Bed bed = new Bed();
		bed.setHeadOfBed(false);
		bedBlock.getState().setData(bed);
		sp.setBedLocation(bedLocation);
		PlayerRespawnEvent playerRespawnBedEvent = new PlayerRespawnEvent(newPlayer, new Location(overWorld, 100, 64, 100), true);
		playerListener.onPlayerRespawn(playerRespawnBedEvent);
		assertEquals(playerRespawnBedEvent.getRespawnLocation(), bedLocation);
		assertEquals(newPlayer.getBedSpawnLocation(), bedLocation);
		assertEquals(newPlayer.messages.poll(), null);
		
		// Random spawn if bed missing
		bedBlock.setType(Material.AIR);
		playerListener.onPlayerRespawn(playerRespawnBedEvent);
		assertEquals(newPlayer.messages.poll(), plugin.txt.parse(Lang.playerBedMissing));
		assertEquals(newPlayer.messages.poll(), Lang.playerYouWakeUp);
		
		// Random spawn again without bed message even if bed is back
		bedBlock.setType(Material.BED_BLOCK);
		playerListener.onPlayerRespawn(playerRespawnBedEvent);
		assertEquals(newPlayer.messages.poll(), Lang.playerYouWakeUp);
		
		// Player quit
		playerListener.onPlayerQuit(playerQuitEvent);
		 sp = pm.getPlayerById(newPlayer.getUniqueId());
		assertFalse("Player is offline", pm.getOnlinePlayers().contains(sp));
		assertFalse("Player is online", sp.isOnline());
	}
}
