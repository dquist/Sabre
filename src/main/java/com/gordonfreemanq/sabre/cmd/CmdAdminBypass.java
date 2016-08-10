package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;


public class CmdAdminBypass extends SabreCommand {

	public CmdAdminBypass()
	{
		super();
		this.aliases.add("bypass");

		this.optionalArgs.put("enabled", "no");
		this.senderMustBePlayer = true;

		this.setHelpShort("Lets you bypass group restrictions.");
	}

	@Override
	public void perform() 
	{
		boolean setTo = false;
		
		if (this.args.size() > 0) {
			setTo = this.argAsBool(0);
		} else {
			setTo = !me.getAdminBypass();
		}
		
		me.setAdminBypass(setTo);
		if (setTo) {
			msg(Lang.blockAdminBypassEnable);
		} else {
			msg(Lang.blockAdminBypassDisable);
		}
	}
}
