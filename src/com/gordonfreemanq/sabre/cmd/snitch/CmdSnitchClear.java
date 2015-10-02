package com.gordonfreemanq.sabre.cmd.snitch;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.snitch.SnitchController;
import com.gordonfreemanq.sabre.snitch.SnitchLogger;

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
		
		SnitchLogger.getInstance().clearEntries(snitch);
		msg(Lang.snitchCleared);
		
	}
}
