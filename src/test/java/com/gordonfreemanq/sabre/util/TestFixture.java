package com.gordonfreemanq.sabre.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Assert;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.MockGateway;

import com.gordonfreemanq.sabre.SabrePlugin;

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
    private Server mockServer;
    private CommandSender commandSender;
    private ArrayList<MockWorld> worlds;
    private PluginManager mockPluginManager;

    public static final File pluginDirectory = new File("bin/test/server/plugins/Sabre");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");
    
    private static class TestFixtureLoader {
        private static final TestFixture INSTANCE = new TestFixture();
    }
    
    private TestFixture() {
        if (TestFixtureLoader.INSTANCE != null) {
            throw new IllegalStateException("Already instantiated");
        }
        
        mockServer = MockServer.create(this);
        mockPluginManager = mock(PluginManager.class);
        worlds = new ArrayList<MockWorld>();
        
        
        setUp();
    }
    
    public static TestFixture instance() {
    	return TestFixtureLoader.INSTANCE;
    }

    private boolean setUp() {
        try {
            pluginDirectory.mkdirs();
            Assert.assertTrue(pluginDirectory.exists());

            // Don't mock toString() and equals()
            MockGateway.MOCK_STANDARD_METHODS = false;

            PluginLoader pluginLoader = Mockito.mock(PluginLoader.class);
            MockDataAccess mockData = PowerMockito.spy(new MockDataAccess());

            // Return a fake PDF file.
            PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12",
                    "com.gordonfreemanq.sabre.SabrePlugin"));
            when(pdf.getAuthors()).thenReturn(new ArrayList<String>());

            sabrePlugin = PowerMockito.spy(new SabrePlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));
            //sabrePlugin.setDataAccess(mockData);

            // Put all files go to bin/test
            doReturn(pluginDirectory).when(sabrePlugin).getDataFolder();

            doReturn(true).when(sabrePlugin).isEnabled();
            doReturn(Util.logger).when(sabrePlugin).getLogger();
            sabrePlugin.setServerFolder(serverDirectory);

            // Add Core to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[] { sabrePlugin };

            // Mock the Plugin Manager
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("Sabre")).thenReturn(sabrePlugin);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);

            // Make some fake folders to fool the fake plugin into thinking these worlds exist
            File worldNormalFile = new File(sabrePlugin.getServerFolder(), "world");
            Util.log("Creating world-folder: " + worldNormalFile.getAbsolutePath());
            worldNormalFile.mkdirs();
            File worldNetherFile = new File(sabrePlugin.getServerFolder(), "world_nether");
            Util.log("Creating world-folder: " + worldNetherFile.getAbsolutePath());
            worldNetherFile.mkdirs();
            File worldSkylandsFile = new File(sabrePlugin.getServerFolder(), "world_the_end");
            Util.log("Creating world-folder: " + worldSkylandsFile.getAbsolutePath());
            worldSkylandsFile.mkdirs();
            
            createMockWorld(new WorldCreator("world"));
            createMockWorld(new WorldCreator("world_nether"));
            createMockWorld(new WorldCreator("world_the_end"));

            // Set server
            Field serverfield = JavaPlugin.class.getDeclaredField("server");
            serverfield.setAccessible(true);
            serverfield.set(sabrePlugin, mockServer);

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
            sabrePlugin.onLoad();
            sabrePlugin.onEnable();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tearDown() {

        try {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(Class.forName("org.bukkit.Bukkit"), null);
        } catch (Exception e) {
            Util.log(Level.SEVERE,
                    "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return false;
        }

        sabrePlugin.onDisable();

        FileUtils.deleteFolder(serverDirectory);
        worlds.clear();
        
        Util.log("TEAR DOWN COMPLETE");
        return true;
    }

    public SabrePlugin getPlugin() {
        return this.sabrePlugin;
    }

    public Server getServer() {
        return this.mockServer;
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
    
    public PluginManager getPluginManager() {
    	return this.mockPluginManager;
    }
    
    public World createMockWorld(WorldCreator creator) {
    	MockWorld mockWorld = MockWorld.create(creator.name(), creator.environment(), creator.type());
        mockWorld.worldFolder = new File(TestFixture.serverDirectory, mockWorld.getName());
    	new File(TestFixture.worldsDirectory, mockWorld.getName()).mkdir();
    	worlds.add(mockWorld);
    	return mockWorld;
    }
}