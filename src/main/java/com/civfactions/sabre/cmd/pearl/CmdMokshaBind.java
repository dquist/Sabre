package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.customitems.MokshaRod;

public class CmdMokshaBind extends SabreCommand {

	public CmdMokshaBind()
	{
		super();
		this.aliases.add("bind");

		this.senderMustBePlayer = true;

		this.requiredArgs.add("player");
		
		this.setHelpShort("Binds a moksha rod to a prison pearl");
	}

	@Override
	public void perform() 
	{
		MokshaRod rod = MokshaRod.getRodFromItem(me.getPlayer().getItemInHand());
		if (rod == null) {
			msg(Lang.pearlNotHoldingMoksha);
			return;
		}
		
		SabrePlayer p = this.argAsPlayer(0);
		if (p == null) {
			msg(Lang.unknownPlayer, this.argAsString(0));
			return;
		}
		
		if (!pearls.isImprisoned(p)) {
			msg(Lang.pearlPlayerNotImprisoned);
			return;
		}
		
		rod.setBoundPlayer(p);
		rod.updateLore();
		me.getPlayer().setItemInHand(rod);
		msg(Lang.pearlBoundMoksha, p.getName());
	}
}
