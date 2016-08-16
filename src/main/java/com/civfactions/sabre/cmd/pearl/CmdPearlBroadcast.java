package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.cmd.SabreCommand;

public class CmdPearlBroadcast extends SabreCommand {

	public CmdPearlBroadcast()
	{
		super();
		this.aliases.add("broadcast");
		
		this.requiredArgs.add("player");

		this.senderMustBePlayer = true;

		this.setHelpShort("Broadcasts your location to another player.");
	}

	@Override
	public void perform() 
	{
		if (!pearls.isImprisoned(me)) {
			msg(Lang.pearlNotImprisoned);
			return;
		}
		
		String playerName = args.get(0);
		IPlayer p = pm.getPlayerByName(playerName);
		if (p == null || !p.isOnline()) {
			msg(Lang.pearlNoPlayer);
			return;
		}
		
		p.setRequestedBcastPlayer(me);
		p.msg(Lang.pearlBcastRequest, me.getName());
		msg(Lang.pearlBcastRequestSent);
	}
}
