package com.gordonfreemanq.sabre.cmd.snitch;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.snitch.SnitchController;
import com.gordonfreemanq.sabre.snitch.SnitchLogger;

public class CmdSnitchReport  extends SabreCommand {

	
	public CmdSnitchReport()
	{
		super();
		
		this.aliases.add("report");
		
		this.optionalArgs.put("page", "1");
		
		this.setHelpShort("Gets log entries");
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
		
		int page = 0;
		if (this.argIsSet(0)) {
			page = Math.max(0, this.argAsInt(0) - 1);
		}
		
		SnitchLogger.getInstance().requestReport(me, snitch, page);
		
	}
}
