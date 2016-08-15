package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

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
