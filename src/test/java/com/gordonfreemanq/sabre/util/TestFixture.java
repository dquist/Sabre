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

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.data.IDataAccess;

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
     * Creates the mock plugin instance
     * @return the new plugin instance
     */
	private SabrePlugin createPlugin() throws Exception {
		
		// Mock plugin loader
		PluginLoader pluginLoader = mock(PluginLoader.class);
		
		// Mock PDF
        PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12", "com.gordonfreemanq.sabre.SabrePlugin"));
        when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
		
        @SuppressWarnings("deprecation")
		SabrePlugin plugin = PowerMockito.spy(new SabrePlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));
		
		// Put all files in bin/test
		doReturn(TestFixture.pluginDirectory).when(plugin).getDataFolder();
		
        doReturn(true).when(plugin).isEnabled();
        doReturn(Util.logger).when(plugin).getLogger();
        
        plugin.onLoad();
        
        // Set mock database
        IDataAccess dataAccess = Mockito.spy(new MockDataAccess());
        Field field = SabrePlugin.class.getDeclaredField("dataAccess");
        field.setAccessible(true);
        field.set(plugin, dataAccess);
		
		return plugin;
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
}