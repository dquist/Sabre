package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;


public class CmdGroupRank extends SabreCommand {

	public CmdGroupRank()
	{
		super();
		this.aliases.add("rank");

		this.requiredArgs.add("group");

		this.setHelpShort("Checks your rank in a group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);

		SabreGroup g = checkGroupExists(groupName);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
		SabreMember m = g.getMember(me);
		if (m == null) {
			msg(Lang.groupNotMember, groupName);
			return;
		}
		
		msg(Lang.groupCheckRank, groupName, m.getRank().toString());
	}
}
