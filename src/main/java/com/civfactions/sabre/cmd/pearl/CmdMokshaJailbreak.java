package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.customitems.MokshaRod;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

public class CmdMokshaJailbreak extends SabreCommand {

	public CmdMokshaJailbreak()
	{
		super();
		this.aliases.add("jailbreak");

		this.senderMustBePlayer = true;
		this.setHelpShort("Attempts to jailbreak a pearled player.");
	}

	@Override
	public void perform() 
	{
		MokshaRod rod = MokshaRod.getRodFromItem(me.getPlayer().getItemInHand());
		if (rod == null) {
			msg(Lang.pearlNotHoldingMoksha);
			return;
		}
		
		SabrePlayer p = rod.getPlayer();
		if (p == null) {
			msg(Lang.pearlMokshaNotBound);
			return;
		}
		
		if (rod.getStrength() == 0) {
			msg(Lang.pearlMokshaAddStrength);
			return;
		}
		

		PrisonPearl pp = pearls.getById(rod.getPlayer().getID());
		if (pp == null) {
			msg(Lang.pearlPlayerNotImprisoned);
			return;
		}
		
		
		
		// All set, lets try the jailbreak
		if (rod.getStrength() > pp.getSealStrength()) {
			// Success!
			pearls.freePearl(pp);
			msg(Lang.pearlJailbreakPass, pp.getPlayer().getName());
		} else {
			// Failed
			msg(Lang.pearlJailbreakFail);
		}
		
		// Consume the item
		me.getPlayer().setItemInHand(null);
	}
}
