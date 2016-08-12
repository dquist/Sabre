package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;

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
