package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;
import com.gordonfreemanq.sabre.util.VanishApi;


public class CmdAdminVanish extends SabreCommand {

	public CmdAdminVanish()
	{
		super();
		this.aliases.add("vanish");

		this.setHelpShort("Toggles vanish status");
		
		this.errorOnToManyArgs = false;
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		VanishApi vm = plugin.getVanishApi();
		
		if (vm.isVanished(me)) {
			vm.unVanish(me);
			me.msg(Lang.adminUnvanished);
		} else {
			vm.vanish(me);
			me.msg(Lang.adminVanished);
		}
	}
}
