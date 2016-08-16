package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.IPlayer;


public class CmdAdminRename extends SabreCommand {

	public CmdAdminRename()
	{
		super();
		this.aliases.add("rename");

		this.requiredArgs.add("player");
		this.requiredArgs.add("new name");

		this.setHelpShort("Changes the name for a player on this server");
	}

	@Override
	public void perform() 
	{
		String playerName = this.argAsString(0);
		String newName = this.argAsString(1);
		
		IPlayer p = pm.getPlayerByName(playerName);
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		// Try to get an exact match for the new name
		IPlayer existing = pm.getPlayerByName(newName);
		
		if (existing != null && !p.equals(existing)) {
			msg(Lang.adminNameExists, newName);
			return;
		}
		
		pm.setDisplayName(p, newName);
		p.msg(Lang.adminYourNameIsNow, newName);
		msg(Lang.adminChangedPlayerName, playerName, newName);
	}
}
