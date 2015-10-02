package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;


public class CmdAdminUnban extends SabreCommand {

	public CmdAdminUnban()
	{
		super();
		this.aliases.add("unban");

		this.requiredArgs.add("player");

		this.setHelpShort("Bans a player");
		
		this.errorOnToManyArgs = false;
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
		
		if (!p.getBanned()) {
			msg(Lang.adminPlayerNotBanned, p.getName());
			return;
		}
		
		pm.setBanStatus(p, false, "");
		msg(Lang.adminUnbannedPlayer, p.getName());
	}
}
