package com.civfactions.sabre.cmd.snitch;

import org.bukkit.Location;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.snitch.Snitch;
import com.civfactions.sabre.snitch.SnitchController;

public class CmdSnitchClear  extends SabreCommand {

	
	public CmdSnitchClear()
	{
		super();
		
		this.aliases.add("clear");
		
		this.setHelpShort("Clears the snitch entries");
	}
	
	@Override
	public void perform()
	{
		Location l = SnitchController.parseLocation(me, true);
		if (l == null) {
			return;
		}
		
		Snitch snitch = (Snitch)bm.getBlockAt(l);
		if (snitch == null) {
			msg(Lang.snitchNotFound);
			AbstractController.normalizeHeldController(me);
			return;
		}

		if (!snitch.canPlayerModify(me)) {
			msg(Lang.noPermission);
			return;
		}
		
		plugin.getSnitchLogger().clearEntries(snitch);
		msg(Lang.snitchCleared);
		
	}
}
