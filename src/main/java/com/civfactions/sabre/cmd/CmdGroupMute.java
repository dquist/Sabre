package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.groups.SabreGroup;


public class CmdGroupMute extends SabreCommand {

	public CmdGroupMute()
	{
		super();
		this.aliases.add("mute");

		this.requiredArgs.add("group");
		this.optionalArgs.put("muted", "off");
		
		this.setHelpShort("mutes/unmutes a group chat");

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
		groupName = g.getName();
		
		boolean setTo = false;
		
		if (this.args.size() > 1) {
			setTo = this.argAsBool(1);
		} else {
			setTo = !g.isChatMutedBy(me);
		}
		
		g.setChatMutedBy(me, setTo);
		
		if (setTo) {
			me.msg(Lang.groupMute, groupName);
			
			// Move to global chat
			if (me.getChatChannel().equals(g)) {
				me.setChatChannel(plugin.getGlobalChat());
				me.msg(Lang.chatMovedGlobal);
			}
		} else {
			me.msg(Lang.groupUnmute, groupName);
		}
	}
}
