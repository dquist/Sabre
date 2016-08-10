package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;

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
		me.setChatChannel(SabrePlugin.getPlugin().getServerBroadcast());
		msg(Lang.chatMovedServerBcast);
	}
}
