package com.civfactions.sabre.cmd;

import java.util.ArrayList;

public class CmdAutoHelp extends SabreCommand
{
	public CmdAutoHelp()
	{
		super();
		this.aliases.add("?");
		this.aliases.add("h");
		this.aliases.add("help");
		
		this.setHelpShort("");
		
		this.optionalArgs.put("page","1");
	}
	
	@Override
	public void perform()
	{
		if (this.commandChain.size() == 0) return;
		SabreCommand cmd = this.commandChain.get(this.commandChain.size()-1);
		
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.addAll(cmd.helpLong);
		
		for(SabreCommand c : cmd.getSubCommands()) {
			// Only list help for commands that are visible or the sender has permission for
			if (c.visibility == CommandVisibility.VISIBLE || (c.visibility == CommandVisibility.SECRET && c.validSenderPermissions(sender, false))) {
				lines.add(c.getUseageTemplate(this.commandChain, true));
			}
		}
		
		int page = this.argAsInt(0, 1);
		
		msg(plugin.txt().getPage(lines, page, "Help for command \""+cmd.aliases.get(0)+"\""));
	}
}
