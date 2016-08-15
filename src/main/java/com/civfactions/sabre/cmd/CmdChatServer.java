package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.util.Permission;

public class CmdChatServer extends SabreCommand {

	public CmdChatServer()
	{
		super();
		this.aliases.add("serverchat");
		
		this.optionalArgs.put("what", "");
		
		this.setHelpShort("Messages the server");

		senderMustBePlayer = true;
		errorOnToManyArgs = false;
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		me.setChatChannel(SabrePlugin.instance().getServerBroadcast());
		msg(Lang.chatMovedServerBcast);
	}
}
