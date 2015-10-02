package com.gordonfreemanq.sabre.cmd.snitch;

import com.gordonfreemanq.sabre.cmd.SabreCommand;

public class CmdSnitch  extends SabreCommand {

	
	public final SabreCommand cmdClear = new CmdSnitchClear();
	public final SabreCommand cmdFind = new CmdSnitchFind();
	public final SabreCommand cmdNotify = new CmdSnitchNotify();
	public final SabreCommand cmdRefresh = new CmdSnitchRefresh();
	public final SabreCommand cmdRename = new CmdSnitchRename();
	public final SabreCommand cmdReport = new CmdSnitchReport();
	
	
	private static CmdSnitch instance;
	public static CmdSnitch getInstance() {
		return instance;
	}
	
	public CmdSnitch()
	{
		super();
		
		this.aliases.add("snitch");
		
		this.setHelpShort("The Snitch base command");
		this.optionalArgs.put("page", "1");
		
		this.addSubCommand(cmdClear);
		this.addSubCommand(cmdFind);
		this.addSubCommand(cmdNotify);
		this.addSubCommand(cmdRefresh);
		this.addSubCommand(cmdRename);
		this.addSubCommand(cmdReport);
		
		instance = this;
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
