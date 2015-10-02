package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;


public class CmdGroupJoin extends SabreCommand {

	public CmdGroupJoin()
	{
		super();
		this.aliases.add("join");
		this.aliases.add("j");

		this.requiredArgs.add("group");
		
		this.setHelpShort("Joins a group");

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
		
		if (g.getMember(me) != null) {
			msg(Lang.groupAlreadyMember, groupName);
			return;
		}
		
		
		if (g.getMembers().size() == 0) {
			gm.uninvitePlayer(g, me);
			SabreMember m = gm.addPlayer(g, me);
			gm.setPlayerRank(m,  Rank.OWNER);
			me.msg(Lang.groupYouJoined, g.getName());
			return;
		}
		
		if (!g.isInvited(me)) {
			msg(Lang.groupNotInvited, groupName);
			g.msgAll(Lang.groupTriedJoin, me.getName(), g.getName());
			return;
		}

		gm.uninvitePlayer(g, me);
		gm.addPlayer(g, me);
		me.msg(Lang.groupYouJoined, g.getName());
		g.msgAllBut(me, Lang.groupPlayerJoined, me.getName(), groupName);
	}
}
