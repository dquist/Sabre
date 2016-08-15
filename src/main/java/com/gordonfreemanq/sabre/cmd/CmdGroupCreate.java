package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreGroup;


public class CmdGroupCreate extends SabreCommand {

	public CmdGroupCreate()
	{
		super();
		
		this.aliases.add("creategroup");
		this.aliases.add("cg");

		this.requiredArgs.add("name");
		
		this.setHelpShort("Creates a new reinforcement group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		SabreGroup g = gm.getGroupByName(me, groupName);
		
		if (g != null) {
			msg(Lang.groupAlreadyOwn, g.getName());
		}
		
		// Success
		g = gm.createNewGroup(me, groupName);
		msg(Lang.groupCreated, g.getFullName());
	}
}
