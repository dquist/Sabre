package com.gordonfreemanq.sabre.cmd;

import java.util.Collection;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreGroup;
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
		
		if (me.getFaction() != null) {
			me.msg(Lang.factionAlreadyMember);
			return;
		}
		
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

		gm.uninvitePlayer(g, me);
		gm.addPlayer(g, me);
		me.msg(Lang.groupYouJoined, g.getFullName());
		
		if (g.isFaction()) {
			g.msgAllBut(me, Lang.factionPlayerJoined, me.getName());
		} else {
			g.msgAllBut(me, Lang.groupPlayerJoined, me.getName(), g.getFullName());
		}
	}
}
