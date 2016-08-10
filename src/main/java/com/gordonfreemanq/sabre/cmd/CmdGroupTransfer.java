package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;


public class CmdGroupTransfer extends SabreCommand {

	public CmdGroupTransfer()
	{
		super();
		this.aliases.add("transfer");
		this.aliases.add("t");

		this.requiredArgs.add("group");
		this.requiredArgs.add("player");
		
		this.setHelpShort("Transfers a group");

		senderMustBePlayer = true;
		senderMustConfirm = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		String playerName = this.argAsString(1);
		SabrePlayer p = this.argAsPlayer(1);
		
		SabreGroup g = checkGroupExists(groupName, false);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
		SabreMember memberMe = g.getMember(me);
		if (memberMe == null || !memberMe.isOwner()) {
			msg(Lang.noPermission);
			return;
		}
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		if (p.equals(me)) {
			msg(Lang.groupNotTransferSelf, groupName);
			return;
		}
		
		if (gm.getGroupByName(p, groupName) != null) {
			msg(Lang.groupNotTransferName, p.getName());
			return;
		}		
		
		SabreMember memberTarget = g.getMember(p);
		if (memberTarget == null) {
			memberTarget = gm.addPlayer(g, p);
		}
		
		gm.setPlayerRank(memberMe, Rank.ADMIN);
		gm.setPlayerRank(memberTarget, Rank.OWNER);
		
		msg(Lang.groupTransferred, groupName, memberTarget.getName());
		p.msg(Lang.groupPlayerTransferred, me.getName(), groupName);
	}
}
