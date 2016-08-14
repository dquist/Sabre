package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.util.Permission;
import com.gordonfreemanq.sabre.util.SabreUtil;


public class CmdTeleportHere extends SabreCommand {

	public CmdTeleportHere()
	{
		super();
		this.aliases.add("tphere");

		this.setHelpShort("Teleports a player to you");
		
		this.requiredArgs.add("player");

		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		String playerName = this.argAsString(0);
		
		SabrePlayer p = this.strAsPlayer(playerName);
		if (p == null) {
			me.msg(Lang.unknownPlayer, playerName);
			return;
		}
		
		if (!p.isOnline()) {
			me.msg("<c>%s <b>is not online.", playerName);
			return;
		}

		SabreUtil.tryToTeleport(p.getPlayer(), me.getPlayer().getLocation());
	}
}
