package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

public class CmdPearlReturn extends SabreCommand {

	public CmdPearlReturn()
	{
		super();
		this.aliases.add("return");

		this.senderMustBePlayer = true;

		this.setHelpShort("Returns a summoned prisoner");
	}

	@Override
	public void perform() 
	{
		PrisonPearl pp = pearls.getPearlByItem(me.getPlayer().getItemInHand());
		if (pp == null) {
			msg(Lang.pearlNotHoldingPearl);
			return;
		}

		pearls.returnPearl(pp, me);
	}
}
