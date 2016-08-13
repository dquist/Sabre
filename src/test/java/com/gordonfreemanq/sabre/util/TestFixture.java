package com.gordonfreemanq.sabre.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.junit.Assert;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.gordonfreemanq.sabre.PlayerListener;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.SabreTweaks;
import com.gordonfreemanq.sabre.StatsTracker;
import com.gordonfreemanq.sabre.blocks.BlockListener;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SignHandler;
import com.gordonfreemanq.sabre.chat.GlobalChat;
import com.gordonfreemanq.sabre.chat.ServerBroadcast;
import com.gordonfreemanq.sabre.factory.FactoryConfig;
import com.gordonfreemanq.sabre.factory.FactoryListener;
import com.gordonfreemanq.sabre.factory.FactoryWorker;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.prisonpearl.PearlListener;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.prisonpearl.PearlWorker;
import com.gordonfreemanq.sabre.snitch.SnitchListener;
import com.gordonfreemanq.sabre.snitch.SnitchLogger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TestFixture {
	
    private SabrePlugin sabrePlugin;
    private MockServer mockServer;
    private CommandSender commandSender;
    private ArrayList<MockWorld> worlds;
    private MockPluginManager mockPluginManager;

    public static final File pluginDirectory = new File("bin/test/server/plugins/Sabre");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");
    
    private boolean isSetUp = false;
    private boolean successfulLoad = false;
    
    private static class TestFixtureLoader {
        private static final TestFixture INSTANCE = new TestFixture();
    }
    
    public static TestFixture instance() {
    	if (!TestFixtureLoader.INSTANCE.isSetUp) {
    		TestFixtureLoader.INSTANCE.setUp();
    	}
    	return TestFixtureLoader.INSTANCE;
    }
    
    private TestFixture() { }

    
    /**
     * Sets up the test fixture
     * @return true if success
     */
    public boolean setUp() {
		try {
			isSetUp = true;
			
        	// Set up the test directory
            pluginDirectory.mkdirs();
            Assert.assertTrue(pluginDirectory.exists());

            // Don't mock toString() and equals()
            MockGateway.MOCK_STANDARD_METHODS = false;

            // Create main mock objects
            mockServer = MockServer.create(this);
            mockPluginManager = MockPluginManager.create(this);
            sabrePlugin = createPlugin();
            
            worlds = new ArrayList<MockWorld>();
            
            // Create default worlds
            createMockWorld(new WorldCreator("world"));
            createMockWorld(new WorldCreator("world_nether"));
            createMockWorld(new WorldCreator("world_the_end"));

            // Init our command sender
            final Logger commandSenderLogger = Logger.getLogger("CommandSender");
            commandSenderLogger.setParent(Util.logger);
            commandSender = mock(CommandSender.class);
            doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    commandSenderLogger.info(ChatColor.stripColor((String) invocation.getArguments()[0]));
                    return null;
                }}).when(commandSender).sendMessage(anyString());
            when(commandSender.getServer()).thenReturn(mockServer);
            when(commandSender.getName()).thenReturn("MockCommandSender");
            when(commandSender.isPermissionSet(anyString())).thenReturn(true);
            when(commandSender.isPermissionSet(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.hasPermission(anyString())).thenReturn(true);
            when(commandSender.hasPermission(Matchers.isA(Permission.class))).thenReturn(true);
            when(commandSender.addAttachment(sabrePlugin)).thenReturn(null);
            when(commandSender.isOp()).thenReturn(true);

            Bukkit.setServer(mockServer);

            // Load and enable the plugin
            //sabrePlugin.onLoad();
            sabrePlugin.onEnable();

            successfulLoad = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    
    /**
     * Tears down the test fixture
     * @return true if success
     */
    public boolean tearDown() {

        sabrePlugin.onDisable();
        
        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE, "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return false;
        }

        FileUtils.deleteFolder(serverDirectory);
        worlds.clear();
        
        Util.log("TEAR DOWN COMPLETE");
        return true;
    }
    
    
    public boolean successfulLoad() {
    	return this.successfulLoad;
    }

    
    /**
     * Gets the mock plugin instance
     * @return The mock plugin
     */
    public SabrePlugin getPlugin() {
        return this.sabrePlugin;
    }

    /**
     * Gets the mock server
     * @return The mock server
     */
    public MockServer getMockServer() {
        return this.mockServer;
    }
    
    
    /**
     * Gets the mock plugin manager
     * @return The mock plugin manager
     */
    public MockPluginManager getMockPluginManager() {
    	return this.mockPluginManager;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }
    
    public ArrayList<MockWorld> getWorlds() {
    	return worlds;
    }
    
    public MockWorld getWorld(String name) {
    	for (MockWorld w : worlds) {
    		if (w.name == name) {
    			return w;
    		}
    	}
    	return null;
    }
    
    public MockWorld getWorld(UUID uid) {
    	for (MockWorld w : worlds) {
    		if (w.uid == uid) {
    			return w;
    		}
    	}
    	return null;
    }
    
    
    /**
     * Creates a mock world
     * @param creator The world creator
     * @return The new world
     */
    public World createMockWorld(WorldCreator creator) {
        File worldFile = new File(serverDirectory, creator.name());
        Util.log("Creating world-folder: " + worldFile.getAbsolutePath());
        worldFile.mkdirs();
    	
    	MockWorld mockWorld = MockWorld.create(creator.name(), creator.environment(), creator.type());
        mockWorld.worldFolder = new File(TestFixture.serverDirectory, mockWorld.getName());
    	new File(TestFixture.worldsDirectory, mockWorld.getName()).mkdir();
    	worlds.add(mockWorld);
    	return mockWorld;
    }
    
    
    /**
     * Creates the mock plugin instance
     * @return the new plugin instance
     */
	private SabrePlugin createPlugin() throws Exception {
		
		// Mock plugin loader
		PluginLoader pluginLoader = mock(PluginLoader.class);
		
		// Mock PDF
        PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12", "com.gordonfreemanq.sabre.SabrePlugin"));
        when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
        when(pdf.getPermissions()).thenReturn(new ArrayList<Permission>());
		
        @SuppressWarnings("deprecation")
		SabrePlugin plugin = PowerMockito.spy(new SabrePlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));
		
		// Put all files in bin/test
		doReturn(TestFixture.pluginDirectory).when(plugin).getDataFolder();
		
        doReturn(true).when(plugin).isEnabled();
        doReturn(Util.logger).when(plugin).getLogger();
        
        plugin.onLoad();
        
        // Need to inject mock instances of all the classes which take SabrePlugin as a dependency
        // Otherwise they will have a reference to the non-mocked version of the plugin
        Field field = SabrePlugin.class.getDeclaredField("dataAccess");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new MockDataAccess()));
        
        field = SabrePlugin.class.getDeclaredField("config");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SabreConfig(plugin.getConfig())));
        
        field = SabrePlugin.class.getDeclaredField("playerManager");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PlayerManager(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("groupManager");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new GroupManager(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("blockManager");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new BlockManager(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("pearlManager");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PearlManager(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("playerListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PlayerListener(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("blockListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new BlockListener(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("snitchLogger");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SnitchLogger(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("snitchListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SnitchListener(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("pearlListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PearlListener(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("playerSpawner");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PlayerSpawner(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("globalChat");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new GlobalChat(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("serverBcast");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new ServerBroadcast(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("sabreTweaks");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SabreTweaks(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("factoryListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new FactoryListener(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("factoryConfig");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new FactoryConfig(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("customItems");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new CustomItems(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("combatTag");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new CombatTagPlusManager()));
        
        field = SabrePlugin.class.getDeclaredField("statsTracker");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new StatsTracker(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("signHandler");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SignHandler(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("factoryWorker");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new FactoryWorker()));
        
        field = SabrePlugin.class.getDeclaredField("pearlWorker");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PearlWorker(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("vanishApi");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new VanishApi()));
        
        field = SabrePlugin.class.getDeclaredField("perm");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PermUtil(plugin)));
        
        // Mock the Protocol Library
        PowerMockito.mockStatic(ProtocolLibrary.class);
        ProtocolManager protocolManager = PowerMockito.mock(ProtocolManager.class);
        when(ProtocolLibrary.getProtocolManager()).thenReturn(protocolManager);
		
		return plugin;
	}
}