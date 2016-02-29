package com.gordonfreemanq.sabre.core;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.util.CustomStringList;
import com.gordonfreemanq.sabre.util.TextUtil;


public abstract class SabreBaseCommand<T extends AbstractSabrePlugin>
{
	public T plugin;
	

	protected PlayerManager pm;
	
	// The sub-commands to this command
	public List<SabreBaseCommand<?>> subCommands;
	public void addSubCommand(SabreBaseCommand<?> subCommand)
	{
		subCommand.commandChain.addAll(this.commandChain);
		subCommand.commandChain.add(this);
		this.subCommands.add(subCommand);
	}
	
	// The different names this commands will react to  
	public CustomStringList aliases;
	
	// Aliases for executing commands directly
	public CustomStringList rawAliases;
	
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
			String pdesc = plugin.perm.getPermissionDescription(this.permission);
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
	public List<SabreBaseCommand<?>> commandChain = new ArrayList<SabreBaseCommand<?>>(); // The command chain used to execute this command
	
	public SabreBaseCommand(T p)
	{
		this.plugin = p;
		
		this.permission = null;
		
		this.subCommands = new ArrayList<SabreBaseCommand<?>>();
		this.aliases = new CustomStringList();
		this.rawAliases = new CustomStringList();
		
		this.requiredArgs = new ArrayList<String>();
		this.optionalArgs = new LinkedHashMap<String, String>();
		
		this.helpShort = null;
		this.helpLong = new ArrayList<String>();
		this.visibility = CommandVisibility.VISIBLE;
	}
	
	// The commandChain is a list of the parent command chain used to get to this command.
	public void execute(CommandSender sender, List<String> args, List<SabreBaseCommand<?>> commandChain)
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
			for (SabreBaseCommand<?> subCommand: this.subCommands)
			{
				if (subCommand.aliases.contains(args.get(0)))
				{
					args.remove(0);
					commandChain.add(this);
					subCommand.execute(sender, args, commandChain);
					return;
				}
			}
			
			// Is there a matching raw alias?
			SabreBaseCommand<?> rawAlias = getRawAliasCommand(args.get(0));
			if (rawAlias != null) {
				args.remove(0);
				commandChain.add(this);
				rawAlias.execute(sender, args, commandChain);
				return;
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
		execute(sender, args, new ArrayList<SabreBaseCommand<?>>());
	}
	
	// This is where the command action is performed.
	public abstract void perform();
	
	
	// -------------------------------------------- //
	// Call Validation
	// -------------------------------------------- //
	
	public SabreBaseCommand<?> getRawAliasCommand(String alias) {
				
		if (this.rawAliases.contains(alias)) {
			return this;
		}
		
		for (SabreBaseCommand<?> subCommand: this.subCommands)
		{
			SabreBaseCommand<?> cmd = subCommand.getRawAliasCommand(alias);
			if (cmd != null) {
				return cmd;
			}
		}
		
		// no match
		return null;
	}
	
	
	
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
		
		return plugin.perm.has(sender, this.permission, this.visibility, informSenderIfNot);
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
	
	public String getUseageTemplate(List<SabreBaseCommand<?>> commandChain, boolean addShortHelp)
	{
		StringBuilder ret = new StringBuilder();
		ret.append(plugin.txt.parseTags("<c>"));
		ret.append('/');
		
		for (SabreBaseCommand<?> mc : commandChain)
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
			ret.append(plugin.txt.parseTags("<p> "));
			ret.append(TextUtil.implode(args, " "));
		}
		
		if (addShortHelp)
		{
			ret.append(plugin.txt.parseTags(" <i>"));
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
			String message = SabrePlugin.getPlugin().txt.parse(str, args);
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
		return SabrePlugin.getPlugin().txt.parse(str);
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
}
