package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.cmd.SabreCommand;

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
		SabrePlayer sp = me.getRequestedBcastPlayer();
		if (sp == null) {
			me.msg(Lang.pearlNoBcastRequest);
			return;
		}
		
		sp.getBcastPlayers().add(me);
		me.setRequestedBcastPlayer(null);
		me.msg(Lang.pearlGettingBcasts, sp.getName());
	}
}
