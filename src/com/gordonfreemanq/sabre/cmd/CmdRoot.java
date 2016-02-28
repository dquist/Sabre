package com.gordonfreemanq.sabre.cmd;

import java.util.Collections;

import com.gordonfreemanq.sabre.cmd.factory.CmdFactory;
import com.gordonfreemanq.sabre.cmd.pearl.CmdPearl;
import com.gordonfreemanq.sabre.cmd.snitch.CmdSnitch;

public class CmdRoot extends SabreCommand {
	
	public final SabreCommand cmdSnitch = new CmdSnitch();
	
	public CmdRoot()
	{
		super();
		
		this.aliases.add("f");
		this.aliases.removeAll(Collections.singletonList(null));  // remove any nulls from extra commas
		
		this.setHelpShort("The Faction base command");
		this.optionalArgs.put("page", "1");
		
		
		this.addSubCommand(new CmdGroupAutoJoin());
		this.addSubCommand(new CmdBuildBypass());
		this.addSubCommand(new CmdBuildFortify());
		this.addSubCommand(new CmdBuildInfo());
		this.addSubCommand(new CmdBuildReinforce());
		this.addSubCommand(new CmdBuildOff());
		this.addSubCommand(new CmdChat());
		this.addSubCommand(new CmdChatMe());
		this.addSubCommand(new CmdChatMsg());
		this.addSubCommand(new CmdChatReply());
		this.addSubCommand(new CmdChatServer());
		this.addSubCommand(new CmdChatSay());
		this.addSubCommand(new CmdGroupCreate());
		this.addSubCommand(new CmdGroupInvite());
		this.addSubCommand(new CmdGroupJoin());
		this.addSubCommand(new CmdGroupKick());
		this.addSubCommand(new CmdGroupLeave());
		this.addSubCommand(new CmdGroupTransfer());
		this.addSubCommand(new CmdGroupRank());
		this.addSubCommand(new CmdGroupRename());
		this.addSubCommand(new CmdGroupSetRank());
		this.addSubCommand(new CmdGroupStats());
		this.addSubCommand(new CmdGroupUninvite());
		this.addSubCommand(new CmdHelp());
		this.addSubCommand(new CmdUnknown());
		this.addSubCommand(new CmdPearl());
		
		// Factory
		this.addSubCommand(new CmdFactory());
		
		
		// Admin commands
		this.addSubCommand(new CmdAdminRoot());
		this.addSubCommand(new CmdSpeed());
		this.addSubCommand(new CmdTest());
		this.addSubCommand(new CmdTeleport());
		this.addSubCommand(new CmdTeleportHere());
		this.addSubCommand(new CmdAdminFly());
		this.addSubCommand(new CmdAdminVanish());
		this.addSubCommand(new CmdAdminGamemode());
		
		this.addSubCommand(cmdSnitch);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
