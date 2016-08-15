package com.civfactions.sabre.cmd.snitch;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.snitch.Snitch;
import com.civfactions.sabre.snitch.SnitchController;

public class CmdSnitchNotify  extends SabreCommand {

	
	public CmdSnitchNotify()
	{
		super();
		
		this.aliases.add("notify");
		
		this.optionalArgs.put("toggle", "on");
		
		this.setHelpShort("Toggles snitch notifications");
		
		this.senderMustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		Location l = SnitchController.parseLocation(me, true);
		if (l == null) {
			return;
		}
		
		Snitch snitch = (Snitch)bm.getBlockAt(l);
		if (snitch == null) {
			msg(Lang.snitchNotFound);
			AbstractController.normalizeHeldController(me);
			return;
		}
		
		if (!snitch.canPlayerModify(me)) {
			msg(Lang.noPermission);
			return;
		}
		 
		boolean setTo = false;
		
		if (this.args.size() > 0) {
			setTo = this.argAsBool(0);
		} else {
			setTo = !snitch.getNotify();
		}
		
		snitch.setNotify(setTo);
		if (setTo) {
			msg(Lang.snitchNotifyEnabled);
		} else {
			msg(Lang.snitchNotifyDisabled);
		}
		
		ItemStack is = (new SnitchController(snitch)).toItemStack();
		me.getPlayer().getInventory().setItemInHand(is);
		
		
	}
}
