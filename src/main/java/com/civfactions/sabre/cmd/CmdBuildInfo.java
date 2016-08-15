package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.blocks.BuildState;


public class CmdBuildInfo extends SabreCommand {

	public CmdBuildInfo()
	{
		super();
		this.aliases.add("bdi");
		this.aliases.add("cti");
		
		this.optionalArgs.put("on", "false");

		this.setHelpShort("Toggles block info mode");

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
			setTo = !s.getInfo();
		}
		
		s.setInfo(setTo);
		if (setTo) {
			msg(Lang.blockInfoEnable);
		} else {
			msg(Lang.blockInfoDisable);
		}
	}
}
