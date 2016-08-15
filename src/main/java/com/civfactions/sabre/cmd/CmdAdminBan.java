package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.util.Permission;


public class CmdAdminBan extends SabreCommand {

	public CmdAdminBan()
	{
		super();
		this.aliases.add("ban");

		this.requiredArgs.add("player");
		this.requiredArgs.add("reason");

		this.setHelpShort("Bans a player");
		
		this.errorOnToManyArgs = false;
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
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
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.size(); i++) {
			sb.append(args.get(i));
			sb.append(" ");
		}
		
		String reason = sb.toString();
		
		pm.setBanStatus(p, true, reason);
		msg(Lang.adminBannedPlayer, p.getName(), reason);
	}
}
