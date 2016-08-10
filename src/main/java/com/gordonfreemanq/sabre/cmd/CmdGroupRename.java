package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.groups.SabreGroup;

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
