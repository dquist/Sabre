package com.gordonfreemanq.sabre;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import com.comphenix.protocol.ProtocolLibrary;
import com.gordonfreemanq.sabre.blocks.BlockListener;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SignHandler;
import com.gordonfreemanq.sabre.chat.GlobalChat;
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.chat.ServerBroadcast;
import com.gordonfreemanq.sabre.cmd.CmdAutoHelp;
import com.gordonfreemanq.sabre.cmd.CommandList;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.data.IDataAccess;
import com.gordonfreemanq.sabre.data.MongoConnector;
import com.gordonfreemanq.sabre.factory.FactoryConfig;
import com.gordonfreemanq.sabre.factory.FactoryListener;
import com.gordonfreemanq.sabre.factory.FactoryWorker;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.prisonpearl.PearlListener;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.prisonpearl.PearlWorker;
import com.gordonfreemanq.sabre.snitch.SnitchListener;
import com.gordonfreemanq.sabre.snitch.SnitchLogger;
import com.gordonfreemanq.sabre.util.CombatInterface;
import com.gordonfreemanq.sabre.util.CombatTagPlusManager;
import com.gordonfreemanq.sabre.util.PermUtil;
import com.gordonfreemanq.sabre.util.PlayerSpawner;
import com.gordonfreemanq.sabre.util.TextUtil;
import com.gordonfreemanq.sabre.util.VanishApi;


public class SabrePlugin extends JavaPlugin
{
	public final static String version = "0.2.0";
	
	// The global instance
	private static SabrePlugin instance;

	private final SabreConfig config = new SabreConfig(this.getConfig());
	private final IDataAccess dataAccess = new MongoConnector(this);
	private final PlayerManager playerManager = new PlayerManager(dataAccess);
	private final BlockManager blockManager = new BlockManager(this, dataAccess);
	private final GroupManager groupManager = new GroupManager(playerManager, blockManager, dataAccess);
	private final PearlManager pearlManager = new PearlManager(this, dataAccess);
	private final PlayerListener playerListener = new PlayerListener(this, playerManager);
	private final BlockListener blockListener = new BlockListener(this, playerManager, blockManager, config);
	private final SnitchLogger snitchLogger = new SnitchLogger(playerManager, dataAccess);
	private final SnitchListener snitchListener = new SnitchListener(this, playerManager, blockManager);
	private final PearlListener pearlListener = new PearlListener(this, playerManager, pearlManager);
	private final PlayerSpawner playerSpawner = new PlayerSpawner(playerManager, blockManager, config);
	private final GlobalChat globalChat = new GlobalChat(this, config);
	private final ServerBroadcast serverBcast = new ServerBroadcast(playerManager);
	private final SabreTweaks sabreTweaks = new SabreTweaks(this, config);
	private final FactoryListener factoryListener = new FactoryListener(playerManager, blockManager);
	private final FactoryConfig factoryConfig = new FactoryConfig(this);
	private final CustomItems customItems = new CustomItems(this);
	private final CombatInterface combatTag = new CombatTagPlusManager();
	private final StatsTracker statsTracker = new StatsTracker(this, playerManager);
	private final SignHandler signHandler = new SignHandler(this);
	private final FactoryWorker factoryWorker = new FactoryWorker();
	private final PearlWorker pearlWorker = new PearlWorker(this, pearlManager, config);
	private final VanishApi vanishApi = new VanishApi();
	private final CommandList commandList = new CommandList();
	private final SabreLogger logger = new SabreLogger(this);
	private final PermUtil perms = new PermUtil(this);
	private final TextUtil txt = new TextUtil();
	
	private boolean pluginLoadError = false;

	/**
	 * Creates a new SabrePlugin instance
	 */
	public SabrePlugin() {
		super();
	}
	
    /**
     * Constructor for unit testing.
     * @deprecated
     */
    @Deprecated
    public SabrePlugin(PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, server, description, dataFolder, file);
    }


	/**
	 * Gets the global instance
	 */
	public static SabrePlugin instance() { 
		return instance;
	}
	
	
	@Override
	public void saveConfig() {
		config.save();
		super.saveConfig();
	}

	/**
	 * Reads configuration and loads data
	 */
	private void loadData() {
		
		try {
			config.read();
			customItems.reload();
			factoryConfig.reload();
			
			try {
				dataAccess.connect();
			} catch (Exception ex) {
				log(Level.SEVERE, "* * * Failed to connect to MongoDB database! * * * ");
				pluginLoadError = true;
			}
			
			playerManager.load();
			groupManager.load();
			pearlManager.load();
			
		} catch(Exception ex) {
			pluginLoadError = true;
			log(Level.SEVERE, ex.toString());
			ex.printStackTrace();
		}
	}
	
    @Override
    public void onLoad() {
		instance = this;
		
        // Create our DataFolder
        getDataFolder().mkdirs();
        
		pluginLoadError = false;
    }


	/**
	 * Bukkit plugin enable function
	 */
	@Override
	public void onEnable() {
		log("=== ENABLE START ===");
		long timeEnableStart = System.currentTimeMillis();
		
		// Read config and load data
		loadData();

		// Register Commands
		commandList.registerCommands();
		
		// Other initialization
		sabreTweaks.initialize();
		combatTag.initialize();
		vanishApi.initialize();

		// Register events
		getServer().getPluginManager().registerEvents(playerListener, this);
		getServer().getPluginManager().registerEvents(playerListener, this);
		playerListener.handleOnlinePlayers();
		blockListener.handleLoadedChunks();
		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(snitchListener, this);
		getServer().getPluginManager().registerEvents(pearlListener, this);
		getServer().getPluginManager().registerEvents(sabreTweaks, this);
		getServer().getPluginManager().registerEvents(factoryListener, this);
		ProtocolLibrary.getProtocolManager().addPacketListener(signHandler);
		
		// Load running factories - perform this AFTER blockListener.handleLoadedChunks is called
		blockManager.loadRunningFactories();
		
		// Start the various tasks
		factoryWorker.start();
		pearlWorker.start();
		statsTracker.start();

		// Startup complete!
		log("=== ENABLE DONE (Took "+(System.currentTimeMillis() - timeEnableStart)+"ms) ===");
	}


	/**
	 * Bukkit plugin disable function
	 */
	@Override
	public void onDisable() {
		dataAccess.disconect();		
		log("=== Sabre Plugin Disabled ===");
	}


	/**
	 * Handles a bukkit command event
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		if (pluginLoadError) {
			return false;
		}
		
		for (SabreCommand c : commandList) {
			if (c.aliases.contains(cmd.getLabel())) {

				// Set the label to the default alias
				cmd.setLabel(c.aliases.get(0));
				
				c.execute(sender, new ArrayList<String>(Arrays.asList(args)));
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Handles a tab complete event
	 */
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
		if (pluginLoadError) {
			return null;
		}

		return null;
	}
	
    
    /**
     * Gets the configuration class
     * @return The configuration
     */
    public SabreConfig config() {
    	return this.config;
    }
	
	/**
	 * Gets the player manager
	 * @return The player manager
	 */
	public PlayerManager getPlayerManager() {
		return this.playerManager;
	}
	
	/**
	 * Gets the block manager
	 * @return The block manager
	 */
	public BlockManager getBlockManager() {
		return this.blockManager;
	}
	
	/**
	 * Gets the prison pearl manager
	 * @return The prison pearl manager
	 */
	public PearlManager getPearlManager() {
		return this.pearlManager;
	}

	/**
	 * Gets the group manager
	 * @return The group manager
	 */
	public GroupManager getGroupManager() {
		return this.groupManager;
	}
	
	/**
	 * Gets the group manager
	 * @return The group manager
	 */
	public CombatInterface getCombatTag() {
		return this.combatTag;
	}
	
	/**
	 * Gets the vanish API
	 * @return The vanish API
	 */
	public VanishApi getVanishApi() {
		return this.vanishApi;
	}
	
	/**
	 * Returns the global chat instance
	 * @return The global chat instance
	 */
	public IChatChannel getGlobalChat() {
		return this.globalChat;
	}
	
	/**
	 * Gets the server broadcast instance
	 * @return the Server broadcast instance
	 */
	public ServerBroadcast getServerBroadcast() {
		return this.serverBcast;
	}

	/**
	 * Gets the Data Access Object
	 * @return The Data Access Object
	 */
	public IDataAccess getDataAccess() {
		return dataAccess;
	}

	/**
	 * Gets the auto-help command
	 * @return The auto-help command
	 */
	public CmdAutoHelp getCmdAutoHelp() { 
		return commandList.getAutoHelp();
	}
	
	/**
	 * Gets the player listener
	 * @return The player listener
	 */
	public PlayerListener getPlayerListener() { 
		return this.playerListener;
	}
	
	/**
	 * Gets the snitch logger
	 * @return The snitch logger
	 */
	public SnitchLogger getSnitchLogger() {
		return this.snitchLogger;
	}
	
	/**
	 * Gets whether the plugin is loaded
	 * @return true if the plugin is loaded
	 */
	public boolean getSuccessfulLoad() {
		return !this.pluginLoadError;
	}
	
	/**
	 * Gets the player spawner
	 * @return The player spawner
	 */
	public PlayerSpawner getSpawner() {
		return this.playerSpawner;
	}
	
	/**
	 * Gets the factory config
	 * @return The factory config
	 */
	public FactoryConfig getFactoryConfig() {
		return this.factoryConfig;
	}
	
	/**
	 * Gets the custom items
	 * @return The custom items
	 */
	public CustomItems getCustomItems() {
		return this.customItems;
	}
	
	/**
	 * Gets the factory worker
	 * @return The factory worker
	 */
	public FactoryWorker getFactoryWorker() {
		return this.factoryWorker;
	}
	
	/**
	 * Gets the sign handler
	 * @return The sign handler
	 */
	public SignHandler getSignHandler() {
		return this.signHandler;
	}
	
	/**
	 * Gets the text utility
	 * @return The text utility
	 */
	public TextUtil txt() {
		return this.txt;
	}
	
	/**
	 * Gets the perms utility
	 * @return The perms utility
	 */
	public PermUtil perms() {
		return this.perms;
	}

	
	/**
	 * Gets the logger
	 * @return The logger
	 */
	public SabreLogger logger() {
		return this.logger;
	}


	public static void log(Object msg) {
		log(Level.INFO, msg);
	}

	public static void log(String str, Object... args) {
		log(Level.INFO, instance().txt.parse(str, args));
	}

	public static void log(Level level, String str, Object... args) {
		log(level, instance().txt.parse(str, args));
	}

	public static void log(Level level, Object msg) {
		Bukkit.getLogger().log(level, String.format("[%s] %s", SabrePlugin.instance().getDescription().getFullName(), msg));
	}
}
