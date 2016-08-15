package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;

public class CmdGroupAutoJoin extends SabreCommand {

	public CmdGroupAutoJoin()
	{
		super();
		this.aliases.add("autojoin");
		this.aliases.add("aj");

		this.optionalArgs.put("enabled", "off");

		this.setHelpShort("Sets your auto-join status");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		boolean setTo = false;
		
		if (this.args.size() > 0) {
			setTo = this.argAsBool(0);
		} else {
			setTo = !me.getAutoJoin();
		}
		
		pm.setAutoJoin(me, setTo);
		if (setTo) {
			msg(Lang.groupAutoJoinEnabled);
		} else {
			msg(Lang.groupAutoJoinDisabled);
		}
	}
}
