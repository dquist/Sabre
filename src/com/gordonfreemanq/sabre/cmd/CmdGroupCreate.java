package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.groups.Rank;
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
		
		// Does the group already exist?
		if (g != null) {
			SabreMember m = g.getMember(me);
			if (m == null) {
				msg(Lang.groupAlreadyExists, g.getName());
			} else if (m.getRank() == Rank.OWNER) {
				msg(Lang.groupAlreadyOwn, g.getName());
			} else {
				msg(Lang.groupAlreadyMember, g.getName());
			}
			return;
		}
		
		// Success
		g = gm.createNewGroup(me, groupName, false);
		gm.addGroup(me, g);
		msg(Lang.groupCreated, groupName);
	}
}
