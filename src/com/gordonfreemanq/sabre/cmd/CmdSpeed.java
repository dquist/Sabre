package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;


public class CmdSpeed extends SabreCommand {

	public CmdSpeed()
	{
		super();
		this.aliases.add("speed");

		this.setHelpShort("Sets fly speed");
		
		this.errorOnToManyArgs = true;
		this.requiredArgs.add("speed");		

		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		int speed = this.argAsInt(0, 0);
		if (speed > 10) {
			speed = 10;
		} else if (speed < 0) {
			speed = 0;
		}
		
		float setSpeed = ((float)speed / 10);
		
		me.getPlayer().setFlySpeed((float)setSpeed);
		me.msg("<i>Set fly speed to <c>%d", speed);
	}
}
