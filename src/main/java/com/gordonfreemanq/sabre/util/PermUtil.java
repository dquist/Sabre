package com.gordonfreemanq.sabre.util;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.core.CommandVisibility;


public class PermUtil {

	public Map<String, String> permissionDescriptions = new HashMap<String, String>();
	
	protected SabrePlugin plugin;
	
	public PermUtil(SabrePlugin plugin)
	{
		this.plugin = plugin;
		
		for(Permission permission : plugin.getDescription().getPermissions())
		{
			this.permissionDescriptions.put(permission.getName(), permission.getDescription());
		}
	}
	
	public String getForbiddenMessage(String perm)
	{
		return plugin.txt.parse(Lang.permForbidden, getPermissionDescription(perm));
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
	public boolean has (CommandSender me, String perm)
	{
		if (me == null) return false;
		
		if ( ! (me instanceof Player))
		{
			return me.hasPermission(perm);
		}

		return me.hasPermission(perm);
	}
	
	public boolean has (CommandSender me, String perm, CommandVisibility visiblity, boolean informSenderIfNot)
	{
		if (has(me, perm))
		{
			return true;
		}
		else if (visiblity != CommandVisibility.VISIBLE) {
			me.sendMessage(Lang.unknownCommand);
		}
		else if (informSenderIfNot && me != null)
		{
			me.sendMessage(this.getForbiddenMessage(perm));
		}
		return false;
	}
	
	public <T> T pickFirstVal(CommandSender me, Map<String, T> perm2val)
	{
		if (perm2val == null) return null;
		T ret = null;
		
		for ( Entry<String, T> entry : perm2val.entrySet())
		{
			ret = entry.getValue();
			if (has(me, entry.getKey())) break;
		}
		
		return ret;
	}
	
}
