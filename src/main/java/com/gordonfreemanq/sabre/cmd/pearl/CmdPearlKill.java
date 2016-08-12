package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;

public class CmdPearlKill extends SabreCommand {

	public CmdPearlKill()
	{
		super();
		this.aliases.add("kill");

		this.senderMustBePlayer = true;

		this.setHelpShort("Kills a pearled prisoner");
	}

	@Override
	public void perform() 
	{
		PrisonPearl pp = pearls.getPearlByItem(me.getPlayer().getItemInHand());
		if (pp == null) {
			msg(Lang.pearlNotHoldingPearl);
			return;
		}

		pearls.killPearl(pp, me);
	}
}
