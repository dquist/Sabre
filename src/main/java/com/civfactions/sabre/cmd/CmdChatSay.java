package com.civfactions.sabre.cmd;

import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.util.Permission;


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
		
		String formatted = SabrePlugin.instance().getServerBroadcast().chat("Server", sb.toString().trim());
		if (senderIsConsole) {
			sender.sendMessage(formatted);
		}
	}
}
