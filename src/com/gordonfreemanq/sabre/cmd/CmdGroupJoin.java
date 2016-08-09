package com.gordonfreemanq.sabre.cmd;

import java.util.Collection;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.util.TextUtil;


public class CmdGroupJoin extends SabreCommand {

	public CmdGroupJoin()
	{
		super();
		this.aliases.add("join");

		this.requiredArgs.add("group");
		
		this.setHelpShort("Joins a group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		final String searchName = this.argAsString(0);
		
		// Get all the groups that the player is invited to
		Collection<SabreGroup> invited = gm.getInvitedGroups(me);
		
		// Get the group by the requested name
		SabreGroup g = invited.stream().filter(g1 -> g1.getName().equalsIgnoreCase(searchName)).findFirst().orElse(null);
		
		// If no match, find closest match
		if (g == null) {
			g = TextUtil.getBestNamedMatch(invited, searchName, "");
		}
		
		// Still no match
		if (g == null) {
			me.msg(Lang.groupNoInvite);
		}
		
		// Get the correct name
		String groupName = g.getName();
		
		
		if (g.getMembers().size() == 0) {
			gm.uninvitePlayer(g, me);
			SabreMember m = gm.addPlayer(g, me);
			gm.setPlayerRank(m,  Rank.OWNER);
			me.msg(Lang.groupYouJoined, g.getName());
			return;
		}
		
		if (!g.isInvited(me)) {
			msg(Lang.groupNotInvited, groupName);
			g.msgAll(Lang.groupTriedJoin, false, me.getName(), g.getName());
			return;
		}

		gm.uninvitePlayer(g, me);
		gm.addPlayer(g, me);
		me.msg(Lang.groupYouJoined, g.getName());
		g.msgAllBut(me, Lang.groupPlayerJoined, me.getName(), groupName);
	}
}
