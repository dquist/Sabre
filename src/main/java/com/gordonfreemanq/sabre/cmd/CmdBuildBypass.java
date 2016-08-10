package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.BuildState;


public class CmdBuildBypass extends SabreCommand {

	public CmdBuildBypass()
	{
		super();
		this.aliases.add("bdb");
		this.aliases.add("ctb");
		
		this.optionalArgs.put("on", "false");

		this.setHelpShort("Toggles block bypass mode");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		boolean setTo = false;
		
		BuildState s = me.getBuildState();
		if (this.args.size() > 0) {
			setTo = this.argAsBool(0);
		} else {
			setTo = !s.getBypass();
		}
		
		s.setBypass(setTo);
		if (setTo) {
			msg(Lang.blockBypassEnable);
		} else {
			msg(Lang.blockBypassDisable);
		}
	}
}
