package com.gordonfreemanq.sabre.cmd;

public class CmdChatMe extends SabreCommand {

	public CmdChatMe()
	{
		super();
		this.aliases.add("me");
		
		this.optionalArgs.put("what", "");
		
		this.setHelpShort("Messages a player");

		senderMustBePlayer = true;
		errorOnToManyArgs = false;
		this.visibility = CommandVisibility.INVISIBLE;
	}

	@Override
	public void perform() 
	{
		if (args.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < args.size(); i++) {
				sb.append(args.get(i));
				sb.append(" ");
			}
			
			me.getChatChannel().chatMe(me, sb.toString().trim());
		}
	}
}
