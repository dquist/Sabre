package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.core.CommandVisibility;

public class CmdChatReply extends SabreCommand {

	public CmdChatReply()
	{
		super();
		this.aliases.add("reply");

		this.optionalArgs.put("what", "msg");

		this.setHelpShort("Replies to a player");

		senderMustBePlayer = true;
		errorOnToManyArgs = false;
		this.visibility = CommandVisibility.INVISIBLE;
	}

	@Override
	public void perform() 
	{
		SabrePlayer p = me.getLastMessaged();
		
		if (p == null) {
			msg(Lang.chatNoReply);
			return;
		}
		
		if (args.size() == 0) {
			msg(Lang.chatWillReplyTo, p.getName());
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i));
			sb.append(" ");
		}
		
		me.setChatChannel(p);
		p.chat(me, sb.toString().trim());
	}
}
