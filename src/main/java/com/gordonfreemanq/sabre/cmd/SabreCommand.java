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
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.CustomStringList;
import com.gordonfreemanq.sabre.util.TextUtil;


public abstract class SabreCommand
{
	public final SabrePlugin plugin;
	protected final PlayerManager pm;
	protected final GroupManager gm;
	protected final BlockManager bm;
	protected final PearlManager pearls;
	protected final SabreConfig config;
	
	protected SabreGroup curGroup;
	protected IChatChannel gc;
	
	private final Map<String, String> permissionDescriptions = new HashMap<String, String>();
	
	
	// The sub-commands to this command
	public List<SabreCommand> subCommands;
	public void addSubCommand(SabreCommand subCommand)
	{
		subCommand.commandChain.addAll(this.commandChain);
		subCommand.commandChain.add(this);
		this.subCommands.add(subCommand);
	}
	
	// The different names this commands will react to  
	public CustomStringList aliases;
	public CustomStringList hiddenAliases;
	
	// Information on the args
	public List<String> requiredArgs;
	public LinkedHashMap<String, String> optionalArgs;
	public boolean errorOnToManyArgs = true;
	
	// FIELD: Help Short
	// This field may be left blank and will in such case be loaded from the permissions node instead.
	// Thus make sure the permissions node description is an action description like "eat hamburgers" or "do admin stuff".
	private String helpShort;
	public void setHelpShort(String val) { this.helpShort = val; }
	public String getHelpShort()
	{
		if (this.helpShort == null)
		{ 
			String pdesc = getPermissionDescription(this.permission);
			if (pdesc != null)
			{
				return pdesc;
			}
			return "*info unavailable*";
		}
		return this.helpShort;
	}
	
	public List<String> helpLong;
	public CommandVisibility visibility;
	
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
	
	public SabreCommand()
	{
		this.plugin = SabrePlugin.instance();
		this.config = plugin.config();
		this.pm = plugin.getPlayerManager();
		this.bm = plugin.getBlockManager();
		this.gm = plugin.getGroupManager();
		this.pearls = plugin.getPearlManager();
		
		this.permission = null;
		
		this.subCommands = new ArrayList<SabreCommand>();
		this.aliases = new CustomStringList();
		this.hiddenAliases = new CustomStringList();
		
		this.requiredArgs = new ArrayList<String>();
		this.optionalArgs = new LinkedHashMap<String, String>();
		
		this.helpShort = null;
		this.helpLong = new ArrayList<String>();
		this.visibility = CommandVisibility.VISIBLE;
		
		for(Permission permission : plugin.getDescription().getPermissions()) {
			this.permissionDescriptions.put(permission.getName(), permission.getDescription());
		}
	}
	
	// The commandChain is a list of the parent command chain used to get to this command.
	public void execute(CommandSender sender, List<String> args, List<SabreCommand> commandChain)
	{		
		// Set the execution-time specific variables
		this.sender = sender;
		if (sender instanceof Player)
		{
			Player p = (Player)sender;
			
			this.me = this.pm.getPlayerById(p.getUniqueId());
			this.senderIsConsole = false;
		}
		else
		{
			this.me = null;
			this.senderIsConsole = true;
		}
		this.args = args;
		this.commandChain = commandChain;
		
		// Permission for the root applies to all sub-commands
		if ( ! validSenderPermissions(sender, true))
		{
			return;
		}

		// Is there a matching sub command?
		if (args.size() > 0 )
		{
			for (SabreCommand subCommand: this.subCommands)
			{
				if (subCommand.aliases.contains(args.get(0)) || subCommand.hiddenAliases.contains(args.get(0)))
				{
					args.remove(0);
					commandChain.add(this);
					subCommand.execute(sender, args, commandChain);
					return;
				}
			}
		}
		
		if ( ! validCall(this.sender, this.args)) {
			return;
		}
		
		perform();
		
		if (senderMustConfirm) {
			//msg(Lang.ConfirmCommand, plugin.GetCommandAlias());
		}
	}
	
	public void execute(CommandSender sender, List<String> args)
	{
		execute(sender, args, new ArrayList<SabreCommand>());
	}
	
	// This is where the command action is performed.
	public abstract void perform();
	
	
	// -------------------------------------------- //
	// Call Validation
	// -------------------------------------------- //	
	
	
	/**
	 * This method validates that all prerequisites to perform this command has been met.
	 * @param sender The command sender
	 * @param args The command args
	 * @return true if the call if valid and can proceed
	 */
	public boolean validCall(CommandSender sender, List<String> args)
	{
		if ( ! validSenderType(sender, true))
		{
			return false;
		}
		
		if ( ! validSenderPermissions(sender, true))
		{
			return false;
		}
		
		if ( ! validArgs(args, sender))
		{
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
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot)
	{
		if (this.senderMustBePlayer && ! (sender instanceof Player))
		{
			if (informSenderIfNot)
			{
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
	public boolean validSenderPermissions(CommandSender sender, boolean informSenderIfNot)
	{
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
	public boolean validArgs(List<String> args, CommandSender sender)
	{
		if (args.size() < this.requiredArgs.size())
		{
			if (sender != null)
			{
				msg(Lang.commandToFewArgs);
				sender.sendMessage(this.getUseageTemplate());
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
				sender.sendMessage(this.getUseageTemplate());
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
	public boolean validArgs(List<String> args)
	{
		return this.validArgs(args, null);
	}
	
	// -------------------------------------------- //
	// Help and Usage information
	// -------------------------------------------- //
	
	public String getUseageTemplate(List<SabreCommand> commandChain, boolean addShortHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append(plugin.txt().parseTags("<c>"));
		ret.append('/');
		
		for (SabreCommand mc : commandChain)
		{
			ret.append(TextUtil.implode(mc.aliases, ","));
			ret.append(' ');
		}
		
		ret.append(TextUtil.implode(this.aliases, ","));
		
		List<String> args = new ArrayList<String>();
		
		for (String requiredArg : this.requiredArgs)
		{
			args.add("<"+requiredArg+">");
		}
		
		for (Entry<String, String> optionalArg : this.optionalArgs.entrySet())
		{
			String val = optionalArg.getValue();
			if (val == null)
			{
				val = "";
			}
			else
			{
				val = "="+val;
			}
			args.add("["+optionalArg.getKey()+val+"]");
		}
		
		if (args.size() > 0)
		{
			ret.append(plugin.txt().parseTags("<p> "));
			ret.append(TextUtil.implode(args, " "));
		}
		
		if (addShortHelp)
		{
			ret.append(plugin.txt().parseTags(" <i>"));
			ret.append(this.getHelpShort());
		}
		
		return ret.toString();
	}
	
	public String getUseageTemplate(boolean addShortHelp)
	{
		return getUseageTemplate(this.commandChain, addShortHelp);
	}
	
	public String getUseageTemplate()
	{
		return getUseageTemplate(false);
	}
	
	// -------------------------------------------- //
	// Message Sending Helpers
	// -------------------------------------------- //
	
	public void msg(String str, Object... args)
	{
		if (senderIsConsole) {
			String message = SabrePlugin.instance().txt().parse(str, args);
			sender.sendMessage(message);
		} else {
			me.msg(str, args);
		}
	}
	
	public void msg(List<String> msgs)
	{
		for(String msg : msgs)
		{
			this.msg(msg);
		}
	}
	
	
	protected static String parse(String str) {
		return SabrePlugin.instance().txt().parse(str);
	}
	
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	
	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	
	// Is set? ======================
	public boolean argIsSet(int idx)
	{
		if (this.args.size() < idx+1)
		{
			return false;
		}
		return true;
	}
	
	// STRING ======================
	public String argAsString(int idx, String def)
	{
		if (this.args.size() < idx+1)
		{
			return def;
		}
		return this.args.get(idx);
	}
	public String argAsString(int idx)
	{
		return this.argAsString(idx, null);
	}
	
	// INT ======================
	public Integer strAsInt(String str, Integer def)
	{
		if (str == null) return def;
		try
		{
			Integer ret = Integer.parseInt(str);
			return ret;
		}
		catch (Exception e)
		{
			return def;
		}
	}
	public Integer argAsInt(int idx, Integer def)
	{
		return strAsInt(this.argAsString(idx), def);
	}
	public Integer argAsInt(int idx)
	{
		return this.argAsInt(idx, null);
	}
	
	// Double ======================
	public Double strAsDouble(String str, Double def)
	{
		if (str == null) return def;
		try
		{
			Double ret = Double.parseDouble(str);
			return ret;
		}
		catch (Exception e)
		{
			return def;
		}
	}
	public Double argAsDouble(int idx, Double def)
	{
		return strAsDouble(this.argAsString(idx), def);
	}
	public Double argAsDouble(int idx)
	{
		return this.argAsDouble(idx, null);
	}
	
	// Boolean ======================
	public Boolean strAsBool(String str)
	{
		str = str.toLowerCase();
		if (str.startsWith("y") || str.startsWith("t") || str.startsWith("on") || str.startsWith("+") || str.startsWith("1"))
		{
			return true;
		}
		return false;
	}
	public Boolean argAsBool(int idx, Boolean def)
	{
		String str = this.argAsString(idx);
		if (str == null) return def;
		
		return strAsBool(str);
	}
	
	public Boolean argAsBool(int idx)
	{
		return this.argAsBool(idx, false);
	}
	




	// PLAYER ======================
	public SabrePlayer strAsPlayer(String name)
	{
		SabrePlayer ret = null;

		if (name != null)
		{
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

	public SabrePlayer argAsPlayer(int idx)
	{
		return this.strAsPlayer(argAsString(idx));
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
	
	
	public String getForbiddenMessage(String perm)
	{
		return plugin.txt().parse(Lang.permForbidden, getPermissionDescription(perm));
	}

	public String getPermissionDescription (String perm)
	{
		String desc = permissionDescriptions.get(perm);
		if (desc == null)
		{
			return Lang.permDoThat;
		}
		return desc;
	}
	
	/**
	 * This method tests if me has a certain permission and returns
	 * true if me has. Otherwise false
	 */
	public boolean senderHasPerm(CommandSender me, String perm)
	{
		if (me == null) return false;
		
		if ( ! (me instanceof Player))
		{
			return me.hasPermission(perm);
		}

		return me.hasPermission(perm);
	}
	
	public boolean senderHasPerm(CommandSender me, String perm, CommandVisibility visiblity, boolean informSenderIfNot) {
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
	
	public <T> T pickFirstVal(CommandSender me, Map<String, T> perm2val)
	{
		if (perm2val == null) return null;
		T ret = null;
		
		for ( Entry<String, T> entry : perm2val.entrySet()) {
			ret = entry.getValue();
			if (senderHasPerm(me, entry.getKey())) break;
		}
		
		return ret;
	}
}
