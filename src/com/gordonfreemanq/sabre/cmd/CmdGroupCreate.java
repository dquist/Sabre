package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;


public class CmdGroupCreate extends SabreCommand {

	public CmdGroupCreate()
	{
		super();
		
		this.aliases.add("create");

		this.requiredArgs.add("group");
		
		this.setHelpShort("Creates a new group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		SabreGroup g = gm.getGroupByName(groupName);
		
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
		g = gm.createNewGroup(groupName, me);
		gm.addGroup(g);
		msg(Lang.groupCreated, groupName);
	}
}
