package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;

public class CmdGroupInvite extends SabreCommand {
	
	public CmdGroupInvite()
	{
		super();
		
		this.aliases.add("invite");
		this.aliases.add("inv");

		this.requiredArgs.add("group");
		this.requiredArgs.add("player");
		
		this.setHelpShort("Invites a player to a group");

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
		if (memberMe == null || !memberMe.canInvite()) {
			msg(Lang.noPermission);
			return;
		}
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		if (p.equals(me)) {
			msg(Lang.groupNotInviteSelf, groupName);
			return;
		}
		
		if (g.getMember(p) != null) {
			msg(Lang.groupPlayerAlreadyMember, p.getName(), groupName);
			return;
		}
		
		if (g.isInvited(p)) {
			msg(Lang.groupPlayerAlreadyInvited, p.getName(), groupName);
			return;
		}
		
		if (p.getAutoJoin() && (!g.isFaction() || p.getFaction() == null)) {
			gm.uninvitePlayer(g, p);
			gm.addPlayer(g, p);
			p.msg(Lang.groupYouJoined, g.getFullName());
			g.msgAllBut(p, Lang.groupPlayerJoined, p.getName(), groupName);
		} else {
			gm.invitePlayer(g, p);
			msg(Lang.groupPlayerInvited, p.getName(), groupName);

			p.msg(Lang.groupInvited, groupName);
			if (p.isOnline()) {
				p.msg(Lang.groupJoinHelp, groupName);
			}
		}
	}
}
