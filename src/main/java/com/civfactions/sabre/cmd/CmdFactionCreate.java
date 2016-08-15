package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.groups.SabreFaction;


public class CmdFactionCreate extends SabreCommand {

	public CmdFactionCreate()
	{
		super();
		
		this.aliases.add("create");

		this.requiredArgs.add("name");
		
		this.setHelpShort("Creates a new faction");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String factionName = this.argAsString(0);
		
		if (me.getFaction() != null) {
			me.msg(Lang.factionAlreadyMember);
			me.msg(Lang.factionUseCreateGroup);
			return;
		}
		
		SabreFaction faction = gm.getFactionByName(factionName);
		
		// Does the faction already exist?
		if (faction != null) {
			msg(Lang.factionAlreadyExists, faction.getName());
			return;
		}
		
		// Success
		faction = gm.createNewFaction(me, factionName);
		msg(Lang.groupCreated, factionName);
	}
}
