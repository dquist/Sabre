package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.groups.SabreGroup;


public class CmdGroupRank extends SabreCommand {

	public CmdGroupRank()
	{
		super();
		this.aliases.add("rank");

		this.requiredArgs.add("group");

		this.setHelpShort("Checks your rank in a group or faction");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);

		SabreGroup g = checkGroupExists(groupName, true);
		if (g == null) {
			return;
		}
		
		msg(Lang.groupCheckRank, g.getFullName(), g.getMember(me).getRank().toString());
	}
}
