package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;

public class CmdGroupUninvite extends SabreCommand {
	
	public CmdGroupUninvite()
	{
		super();
		
		this.aliases.add("uninvite");
		this.aliases.add("uninv");

		this.requiredArgs.add("group");
		this.requiredArgs.add("player");
		
		this.setHelpShort("Revokes a player invite");

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
		groupName = g.getName();
		
		SabreMember memberMe = g.getMember(me);
		if (!memberMe.canInvite()) {
			msg(Lang.noPermission, groupName);
			return;
		}
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		if (g.getMember(p) != null) {
			msg(Lang.groupPlayerAlreadyMember, p.getName(), groupName);
			return;
		}
		
		if (!g.isInvited(p)) {
			msg(Lang.groupPlayerNotInvited, p.getName(), groupName);
			return;
		}
		
		gm.uninvitePlayer(g, p);
		msg(Lang.groupPlayerUninvited, p.getName(), groupName);
	}
}
