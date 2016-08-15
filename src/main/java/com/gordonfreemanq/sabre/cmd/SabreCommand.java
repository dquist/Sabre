package com.gordonfreemanq.sabre.cmd;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.CustomStringList;
import com.gordonfreemanq.sabre.util.TextUtil;


public abstract class SabreCommand
{
	protected final SabrePlugin plugin;
	protected final PlayerManager pm;
	protected final GroupManager gm;
	protected final BlockManager bm;
	protected final PearlManager pearls;
	protected final SabreConfig config;
	
	protected SabreGroup curGroup;
	
	private final Map<String, String> permissionDescriptions = new HashMap<String, String>();
	private final List<SabreCommand> subCommands = new ArrayList<SabreCommand>();
	
	// The different names this commands will react to  
	public final CustomStringList aliases = new CustomStringList();
	public final CustomStringList hiddenAliases = new CustomStringList();
	
	// Information on the args
	public final List<String> requiredArgs = new ArrayList<String>();
	public final LinkedHashMap<String, String> optionalArgs = new LinkedHashMap<String, String>();
	public boolean errorOnToManyArgs = true;
	
	// FIELD: Help Short
	// This field may be left blank and will in such case be loaded from the permissions node instead.
	// Thus make sure the permissions node description is an action description like "eat hamburgers" or "do admin stuff".
	private String helpShort;
	
	public final List<String> helpLong = new ArrayList<String>();
	public CommandVisibility visibility = CommandVisibility.VISIBLE;
	
	// Some information on permissions
	public boolean senderMustBePlayer;
	public String permission;
	
	public boolean senderMustConfirm;
	
	// Information available on execution of the command
	public CommandSender sender; // Will always be set
	public SabrePlayer me; // Will only be set when the sender is a player
	public boolean senderIsConsole;
	public List<String> args; // Will contain the arguments, or and empty list if there are none.
	public List<SabreCommand> commandChain = new ArrayList<SabreCommand>(); // The command chain used to execute this command
	
	/**
	 * Creates a new SabreCommand instance
	 */
	protected SabreCommand() {
		this.plugin = SabrePlugin.instance();
		this.config = plugin.config();
		this.pm = plugin.getPlayerManager();
		this.bm = plugin.getBlockManager();
		this.gm = plugin.getGroupManager();
		this.pearls = plugin.getPearlManager();
		
		this.permission = null;
		
		for(Permission permission : plugin.getDescription().getPermissions()) {
			this.permissionDescriptions.put(permission.getName(), permission.getDescription());
		}
	}
	
	/**
	 * Executes the command
	 * @param sender The command sender
	 * @param args The command arguments
	 * @param commandChain The command chain
	 */
	public void execute(CommandSender sender, List<String> args, List<SabreCommand> commandChain) {		
		// Set the execution-time specific variables
		this.sender = sender;
		if (sender instanceof Player) {
			Player p = (Player)sender;
			
			this.me = this.pm.getPlayerById(p.getUniqueId());
			this.senderIsConsole = false;
		} else {
			this.me = null;
			this.senderIsConsole = true;
		}
		this.args = args;
		this.commandChain = commandChain;
		
		// Sender must have permission for the root node of this command
		if ( !validSenderPermissions(sender, true)) {
			return;
		}

		// Find a matching sub-command
		if (args.size() > 0 ) {
			for (SabreCommand subCommand: this.subCommands) {
				if (subCommand.aliases.contains(args.get(0)) || subCommand.hiddenAliases.contains(args.get(0))) {
					args.remove(0);
					commandChain.add(this);
					subCommand.execute(sender, args, commandChain);
					return;
				}
			}
		}
		
		if ( !validCall(this.sender, this.args)) {
			return;
		}
		
		perform();
		
		if (senderMustConfirm) {
			//msg(Lang.ConfirmCommand, plugin.GetCommandAlias());
		}
	}
	
	/**
	 * Executes the command
	 * @param sender The command sender
	 * @param args The command arguments
	 */
	public void execute(CommandSender sender, List<String> args) {
		execute(sender, args, new ArrayList<SabreCommand>());
	}
	

	/**
	 * Performs the command
	 */
	public abstract void perform();
	
	/**
	 * Adds a sub-command
	 * @param subCommand the sub-command to add
	 */
	public void addSubCommand(SabreCommand subCommand) {
		subCommand.commandChain.addAll(this.commandChain);
		subCommand.commandChain.add(this);
		this.subCommands.add(subCommand);
	}
	
	/**
	 * Gets the sub-commands
	 * @return The sub-commands
	 */
	public List<SabreCommand> getSubCommands() {
		return this.subCommands;
	}
	
	/**
	 * Sets the short help string
	 * @param str The short help value
	 */
	protected void setHelpShort(String str) { 
		this.helpShort = str;
	}
	
	/**
	 * Gets the short help string
	 * @return The short help string
	 */
	protected String getHelpShort() {
		if (this.helpShort == null) {
			String pdesc = getPermissionDescription(this.permission);
			if (pdesc != null) {
				return pdesc;
			}
			return "*info unavailable*";
		}
		return this.helpShort;
	}
	
	
	/**
	 * This method validates that all prerequisites to perform this command has been met.
	 * @param sender The command sender
	 * @param args The command args
	 * @return true if the call if valid and can proceed
	 */
	protected boolean validCall(CommandSender sender, List<String> args) {
		if ( ! validSenderType(sender, true)) {
			return false;
		}
		
		if ( ! validSenderPermissions(sender, true)) {
			return false;
		}
		
		if ( !validArgs(args, sender)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the sender is the correct type (console/player)
	 * @param sender The sender to check
	 * @param informSenderIfNot Whether to inform the sender if not valid
	 * @return true if the sender is valid
	 */
	protected boolean validSenderType(CommandSender sender, boolean informSenderIfNot) {
		if (this.senderMustBePlayer && ! (sender instanceof Player)) {
			if (informSenderIfNot) {
				msg(Lang.commandSenderMustBePlayer);
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the sender has valid permissions
	 * @param sender The sender to check
	 * @param informSenderIfNot Whether to inform the sender if no permission
	 * @return true if the sender has permission
	 */
	protected boolean validSenderPermissions(CommandSender sender, boolean informSenderIfNot) {
		if (this.permission == null) {
			return true;
		}
		return senderHasPerm(sender, this.permission, this.visibility, informSenderIfNot);
	}
	
	/**
	 * Checks if the valid arguments were sent for the command
	 * @param args The args to check
	 * @param sender The command sender
	 * @return true if the commands are valid
	 */
	protected boolean validArgs(List<String> args, CommandSender sender)
	{
		if (args.size() < this.requiredArgs.size())
		{
			if (sender != null)
			{
				msg(Lang.commandToFewArgs);
				sender.sendMessage(this.getUsageTemplate());
			}
			return false;
		}
		
		if (args.size() > this.requiredArgs.size() + this.optionalArgs.size() && this.errorOnToManyArgs)
		{
			if (sender != null)
			{
				// Get the to many string slice
				List<String> theToMany = args.subList(this.requiredArgs.size() + this.optionalArgs.size(), args.size());
				msg(Lang.commandToManyArgs, TextUtil.implode(theToMany, " "));
				sender.sendMessage(this.getUsageTemplate());
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the valid arguments were sent for the command
	 * @param args The args to check
	 * @return true if the args are valid
	 */
	protected boolean validArgs(List<String> args) {
		return this.validArgs(args, null);
	}
	
	/**
	 * Gets the complete usage template for a command chain
	 * @param commandChain The command chain
	 * @param addShortHelp Whether to add a short help description
	 * @return The full command help
	 */
	public String getUseageTemplate(List<SabreCommand> commandChain, boolean addShortHelp) {
		StringBuilder ret = new StringBuilder();
		ret.append(plugin.txt().parseTags("<c>"));
		ret.append('/');
		
		for (SabreCommand mc : commandChain) {
			ret.append(TextUtil.implode(mc.aliases, ","));
			ret.append(' ');
		}
		
		ret.append(TextUtil.implode(this.aliases, ","));
		
		List<String> args = new ArrayList<String>();
		
		for (String requiredArg : this.requiredArgs) {
			args.add("<"+requiredArg+">");
		}
		
		for (Entry<String, String> optionalArg : this.optionalArgs.entrySet()) {
			String val = optionalArg.getValue();
			if (val == null) {
				val = "";
			} else {
				val = "="+val;
			}
			args.add("["+optionalArg.getKey()+val+"]");
		}
		
		if (args.size() > 0) {
			ret.append(plugin.txt().parseTags("<p> "));
			ret.append(TextUtil.implode(args, " "));
		}
		
		if (addShortHelp) {
			ret.append(plugin.txt().parseTags(" <i>"));
			ret.append(this.getHelpShort());
		}
		
		return ret.toString();
	}
	
	/**
	 * Gets the usage template for the command
	 * @param addShortHelp whether to add a short help description
	 * @return The usage template
	 */
	public String getUsageTemplate(boolean addShortHelp) {
		return getUseageTemplate(this.commandChain, addShortHelp);
	}
	
	/**
	 * Gets the usage template for the command
	 * @return The usage template
	 */
	public String getUsageTemplate() {
		return getUsageTemplate(false);
	}

	/**
	 * Formats and sends a message to the player
	 * @param str The message to send 
	 * @param args The message arguments
	 */
	protected void msg(String str, Object... args) {
		if (senderIsConsole) {
			String message = plugin.txt().parse(str, args);
			sender.sendMessage(message);
		} else {
			me.msg(str, args);
		}
	}
	
	/**
	 * Sends a list of messages to the player
	 * @param msgs The messages to send
	 */
	protected void msg(List<String> msgs) {
		for(String msg : msgs) {
			this.msg(msg);
		}
	}
	
	/**
	 * Gets whether an argument is set
	 * @param index The index to check
	 * @return true if the argument has a value
	 */
	protected boolean argIsSet(int idx) {
		if (this.args.size() < idx+1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Gets an argument as a string
	 * @param index The index
	 * @param def The default value
	 * @return The string argument
	 */
	protected String argAsString(int index, String def) {
		if (this.args.size() < index + 1) {
			return def;
		}
		return this.args.get(index);
	}
	
	/**
	 * Gets an argument as a string
	 * @param index The index
	 * @return The string argument
	 */
	protected String argAsString(int idx) {
		return this.argAsString(idx, null);
	}
	
	/**
	 * Gets an integer value from a string
	 * @param str The string value
	 * @param def The default value
	 * @return The integer argument
	 */
	protected Integer strAsInt(String str, Integer def) {
		if (str == null) return def;
		try {
			Integer ret = Integer.parseInt(str);
			return ret;
		} catch (Exception e) {
			return def;
		}
	}
	
	/**
	 * Gets an argument as an integer
	 * @param index The index
	 * @param def The default value
	 * @return The integer argument
	 */
	protected Integer argAsInt(int index, Integer def) {
		return strAsInt(this.argAsString(index), def);
	}
	
	/**
	 * Gets an argument as a integer
	 * @param index The index
	 * @return The integer argument
	 */
	protected Integer argAsInt(int index) {
		return this.argAsInt(index, null);
	}
	
	/**
	 * Gets an default value from a string
	 * @param str The string value
	 * @param def The default value
	 * @return The double argument
	 */
	protected Double strAsDouble(String str, Double def) {
		if (str == null) return def;
		try {
			Double ret = Double.parseDouble(str);
			return ret;
		} catch (Exception e) {
			return def;
		}
	}
	
	/**
	 * Gets an argument as a double
	 * @param index The index
	 * @param def The default value
	 * @return The double argument
	 */
	protected Double argAsDouble(int idx, Double def) {
		return strAsDouble(this.argAsString(idx), def);
	}
	
	/**
	 * Gets an argument as a double
	 * @param index The index
	 * @return The double argument
	 */
	protected Double argAsDouble(int idx) {
		return this.argAsDouble(idx, null);
	}
	
	/**
	 * Gets an boolean value from a string
	 * Values that qualify as a 'true' value are [y, t, on, +, 1]
	 * @param str The string value
	 * @return The boolean argument
	 */
	protected Boolean strAsBool(String str) {
		str = str.toLowerCase();
		if (str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets an argument as a boolean
	 * @param index The index
	 * @param def The default value
	 * @return The boolean argument
	 */
	protected Boolean argAsBool(int idx, Boolean def) {
		String str = this.argAsString(idx);
		if (str == null) {
			return def;
		}
		
		return strAsBool(str);
	}
	
	/**
	 * Gets an argument as a boolean
	 * @param index The index
	 * @return The boolean argument
	 */
	protected Boolean argAsBool(int index) {
		return this.argAsBool(index, false);
	}
	
	/**
	 * Gets a player instance from a string
	 * @param name The name to find
	 * @return The best match player instance
	 */
	protected SabrePlayer strAsPlayer(String name) {
		SabrePlayer ret = null;

		if (name != null) {
			// Try to get exact player
			ret = pm.getPlayerByName(name);
			
			if (ret == null) {
				// Try to get a match from the players in the current command group
				if (curGroup != null) {
					SabreMember m = TextUtil.getBestNamedMatch(curGroup.getMembers(), name, me.getName());
					if (m != null) {
						ret = m.getPlayer();
					}
				}
				
				// If still no match, check online players
				if (ret == null) {
					ret = TextUtil.getBestNamedMatch(pm.getOnlinePlayers(), name, me.getName());
				}
				
				// If still no match, check all players
				if (ret == null) {
					ret = TextUtil.getBestNamedMatch(pm.getPlayers(), name, me.getName());
				}
			}
		}

		return ret;
	}
	
	/**
	 * Gets an argument as a player
	 * @param index The argument index
	 * @return The player instance
	 */
	protected SabrePlayer argAsPlayer(int index) {
		return this.strAsPlayer(argAsString(index));
	}
	
	
	/**
	 * Checks if a group exists
	 * @param owner The group owner
	 * @param name The group name
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup checkGroupExists(SabrePlayer owner, String searchName, boolean searchSimilar) {
		String groupName = searchName;
		
		// If the search name contains the '#' character, split up the group name and owner name
		if (searchName.contains("#")) {
			String[] split = searchName.split("#");
			if (split.length > 0) {
				groupName = split[0];
			}
			if (split.length > 1) {
				SabrePlayer searchOwner = pm.getPlayerByName(split[1]);
				if (searchOwner != null) {
					owner = searchOwner;
				}
			}
		}
		
		final String queryName = groupName;
		
		// First try to find the group by owner and name
		curGroup = gm.getGroupByName(owner, groupName);
		
		// If no match, search all the groups that this player is a member of
		if (curGroup == null) {
			Collection<SabreGroup> playerGroups = gm.getPlayerGroups(me);
			curGroup = playerGroups.stream().filter(g -> g.getName().equalsIgnoreCase(queryName)).findFirst().orElse(null);

			// If no exact hit, try to find the best match group that the player is a member of
			if (curGroup == null && searchSimilar) {
				curGroup = TextUtil.getBestNamedMatch(playerGroups, groupName, "");
			}
		}

		// No group found
		if (curGroup == null) {
			msg(Lang.groupNotExist, searchName);
		}
		
		return curGroup;
	}

	
	/**
	 * Checks if a group exists
	 * @param name The group name
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup checkGroupExists(String name, boolean searchSimilar) {
		return checkGroupExists(me, name, searchSimilar);
	}
	
	
	/**
	 * Gets the forbidden message for a command.
	 * @param perm The permission to check
	 * @return The permission forbidden message
	 */
	private String getForbiddenMessage(String perm) {
		return plugin.txt().parse(Lang.permForbidden, getPermissionDescription(perm));
	}

	
	/**
	 * Gets the permission description for the command
	 * @param perm The permission to check
	 * @return The permission description
	 */
	private String getPermissionDescription(String perm) {
		String desc = permissionDescriptions.get(perm);
		if (desc == null) {
			return Lang.permDoThat;
		}
		return desc;
	}
	
	
	/**
	 * Checks if a sender has a certain permission
	 * @param me The command sender
	 * @param perm The permission to check
	 * @return true if the sender has the permission
	 */
	private boolean senderHasPerm(CommandSender me, String perm) {
		if (me == null) {
			return false;
		}
		
		if ( ! (me instanceof Player)) {
			return me.hasPermission(perm);
		}

		return me.hasPermission(perm);
	}
	
	
	/**
	 * Checks if a sender has a certain permission
	 * @param me The command sender
	 * @param perm The permission to check
	 * @param visiblity The command visibility
	 * @param informSenderIfNot Whether to inform the sender of invalid perms
	 * @return true if the sender has the permission
	 */
	private boolean senderHasPerm(CommandSender me, String perm, CommandVisibility visiblity, boolean informSenderIfNot) {
		if (senderHasPerm(me, perm)) {
			return true;
		}
		else if (visiblity != CommandVisibility.VISIBLE) {
			me.sendMessage(Lang.unknownCommand);
		}
		else if (informSenderIfNot && me != null) {
			me.sendMessage(this.getForbiddenMessage(perm));
		}
		return false;
	}
	
	/**
	 * Formats a string through the text tag parser
	 * @param str The string to format
	 * @return The formatted string
	 */
	protected String parse(String str) {
		return plugin.txt().parse(str);
	}
	
	/**
	 * Formats a string through the text tag parser
	 * @param str The string to format
	 * @param args The string arguments
	 * @return The formatted string
	 */
	protected String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
}
