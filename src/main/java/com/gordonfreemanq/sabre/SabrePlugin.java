package com.gordonfreemanq.sabre;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.Server;

import com.comphenix.protocol.ProtocolLibrary;
import com.gordonfreemanq.sabre.blocks.BlockListener;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SignHandler;
import com.gordonfreemanq.sabre.chat.GlobalChat;
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.chat.ServerBroadcast;
import com.gordonfreemanq.sabre.cmd.CmdAdminBan;
import com.gordonfreemanq.sabre.cmd.CmdAdminFly;
import com.gordonfreemanq.sabre.cmd.CmdAdminGamemode;
import com.gordonfreemanq.sabre.cmd.CmdAdminGive;
import com.gordonfreemanq.sabre.cmd.CmdAdminMore;
import com.gordonfreemanq.sabre.cmd.CmdAdminRespawn;
import com.gordonfreemanq.sabre.cmd.CmdAdminRoot;
import com.gordonfreemanq.sabre.cmd.CmdAdminUnban;
import com.gordonfreemanq.sabre.cmd.CmdAdminVanish;
import com.gordonfreemanq.sabre.cmd.CmdAutoHelp;
import com.gordonfreemanq.sabre.cmd.CmdBuildAcid;
import com.gordonfreemanq.sabre.cmd.CmdBuildBypass;
import com.gordonfreemanq.sabre.cmd.CmdBuildFortify;
import com.gordonfreemanq.sabre.cmd.CmdBuildInfo;
import com.gordonfreemanq.sabre.cmd.CmdBuildOff;
import com.gordonfreemanq.sabre.cmd.CmdBuildReinforce;
import com.gordonfreemanq.sabre.cmd.CmdChat;
import com.gordonfreemanq.sabre.cmd.CmdChatIgnore;
import com.gordonfreemanq.sabre.cmd.CmdChatMe;
import com.gordonfreemanq.sabre.cmd.CmdChatMsg;
import com.gordonfreemanq.sabre.cmd.CmdChatReply;
import com.gordonfreemanq.sabre.cmd.CmdChatSay;
import com.gordonfreemanq.sabre.cmd.CmdChatServer;
import com.gordonfreemanq.sabre.cmd.CmdHelp;
import com.gordonfreemanq.sabre.cmd.CmdRoot;
import com.gordonfreemanq.sabre.cmd.CmdSpeed;
import com.gordonfreemanq.sabre.cmd.CmdTeleport;
import com.gordonfreemanq.sabre.cmd.CmdTeleportHere;
import com.gordonfreemanq.sabre.cmd.CmdTest;
import com.gordonfreemanq.sabre.cmd.factory.CmdFactory;
import com.gordonfreemanq.sabre.cmd.pearl.CmdPearl;
import com.gordonfreemanq.sabre.cmd.snitch.CmdSnitch;
import com.gordonfreemanq.sabre.core.AbstractSabrePlugin;
import com.gordonfreemanq.sabre.core.SabreBaseCommand;
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
import com.gordonfreemanq.sabre.util.VanishApi;


public class SabrePlugin extends AbstractSabrePlugin
{
	public final static String version = "0.1.12";
	
	private static SabrePlugin instance;

	private PlayerManager playerManager;
	private GroupManager groupManager;
	private BlockManager blockManager;
	private IDataAccess db;
	private HashSet<SabreBaseCommand<?>> baseCommands;
	private CmdAutoHelp cmdAutoHelp;
	private GlobalChat globalChat;
	private ServerBroadcast serverBcast;
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private SnitchLogger snitchLogger;
	private SnitchListener snitchListener;
	private PearlManager pearlManager;
	private PearlListener pearlListener;
	private SabreConfig config;
	private SignHandler signHandler;
	private StatsTracker statsTracker;
	private SabreTweaks sabreTweaks;
	private FactoryListener factoryListener;
	private CustomItems customItems;
	private FactoryConfig factoryConfig;
	private FactoryWorker factoryWorker;
	private PearlWorker pearlWorker;
	private CombatInterface combatTag;
	private VanishApi vanishApi;
	
	private File serverFolder = new File(System.getProperty("user.dir"));
	
	/**
	 * Gets the player manager
	 * @return The player manager
	 */
	public PlayerManager getPlayerManager() {
		return this.playerManager;
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
	 * Returns the global configuration instance
	 * @return The global configuration instance
	 */
	public SabreConfig getSabreConfig() {
		return this.config;
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
		return this.db;
	}

	
	/**
	 * Sets the Data Access Object
	 * @return The Data Access Object
	 */
	public void setDataAccess(IDataAccess db) {
		this.db = db;
	}

	
	/**
	 * Gets the auto-help command
	 * @return The auto-help command
	 */
	public CmdAutoHelp getCmdAutoHelp() { 
		return cmdAutoHelp;
	}

	/**
	 * @brief SabrePlugin constructor 
	 */
	public SabrePlugin() {
	}
	
    /**
     * This is for unit testing.
     */
    public SabrePlugin(PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, server, description, dataFolder, file);
    }


	public static SabrePlugin getPlugin() { 
		return instance;
	}

	public String GetCommandAlias() {
		return "f";
	}
	
	
	/**
	 * Reloads the file configuration
	 */
	public void loadConfig() {
		config = SabreConfig.load(this.getConfig());
		saveConfig();
	}
	
	
	@Override
	public void saveConfig() {
		if (config != null) {
			//config.save();
			//super.saveConfig();
		}
	}

	/**
	 * Connects to the database and loads the data
	 */
	private void loadData() {
		
		try {
			customItems.reload();
			factoryConfig.reload();
			
			db.connect();
			playerManager.load();
			groupManager.load();
			pearlManager.load();
			
			
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}


	/**
	 * Bukkit plugin enable function
	 */
	@Override
	public void onEnable() {
		// Base plugin
		if (!super.preEnable()) {
			return;
		}
		
		instance = this;
		
		// Load config
		loadConfig();
		
		// Create objects
		if (this.db == null) {
			this.db = new MongoConnector(this, config);
		}
		
		this.playerManager = new PlayerManager(db, this);
		this.groupManager = new GroupManager(playerManager, db, this);
		this.blockManager = new BlockManager(db);
		this.globalChat = new GlobalChat(playerManager, config);
		this.serverBcast = new ServerBroadcast(playerManager);
		this.playerListener = new PlayerListener(playerManager, globalChat, this);
		
		getServer().getPluginManager().registerEvents(playerListener, this);
		
		this.blockListener = new BlockListener(playerManager, blockManager, config, this);
		this.snitchLogger = new SnitchLogger(db, playerManager);
		this.snitchListener = new SnitchListener(snitchLogger);
		this.pearlManager = new PearlManager(db, config);
		this.pearlListener = new PearlListener(pearlManager, playerManager);
		this.sabreTweaks = new SabreTweaks(config);
		this.factoryListener = new FactoryListener(playerManager, blockManager);
		this.factoryConfig = new FactoryConfig();
		this.customItems = new CustomItems();
		this.combatTag = new CombatTagPlusManager();
		
		// Try to connect to the database and load the data
		try {
			this.loadData();
			playerListener.setPluginLoaded(true);
		} catch(Exception ex) {
			this.log(Level.SEVERE, "Failed to connect to MongoDB database!");
			throw ex;
		}


		// Add Commands
		this.cmdAutoHelp = new CmdAutoHelp();
		this.baseCommands = new HashSet<SabreBaseCommand<?>>();
		baseCommands.add(new CmdRoot());
		baseCommands.add(new CmdPearl());
		baseCommands.add(new CmdFactory());
		baseCommands.add(new CmdSnitch());
		baseCommands.add(new CmdChat());
		baseCommands.add(new CmdChatMe());
		baseCommands.add(new CmdChatMsg());
		baseCommands.add(new CmdChatReply());
		baseCommands.add(new CmdChatServer());
		baseCommands.add(new CmdChatSay());
		baseCommands.add(new CmdChatIgnore());
		baseCommands.add(new CmdBuildBypass());
		baseCommands.add(new CmdBuildFortify());
		baseCommands.add(new CmdBuildInfo());
		baseCommands.add(new CmdBuildReinforce());
		baseCommands.add(new CmdBuildOff());
		baseCommands.add(new CmdBuildAcid());
		baseCommands.add(new CmdHelp());
		

		// Admin commands
		baseCommands.add(new CmdAdminRoot());
		baseCommands.add(new CmdSpeed());
		baseCommands.add(new CmdTest());
		baseCommands.add(new CmdTeleport());
		baseCommands.add(new CmdTeleportHere());
		baseCommands.add(new CmdAdminFly());
		baseCommands.add(new CmdAdminVanish());
		baseCommands.add(new CmdAdminGamemode());
		baseCommands.add(new CmdAdminRespawn());
		baseCommands.add(new CmdAdminBan());
		baseCommands.add(new CmdAdminUnban());
		baseCommands.add(new CmdAdminMore());
		baseCommands.add(new CmdAdminGive());
		
		factoryWorker = new FactoryWorker();
		factoryWorker.start();

		playerListener.handleOnlinePlayers();
		blockListener.handleLoadedChunks();
		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(snitchListener, this);
		getServer().getPluginManager().registerEvents(pearlListener, this);
		getServer().getPluginManager().registerEvents(sabreTweaks, this);
		getServer().getPluginManager().registerEvents(factoryListener, this);
		signHandler = new SignHandler();
		//ProtocolLibrary.getProtocolManager().addPacketListener(signHandler); // TODO ProtocolLibrary
		statsTracker = new StatsTracker(playerManager);
		statsTracker.start();
		
		// Load the running factories
		blockManager.loadRunningFactories();
		
		this.sabreTweaks.registerTimerForPearlCheck();
		
		pearlWorker = new PearlWorker(pearlManager, config);
		pearlWorker.start();

		postEnable();
		this.loadSuccessful = true;
		this.playerListener.setPluginLoaded(true);
		this.vanishApi = new VanishApi();
	}


	/**
	 * Bukkit plugin disable function
	 */
	@Override
	public void onDisable()
	{
		//statsTracker.cancel();
		playerListener.setPluginLoaded(false);
		db.disconect();
		
		// Save the config
		saveConfig();
	}


	/**
	 * Handles a bukkit command event
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args)
	{		
		for (SabreBaseCommand<?> c : baseCommands) {
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
		if (!loadSuccessful) {
			return null;
		}

		return null;
	}
	
	
    /**
     * Gets the server's root-folder
     * @return The server's root-folder
     */
    public File getServerFolder() {
        return serverFolder;
    }
    
    
    /**
     * Sets this server's root-folder.
     * @param newServerFolder The new server-root
     */
    public void setServerFolder(File newServerFolder) {
        if (!newServerFolder.isDirectory()) {
            throw new IllegalArgumentException("That's not a folder!");
        }

        this.serverFolder = newServerFolder;
    }
}
