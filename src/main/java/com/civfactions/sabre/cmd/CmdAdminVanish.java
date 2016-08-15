package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.util.Permission;
import com.civfactions.sabre.util.VanishApi;


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
