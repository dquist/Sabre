package com.gordonfreemanq.sabre.core;

import org.bukkit.command.CommandSender;

import com.gordonfreemanq.sabre.SabrePlugin;

public enum Permission
{
	ADMIN("admin"),
	MOD("mod"),
	PLAYER("build");
	
	/**
	 * The node string that is referenced for permissions
	 */
	public final String node;
	
	Permission(final String node)
	{
		this.node = "sabre." + node;
	}
	
	public boolean has(CommandSender sender, boolean informSenderIfNot)
	{
		return SabrePlugin.getPlugin().perm.has(sender, this.node, informSenderIfNot);
	}
	
	public boolean has(CommandSender sender)
	{
		return has(sender, false);
	}
}
