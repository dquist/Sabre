package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;


public class CmdChatSay extends SabreCommand {

	public CmdChatSay()
	{
		super();
		this.aliases.add("say");

		this.requiredArgs.add("message");
		
		this.setHelpShort("Messages the server");

		senderMustBePlayer = false;
		errorOnToManyArgs = false;
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i));
			sb.append(" ");
		}
		
		String formatted = SabrePlugin.getPlugin().getServerBroadcast().chat("Server", sb.toString().trim());
		if (senderIsConsole) {
			sender.sendMessage(formatted);
		}
	}
}
