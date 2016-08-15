package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

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
		
		pearls.summonPearl(pp, me);
	}
}
