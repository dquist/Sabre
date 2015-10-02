package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BuildMode;
import com.gordonfreemanq.sabre.blocks.BuildState;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;


public class CmdGroupSetRank extends SabreCommand {

	public CmdGroupSetRank()
	{
		super();
		this.aliases.add("setrank");
		this.aliases.add("sr");

		this.requiredArgs.add("group");
		this.requiredArgs.add("player");
		this.requiredArgs.add("rank");

		this.setHelpShort("Sets the rank of a player");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		String playerName = this.argAsString(1);
		String rankName = this.argAsString(2);
		Rank rank = Rank.fromString(rankName);
		SabrePlayer p = this.argAsPlayer(1);

		SabreGroup g = checkGroupExists(groupName);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
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
			msg(Lang.groupNotRankSelf, groupName);
			return;
		}

		SabreMember memberTarget = g.getMember(p);
		if (memberTarget == null) {
			msg(Lang.groupPlayerNotMember, p.getName(), groupName);
			return;
		}
		
		if (rank == null) {
			msg(Lang.groupBadRank);
			return;
		}
		
		int myRank = memberMe.getRank().ordinal();
		int curRank = memberTarget.getRank().ordinal();
		int newRank = rank.ordinal();

		if (memberMe.isOwner()) {
			if (newRank == Rank.OWNER.ordinal()) {
				msg(Lang.groupUseTransfer);
				msg(Lang.groupTransferHelp, groupName);
				return;
			}
			
		} else if (myRank == Rank.ADMIN.ordinal()) {
			// Admins get to promote others to admins
			if (curRank > myRank || newRank > myRank) {
				msg(Lang.noPermission);
				return;
			}
		} else if (myRank < Rank.OFFICER.ordinal()) {
			msg(Lang.noPermission);
			return;
		} else if (curRank >= myRank || newRank >= myRank) {
			msg(Lang.noPermission);
			return;
		}
		
		// Do the deed
		gm.setPlayerRank(memberTarget, rank);
		msg(Lang.groupSetRank, memberTarget.getName(), rank.toString());
		
		// Reset the build mode for the player
		SabrePlayer other = memberTarget.getPlayer();
		BuildState state = other.getBuildState();
		SabreGroup buildGroup = state.getGroup();
		if (!memberTarget.canBuild() && buildGroup != null &&buildGroup.equals(g) && !state.getMode().equals(BuildMode.OFF)) {
			other.getBuildState().setMode(BuildMode.OFF);
			memberTarget.getPlayer().msg(Lang.blockBuildMode, BuildMode.OFF);
		}
	}
}
