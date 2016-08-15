package com.civfactions.sabre.test;

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

import com.civfactions.sabre.PlayerListener;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabreConfig;
import com.civfactions.sabre.SabreLogger;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.SabreTweaks;
import com.civfactions.sabre.StatsTracker;
import com.civfactions.sabre.blocks.BlockListener;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.CustomItems;
import com.civfactions.sabre.blocks.SignHandler;
import com.civfactions.sabre.chat.GlobalChat;
import com.civfactions.sabre.chat.ServerBroadcast;
import com.civfactions.sabre.data.IDataAccess;
import com.civfactions.sabre.factory.FactoryConfig;
import com.civfactions.sabre.factory.FactoryListener;
import com.civfactions.sabre.factory.FactoryWorker;
import com.civfactions.sabre.groups.GroupManager;
import com.civfactions.sabre.prisonpearl.PearlListener;
import com.civfactions.sabre.prisonpearl.PearlManager;
import com.civfactions.sabre.prisonpearl.PearlWorker;
import com.civfactions.sabre.snitch.SnitchListener;
import com.civfactions.sabre.snitch.SnitchLogger;
import com.civfactions.sabre.util.CombatTagPlusManager;
import com.civfactions.sabre.util.PlayerSpawner;
import com.civfactions.sabre.util.VanishApi;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TestFixture {
	
    private SabrePlugin sabrePlugin;
    private MockServer mockServer;
    private CommandSender commandSender;
    private ArrayList<MockWorld> worlds;
    private MockPluginManager mockPluginManager;
    private MockScheduler mockScheduler;

    public static final File pluginDirectory = new File("bin/test/server/plugins/Sabre");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");

    public static final Logger logger = Logger.getLogger("Sabre-Test");
    
    public TestFixture() { }

    
    /**
     * Sets up the test fixture
     * @return true if success
     */
    public boolean setUp() {
		try {			
        	// Set up the test directory
            pluginDirectory.mkdirs();
            Assert.assertTrue(pluginDirectory.exists());

            // Don't mock toString() and equals()
            MockGateway.MOCK_STANDARD_METHODS = false;

            // Create main mock objects
            mockScheduler = MockScheduler.create();
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
            commandSenderLogger.setParent(logger);
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
            log(Level.SEVERE, "Error while trying to unregister the server from Bukkit. Has Bukkit changed?");
            e.printStackTrace();
            Assert.fail(e.getMessage());
            return false;
        }

        TestFixture.deleteFolder(serverDirectory);
        worlds.clear();
        
        log("TEAR DOWN COMPLETE");
        return true;
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

    /**
     * Gets the mock command sender
     * @return The mock command sender
     */
    public CommandSender getCommandSender() {
        return commandSender;
    }

    /**
     * Gets the mock scheduler
     * @return The mock scheduler
     */
    public MockScheduler getMockScheduler() {
        return mockScheduler;
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
        log("Creating world-folder: " + worldFile.getAbsolutePath());
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
        PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12", "com.civfactions.sabre.SabrePlugin"));
        when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
        when(pdf.getPermissions()).thenReturn(new ArrayList<Permission>());
		
        @SuppressWarnings("deprecation")
		SabrePlugin plugin = PowerMockito.spy(new SabrePlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));
		
		// Put all files in bin/test
		doReturn(TestFixture.pluginDirectory).when(plugin).getDataFolder();
		
        doReturn(true).when(plugin).isEnabled();
        doReturn(logger).when(plugin).getLogger();
        
        plugin.onLoad();
        
        // Need to inject mock instances of all the classes which take SabrePlugin as a dependency
        // Otherwise they will have a reference to the non-mocked version of the plugin
        IDataAccess dataAccess =  Mockito.spy(new MockDataAccess());
        Field field = SabrePlugin.class.getDeclaredField("dataAccess");
        field.setAccessible(true);
        field.set(plugin, dataAccess);
        
        SabreConfig config = Mockito.spy(new SabreConfig(plugin.getConfig()));
        field = SabrePlugin.class.getDeclaredField("config");
        field.setAccessible(true);
        field.set(plugin, config);
        
        PlayerManager playerManager = Mockito.spy(new PlayerManager(dataAccess));
        field = SabrePlugin.class.getDeclaredField("playerManager");
        field.setAccessible(true);
        field.set(plugin, playerManager);
        
        BlockManager blockManager = Mockito.spy(new BlockManager(plugin, dataAccess));
        field = SabrePlugin.class.getDeclaredField("blockManager");
        field.setAccessible(true);
        field.set(plugin, blockManager);
        
        GroupManager groupManager = Mockito.spy(new GroupManager(playerManager, blockManager, dataAccess));
        field = SabrePlugin.class.getDeclaredField("groupManager");
        field.setAccessible(true);
        field.set(plugin, groupManager);
        
        PearlManager pearlManager = Mockito.spy(new PearlManager(plugin, dataAccess));
        field = SabrePlugin.class.getDeclaredField("pearlManager");
        field.setAccessible(true);
        field.set(plugin, pearlManager);
        
        field = SabrePlugin.class.getDeclaredField("playerListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PlayerListener(plugin, playerManager)));
        
        field = SabrePlugin.class.getDeclaredField("blockListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new BlockListener(plugin, playerManager, blockManager, config)));
        
        field = SabrePlugin.class.getDeclaredField("snitchLogger");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SnitchLogger(playerManager, dataAccess)));
        
        field = SabrePlugin.class.getDeclaredField("snitchListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SnitchListener(plugin, playerManager, blockManager)));
        
        field = SabrePlugin.class.getDeclaredField("pearlListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PearlListener(plugin, playerManager, pearlManager)));
        
        field = SabrePlugin.class.getDeclaredField("playerSpawner");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PlayerSpawner(playerManager, blockManager, config)));
        
        field = SabrePlugin.class.getDeclaredField("globalChat");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new GlobalChat(plugin, config)));
        
        field = SabrePlugin.class.getDeclaredField("serverBcast");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new ServerBroadcast(playerManager)));
        
        field = SabrePlugin.class.getDeclaredField("sabreTweaks");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SabreTweaks(plugin, config)));
        
        field = SabrePlugin.class.getDeclaredField("factoryListener");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new FactoryListener(playerManager, blockManager)));
        
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
        field.set(plugin, Mockito.spy(new StatsTracker(plugin, playerManager)));
        
        field = SabrePlugin.class.getDeclaredField("signHandler");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SignHandler(plugin)));
        
        field = SabrePlugin.class.getDeclaredField("factoryWorker");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new FactoryWorker()));
        
        field = SabrePlugin.class.getDeclaredField("pearlWorker");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new PearlWorker(plugin, pearlManager, config)));
        
        field = SabrePlugin.class.getDeclaredField("vanishApi");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new VanishApi()));
        
        field = SabrePlugin.class.getDeclaredField("logger");
        field.setAccessible(true);
        field.set(plugin, Mockito.spy(new SabreLogger(plugin)));
        
        // Mock the Protocol Library
        PowerMockito.mockStatic(ProtocolLibrary.class);
        ProtocolManager protocolManager = PowerMockito.mock(ProtocolManager.class);
        when(ProtocolLibrary.getProtocolManager()).thenReturn(protocolManager);
		
		return plugin;
	}
	
    /**
     * Formatter to format log-messages in tests
     *
     */
    private static class MVTestLogFormatter extends Formatter {
        private static final DateFormat df = new SimpleDateFormat("HH:mm:ss");

        public String format(LogRecord record) {
            StringBuilder ret = new StringBuilder();

            ret.append("[").append(df.format(record.getMillis())).append("] [")
                    .append(record.getLoggerName()).append("] [")
                    .append(record.getLevel().getLocalizedName()).append("] ");
            ret.append(record.getMessage());
            ret.append('\n');

            if (record.getThrown() != null) {
                // An Exception was thrown! Let's print the StackTrace!
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                ret.append(writer);
            }

            return ret.toString();
        }
    }

    static {
        logger.setUseParentHandlers(false);

        Handler handler = new ConsoleHandler();
        handler.setFormatter(new MVTestLogFormatter());
        Handler[] handlers = logger.getHandlers();

        for (Handler h : handlers)
            logger.removeHandler(h);

        logger.addHandler(handler);
    }


    public static void log(Throwable t) {
        log(Level.WARNING, t.getLocalizedMessage(), t);
    }

    public static void log(Level level, Throwable t) {
        log(level, t.getLocalizedMessage(), t);
    }

    public static void log(String message, Throwable t) {
        log(Level.WARNING, message, t);
    }

    public static void log(Level level, String message, Throwable t) {
        LogRecord record = new LogRecord(level, message);
        record.setThrown(t);
        logger.log(record);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }

    /**
     * Used to delete a folder.
     *
     * @param file The folder to delete.
     * @return true if the folder was successfully deleted.
     */
    public static boolean deleteFolder(File file) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(file);
            return true;
        } catch (IOException e) {
            //Logging.warning(e.getMessage());
            return false;
        }
    }

    /**
     * Used to delete the contents of a folder, without deleting the folder itself.
     *
     * @param file The folder whose contents to delete.
     * @return true if the contents were successfully deleted
     */
    public static boolean deleteFolderContents(File file) {
        try {
            org.apache.commons.io.FileUtils.cleanDirectory(file);
            return true;
        } catch (IOException e) {
            //Logging.warning(e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to copy the world-folder.
     * @param source Source-File
     * @param target Target-File
     * @param log A logger that logs the operation
     *
     * @return if it had success
     */
    public static boolean copyFolder(File source, File target, Logger log) {
        try {
            org.apache.commons.io.FileUtils.copyDirectory(source, target);
            return true;
        } catch (IOException e) {
            log.warning(e.getMessage());
            return false;
        }
    }
}