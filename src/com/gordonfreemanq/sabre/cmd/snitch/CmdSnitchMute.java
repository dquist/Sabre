package com.gordonfreemanq.sabre.cmd.snitch;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.groups.SabreGroup;

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
		
		SabreGroup g = checkGroupExists(groupName);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
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
