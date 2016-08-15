package com.civfactions.sabre.cmd;

public class CmdUnknown extends SabreCommand {

	public CmdUnknown()
	{
		super();
		this.aliases.add("unknown");
		
		this.errorOnToManyArgs = false;
		this.visibility = CommandVisibility.INVISIBLE;

		senderMustBePlayer = false;
	}

	@Override
	public void perform() 
	{
		msg("<w>Unknown command. Type \"/help\" for help.");
	}
}
