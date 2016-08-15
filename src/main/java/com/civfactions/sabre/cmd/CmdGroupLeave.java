package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;


public class CmdGroupLeave extends SabreCommand {

	public CmdGroupLeave()
	{
		super();
		this.aliases.add("leave");
		this.aliases.add("l");

		this.requiredArgs.add("group");

		this.setHelpShort("Leaves a group");

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
		
		// Get the correct name
		groupName = g.getFullName();
		
		SabreMember memberMe = g.getMember(me);
		if (memberMe == null) {
			msg(Lang.groupNotMember, groupName);
			return;
		}
		
		if (memberMe.isOwner()) {
			msg(Lang.groupNotLeaveOwner);
			msg(Lang.groupTransferHelp, groupName);
			return;
		}
		
		gm.removePlayer(g, me);
		msg(Lang.groupYouLeft, groupName);
		
		if (g.isFaction()) {
			msg(Lang.factionYouLeft, groupName);
			g.msgAll(Lang.factionPlayerLeft, false, me.getName());
		} else {
			msg(Lang.groupYouLeft, groupName);
			g.msgAll(Lang.groupPlayerLeft, false, me.getName(), g.getName());
		}
	}
}
