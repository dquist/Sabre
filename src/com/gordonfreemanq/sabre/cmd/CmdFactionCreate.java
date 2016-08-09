package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;


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
		SabreGroup faction = gm.getFactionByName(factionName);
		
		// Does the faction already exist?
		if (faction != null) {
			SabreMember m = faction.getMember(me);
			if (m == null) {
				msg(Lang.factionAlreadyExists, faction.getName());
			} else if (m.getRank() == Rank.OWNER) {
				msg(Lang.groupAlreadyOwn, faction.getName());
			} else {
				msg(Lang.groupAlreadyMember, faction.getName());
			}
			return;
		}
		
		// Success
		faction = gm.createNewGroup(me, factionName, true);
		gm.addGroup(me, faction);
		msg(Lang.groupCreated, factionName);
	}
}
