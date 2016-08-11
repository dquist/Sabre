package com.gordonfreemanq.sabre.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
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
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.*;

public class TestInstanceCreator {
    private SabrePlugin sabrePlugin;
    private Server mockServer;
    private CommandSender commandSender;
    private ItemFactory itemFactory;

    public static final File pluginDirectory = new File("bin/test/server/plugins/Sabre");
    public static final File serverDirectory = new File("bin/test/server");
    public static final File worldsDirectory = new File("bin/test/server");

    public boolean setUp() {
        try {
            pluginDirectory.mkdirs();
            Assert.assertTrue(pluginDirectory.exists());

            MockGateway.MOCK_STANDARD_METHODS = false;

            TestPluginLoader pluginLoader = new TestPluginLoader();

            // Initialize the Mock server.
            mockServer = mock(Server.class);
            when(mockServer.getName()).thenReturn("TestBukkit");
            Logger.getLogger("Minecraft").setParent(Util.logger);
            when(mockServer.getLogger()).thenReturn(Util.logger);
            when(mockServer.getWorldContainer()).thenReturn(worldsDirectory);
            
            itemFactory = PowerMockito.mock(ItemFactory.class);
            when(itemFactory.getItemMeta(any())).thenReturn(PowerMockito.mock(ItemMeta.class));
            when(mockServer.getItemFactory()).thenReturn(itemFactory);

            // Return a fake PDF file.
            PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12",
                    "com.gordonfreemanq.sabre.SabrePlugin"));
            when(pdf.getAuthors()).thenReturn(new ArrayList<String>());

            sabrePlugin = PowerMockito.spy(new SabrePlugin(pluginLoader, mockServer, pdf, pluginDirectory, new File(pluginDirectory, "testPluginFile")));
            sabrePlugin.setDataAccess(Mockito.mock(IDataAccess.class));
            
            /*
            PowerMockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    return null; // don't run metrics in tests
                }
            }).when(sabrePlugin, "setupMetrics"); */

            // Let's let all MV files go to bin/test
            doReturn(pluginDirectory).when(sabrePlugin).getDataFolder();

            doReturn(true).when(sabrePlugin).isEnabled();
            doReturn(Util.logger).when(sabrePlugin).getLogger();
            sabrePlugin.setServerFolder(serverDirectory);

            // Add Core to the list of loaded plugins
            JavaPlugin[] plugins = new JavaPlugin[] { sabrePlugin };

            // Mock the Plugin Manager
            PluginManager mockPluginManager = PowerMockito.mock(PluginManager.class);
            when(mockPluginManager.getPlugins()).thenReturn(plugins);
            when(mockPluginManager.getPlugin("Sabre")).thenReturn(sabrePlugin);
            when(mockPluginManager.getPermission(anyString())).thenReturn(null);
            // Tell Buscript Vault is not available.
            //when(mockPluginManager.getPermission("Vault")).thenReturn(null);

            // Make some fake folders to fool the fake MV into thinking these worlds exist
            File worldNormalFile = new File(sabrePlugin.getServerFolder(), "world");
            Util.log("Creating world-folder: " + worldNormalFile.getAbsolutePath());
            worldNormalFile.mkdirs();
            File worldNetherFile = new File(sabrePlugin.getServerFolder(), "world_nether");
            Util.log("Creating world-folder: " + worldNetherFile.getAbsolutePath());
            worldNetherFile.mkdirs();
            File worldSkylandsFile = new File(sabrePlugin.getServerFolder(), "world_the_end");
            Util.log("Creating world-folder: " + worldSkylandsFile.getAbsolutePath());
            worldSkylandsFile.mkdirs();



            // Give the server some worlds
            when(mockServer.getWorld(anyString())).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    String arg;
                    try {
                        arg = (String) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorld(any(UUID.class))).thenAnswer(new Answer<World>() {
                @Override
                public World answer(InvocationOnMock invocation) throws Throwable {
                    UUID arg;
                    try {
                        arg = (UUID) invocation.getArguments()[0];
                    } catch (Exception e) {
                        return null;
                    }
                    return MockWorldFactory.getWorld(arg);
                }
            });

            when(mockServer.getWorlds()).thenAnswer(new Answer<List<World>>() {
                @Override
                public List<World> answer(InvocationOnMock invocation) throws Throwable {
                    return MockWorldFactory.getWorlds();
                }
            });

            when(mockServer.getPluginManager()).thenReturn(mockPluginManager);

            when(mockServer.createWorld(Matchers.isA(WorldCreator.class))).thenAnswer(
                    new Answer<World>() {
                        @Override
                        public World answer(InvocationOnMock invocation) throws Throwable {
                            WorldCreator arg;
                            try {
                                arg = (WorldCreator) invocation.getArguments()[0];
                            } catch (Exception e) {
                                return null;
                            }
                            // Add special case for creating null worlds.
                            // Not sure I like doing it this way, but this is a special case
                            if (arg.name().equalsIgnoreCase("nullworld")) {
                                return MockWorldFactory.makeNewNullMockWorld(arg.name(), arg.environment(), arg.type());
                            }
                            return MockWorldFactory.makeNewMockWorld(arg.name(), arg.environment(), arg.type());
                        }
                    });

            when(mockServer.unloadWorld(anyString(), anyBoolean())).thenReturn(true);

            // add mock scheduler
            BukkitScheduler mockScheduler = mock(BukkitScheduler.class);
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class), anyLong())).
            thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocation) throws Throwable {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                }});
            when(mockScheduler.scheduleSyncDelayedTask(any(Plugin.class), any(Runnable.class))).
            thenAnswer(new Answer<Integer>() {
                @Override
                public Integer answer(InvocationOnMock invocation) throws Throwable {
                    Runnable arg;
                    try {
                        arg = (Runnable) invocation.getArguments()[1];
                    } catch (Exception e) {
                        return null;
                    }
                    arg.run();
                    return null;
                }});
            when(mockServer.getScheduler()).thenReturn(mockScheduler);

            // Set server
            Field serverfield = JavaPlugin.class.getDeclaredField("server");
            serverfield.setAccessible(true);
            serverfield.set(sabrePlugin, mockServer);

            /*
            // Set buscript
            Buscript buscript = Mockito.spy(new Buscript(core));
            Field buscriptfield = MultiverseCore.class.getDeclaredField("buscript");
            buscriptfield.setAccessible(true);
            buscriptfield.set(core, buscript);
            when(buscript.getPlugin()).thenReturn(core);

            // Set worldManager
            WorldManager wm = Mockito.spy(new WorldManager(core));
            Field worldmanagerfield = MultiverseCore.class.getDeclaredField("worldManager");
            worldmanagerfield.setAccessible(true);
            worldmanagerfield.set(core, wm);

            // Set playerListener
            MVPlayerListener pl = Mockito.spy(new MVPlayerListener(core));
            Field playerlistenerfield = MultiverseCore.class.getDeclaredField("playerListener");
            playerlistenerfield.setAccessible(true);
            playerlistenerfield.set(core, pl);

            // Set entityListener
            MVEntityListener el = Mockito.spy(new MVEntityListener(core));
            Field entitylistenerfield = MultiverseCore.class.getDeclaredField("entityListener");
            entitylistenerfield.setAccessible(true);
            entitylistenerfield.set(core, el);

            // Set weatherListener
            MVWeatherListener wl = Mockito.spy(new MVWeatherListener(core));
            Field weatherlistenerfield = MultiverseCore.class.getDeclaredField("weatherListener");
            weatherlistenerfield.setAccessible(true);
            weatherlistenerfield.set(core, wl); */

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

            // Load Multiverse Core
            sabrePlugin.onLoad();

            // Enable it.
            sabrePlugin.onEnable();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean tearDown() {
    	/*
        List<MultiverseWorld> worlds = new ArrayList<MultiverseWorld>(core.getMVWorldManager()
                .getMVWorlds());
        for (MultiverseWorld world : worlds) {
            core.getMVWorldManager().deleteWorld(world.getName());
        } */

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
        MockWorldFactory.clearWorlds();

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
}