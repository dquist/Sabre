package com.civfactions.sabre.cmd.factory;

import com.civfactions.sabre.cmd.SabreCommand;

public class CmdFactory extends SabreCommand {

	public final SabreCommand cmdConfigure = new CmdFactoryConfigure();
	public final SabreCommand cmdCreate = new CmdFactoryCreate();
	public final SabreCommand cmdUpgrade = new CmdFactoryUpgrade();
	
	private static CmdFactory instance;
	public static CmdFactory getInstance() {
		return instance;
	}
	
	public CmdFactory()
	{
		super();
		this.aliases.add("factory");

		this.senderMustBePlayer = true;
		
		this.setHelpShort("The Factory base command");

		this.addSubCommand(cmdCreate);
		this.addSubCommand(cmdConfigure);
		this.addSubCommand(cmdUpgrade);
		this.addSubCommand(new CmdFactoryProspect());
		
		instance = this;
	}

	@Override
	public void perform() 
	{
		this.commandChain.add(this);
		plugin.getCmdAutoHelp().execute(this.sender, this.args, this.commandChain);
	}
}
