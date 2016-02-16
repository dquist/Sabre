package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.cmd.SabreCommand;

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
		SabrePlayer p = pm.getPlayerByName(playerName);
		if (p == null || !p.isOnline()) {
			msg(Lang.pearlNoPlayer);
			return;
		}
		
		p.setRequestedBcastPlayer(me);
		p.msg(Lang.pearlBcastRequest, me.getName());
		msg(Lang.pearlBcastRequestSent);
	}
}
