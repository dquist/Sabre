package com.gordonfreemanq.sabre.cmd;

import java.util.Collections;

public class CmdRoot extends SabreCommand {
	
	public CmdRoot()
	{
		super();

		this.aliases.add("f");
		this.aliases.removeAll(Collections.singletonList(null));  // remove any nulls from extra commas
		
		this.setHelpShort("The Faction base command");
		this.optionalArgs.put("page", "1");
		
		
		this.addSubCommand(new CmdGroupAutoJoin());
		this.addSubCommand(new CmdGroupCreate());
		this.addSubCommand(new CmdGroupInvite());
		this.addSubCommand(new CmdGroupJoin());
		this.addSubCommand(new CmdGroupKick());
		this.addSubCommand(new CmdGroupLeave());
		this.addSubCommand(new CmdGroupMute());
		this.addSubCommand(new CmdGroupTransfer());
		this.addSubCommand(new CmdGroupRank());
		this.addSubCommand(new CmdGroupRename());
		this.addSubCommand(new CmdGroupSetRank());
		this.addSubCommand(new CmdGroupStats());
		this.addSubCommand(new CmdGroupUninvite());
		this.addSubCommand(new CmdHelp());
		this.addSubCommand(new CmdUnknown());
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
