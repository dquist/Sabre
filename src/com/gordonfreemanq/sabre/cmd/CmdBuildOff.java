package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.BuildMode;


public class CmdBuildOff extends SabreCommand {

	public CmdBuildOff()
	{
		super();
		this.aliases.add("bdo");
		this.aliases.add("cto");

		this.setHelpShort("Resets build modes");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		me.getBuildState().reset();
		msg(Lang.blockBuildMode, BuildMode.OFF.name());
	}
}
