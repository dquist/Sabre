package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.core.CommandVisibility;

public class CmdChatIgnore extends SabreCommand {

	public CmdChatIgnore()
	{
		super();
		this.aliases.add("ignore");

		this.requiredArgs.add("player");
		this.optionalArgs.put("ignored", "off");
		
		this.setHelpShort("Ignores a player");

		senderMustBePlayer = true;
		errorOnToManyArgs = false;
		this.visibility = CommandVisibility.INVISIBLE;
	}

	@Override
	public void perform() 
	{		
		String playerName = this.argAsString(0);
		SabrePlayer p = this.strAsPlayer(playerName);
		
		if (p == null) {
			msg(Lang.unknownPlayer, playerName);
			return;
		} 
		
		if (!p.isOnline()) {
			msg(Lang.chatPlayerOffline, p.getName());
			return;
		}
		
		boolean setTo = false;
		
		if (this.args.size() > 1) {
			setTo = this.argAsBool(1);
		} else {
			setTo = me.isIgnoring(p);
		}
		
		me.setIgnored(p, setTo);
		
		if (setTo) {
			me.msg(Lang.groupMute, p.getName());
			
			// Move to global chat
			if (me.getChatChannel().equals(p)) {
				me.setChatChannel(plugin.getGlobalChat());
				me.msg(Lang.chatMovedGlobal);
			}
		} else {
			me.msg(Lang.groupUnmute, p.getName());
		}
	}
}
