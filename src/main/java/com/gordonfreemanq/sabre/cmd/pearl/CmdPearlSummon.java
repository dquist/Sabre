package com.gordonfreemanq.sabre.cmd.pearl;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;

public class CmdPearlSummon extends SabreCommand {

	public CmdPearlSummon()
	{
		super();
		this.aliases.add("summon");

		this.senderMustBePlayer = true;

		this.setHelpShort("Summons a prisoner to your location");
	}

	@Override
	public void perform() 
	{
		PrisonPearl pp = pearls.getPearlByItem(me.getPlayer().getItemInHand());
		if (pp == null) {
			msg(Lang.pearlNotHoldingPearl);
			return;
		}
		
		PearlManager.getInstance().summonPearl(pp, me);
	}
}
