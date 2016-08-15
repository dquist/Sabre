package com.civfactions.sabre.cmd.snitch;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.groups.SabreGroup;

public class CmdSnitchMute  extends SabreCommand {

	
	public CmdSnitchMute()
	{
		super();
		
		this.requiredArgs.add("group");
		this.optionalArgs.put("muted", "off");
		
		this.setHelpShort("mutes/unmutes a snitch group");

		senderMustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		String groupName = this.argAsString(0);
		
		SabreGroup g = checkGroupExists(groupName, true);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getFullName();
		
		boolean setTo = false;
		
		if (this.args.size() > 1) {
			setTo = this.argAsBool(1);
		} else {
			setTo = !g.isSnitchMutedBy(me);
		}
		
		g.setSnitchMutedBy(me,  setTo);
		
		if (setTo) {
			me.msg(Lang.snitchMute, groupName);
		} else {
			me.msg(Lang.snitchUnmute, groupName);
		}
	}
}
