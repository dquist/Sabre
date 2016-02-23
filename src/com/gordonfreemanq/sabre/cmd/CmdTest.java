package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;


public class CmdTest extends SabreCommand {

	public CmdTest()
	{
		super();
		this.aliases.add("test");
		
		this.errorOnToManyArgs = false;

		this.setHelpShort("A test function");
		
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
	}
}
