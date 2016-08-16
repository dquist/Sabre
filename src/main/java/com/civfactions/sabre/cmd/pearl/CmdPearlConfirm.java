package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.cmd.SabreCommand;

public class CmdPearlConfirm extends SabreCommand {

	public CmdPearlConfirm()
	{
		super();
		this.aliases.add("confirm");

		this.senderMustBePlayer = true;

		this.setHelpShort("Confirms a pearl broadcast request.");
	}

	@Override
	public void perform() 
	{
		IPlayer sp = me.getRequestedBcastPlayer();
		if (sp == null) {
			me.msg(Lang.pearlNoBcastRequest);
			return;
		}
		
		sp.addBcastPlayer(me);
		me.setRequestedBcastPlayer(null);
		me.msg(Lang.pearlGettingBcasts, sp.getName());
	}
}
