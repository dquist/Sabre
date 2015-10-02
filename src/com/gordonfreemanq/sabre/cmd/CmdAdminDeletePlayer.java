package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.groups.SabreGroup;


public class CmdAdminDeletePlayer extends SabreCommand {

	public CmdAdminDeletePlayer()
	{
		super();
		this.aliases.add("deleteplayer");
		this.aliases.add("dp");

		this.requiredArgs.add("player");

		this.setHelpShort("Deletes the player from the sever");
	}

	@Override
	public void perform() 
	{
		String playerName = this.argAsString(0);
		
		SabrePlayer p = pm.getPlayerByName(playerName);
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		// This will kick the player and prevent a re-login
		pm.setBanStatus(p, true, Lang.adminPlayerModifyBan);
		
		// Remove the player from groups
		for (SabreGroup g : gm.getGroups()) {
			if (g.isMember(p)) {
				gm.removePlayer(g, p);
			}
			if (g.isInvited(p)) {
				gm.uninvitePlayer(g, p);
			}
		}
		
		pm.removePlayer(p);
		pm.setBanStatus(p, false, "");
		msg(Lang.adminRemovedPlayer, p.getName());
	}
}
