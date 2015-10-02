package com.gordonfreemanq.sabre.cmd;


public class CmdTest extends SabreCommand {

	public CmdTest()
	{
		super();
		this.aliases.add("test");
		
		this.errorOnToManyArgs = false;

		this.setHelpShort("A test function");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
	}
}
