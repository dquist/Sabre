package com.gordonfreemanq.sabre.cmd;

import org.bukkit.GameMode;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.util.Permission;


public class CmdAdminGamemode extends SabreCommand {

	public CmdAdminGamemode()
	{
		super();
		this.aliases.add("gamemode");
		this.aliases.add("gm");

		this.setHelpShort("Set game mode");
		
		this.requiredArgs.add("mode");
		
		this.errorOnToManyArgs = false;
		this.senderMustBePlayer = true;
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void perform() 
	{
		Integer modeNum = this.argAsInt(0);
		GameMode mode = null;
		
		try {
			if (modeNum != null) {
				if (modeNum < 0 || modeNum > 4) {
					me.msg(Lang.adminInvalidMode);
					return;
				}
				mode = GameMode.getByValue(modeNum);
			}
			
			if (mode == null) {
				mode = GameMode.valueOf(this.args.get(0).toUpperCase());
			}
		}
		finally {
			if (mode == null) {
				me.msg(Lang.adminInvalidMode);
				return;
			}
		}
		
		
		me.getPlayer().setGameMode(mode);
		me.msg(Lang.adminUpdatedMode, mode.toString());
	}
}
