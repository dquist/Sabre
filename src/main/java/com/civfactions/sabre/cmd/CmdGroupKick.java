package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;


public class CmdGroupKick extends SabreCommand {

	public CmdGroupKick()
	{
		super();
		this.aliases.add("kick");

		this.requiredArgs.add("group");
		this.requiredArgs.add("player");

		this.setHelpShort("Kicks a player from a group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		String playerName = this.argAsString(1);
		SabrePlayer p = this.argAsPlayer(1);

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
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		if (p.equals(me)) {
			msg(Lang.groupNotKickSelf);
			msg(Lang.groupLeaveHelp, groupName);
			return;
		}

		SabreMember memberTarget = g.getMember(p);
		if (memberTarget == null) {
			msg(Lang.groupPlayerNotMember, p.getName(), groupName);
			return;
		}
		
		if (!memberMe.canKickMember(memberTarget)) {
			msg(Lang.noPermission);
			return;
		}

		// Do the deed
		gm.removePlayer(g, p);
		
		msg(Lang.groupYouKicked, p.getName(), groupName);
		p.msg(Lang.groupYouWereKicked, groupName);
		g.msgAllBut(me, Lang.groupPlayerKicked, me.getName(), p.getName(), groupName);
	}
}