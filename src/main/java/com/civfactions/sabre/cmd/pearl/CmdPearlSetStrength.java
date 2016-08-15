package com.civfactions.sabre.cmd.pearl;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.cmd.CommandVisibility;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;
import com.civfactions.sabre.util.Permission;

public class CmdPearlSetStrength extends SabreCommand {

	public CmdPearlSetStrength()
	{
		super();
		this.aliases.add("setstrength");

		this.senderMustBePlayer = true;
		this.requiredArgs.add("player");
		this.requiredArgs.add("value");
		
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
		
		this.setHelpShort("Sets the strength value of a pearl.");
	}

	@Override
	public void perform() 
	{		
		SabrePlayer p = this.argAsPlayer(0);
		if (p == null) {
			msg(Lang.unknownPlayer, this.argAsString(0));
			return;
		}
		
		if (!pearls.isImprisoned(p)) {
			msg(Lang.pearlPlayerNotImprisoned);
			return;
		}
		
		PrisonPearl pp = pearls.getById(p.getID());
		
		int strength = this.argAsInt(1, pp.getSealStrength());
		pearls.setSealStrength(pp, strength);
		
		msg(Lang.pearlUpdateStrength, pp.getSealStrength());
	}
}
