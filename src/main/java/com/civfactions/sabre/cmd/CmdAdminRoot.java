package com.civfactions.sabre.cmd;

import java.util.Collections;

import com.civfactions.sabre.util.Permission;

public class CmdAdminRoot extends SabreCommand {
	
	public CmdAdminRoot()
	{
		super();
		
		this.aliases.add("admin");
		this.aliases.removeAll(Collections.singletonList(null));  // remove any nulls from extra commas
		
		this.setHelpShort("Sabre Administration commands");
		
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
		
		this.addSubCommand(new CmdAdminRename());
		this.addSubCommand(new CmdAdminDeletePlayer());
		this.addSubCommand(new CmdAdminBypass());
		this.addSubCommand(new CmdAdminSetSpawn());
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
