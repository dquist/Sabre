package com.civfactions.sabre.cmd;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;

public class CmdGroupRename extends SabreCommand {

	public CmdGroupRename()
	{
		super();
		
		this.aliases.add("rename");

		this.requiredArgs.add("group");
		this.requiredArgs.add("new name");
		
		this.setHelpShort("Renames a group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		String groupName = this.argAsString(0);
		String newName = this.argAsString(1);
		
		SabreGroup g = checkGroupExists(groupName, true);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
		SabreMember m = g.getMember(me);
		if (m == null || !m.isOwner()) {
			msg(Lang.noPermission);
			return;
		}
		
		// Success
		gm.renameGroup(g, newName);
		msg(Lang.groupRenamed, groupName, newName);
	}
}
