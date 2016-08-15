package com.civfactions.sabre.cmd.snitch;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.snitch.Snitch;
import com.civfactions.sabre.snitch.SnitchController;

public class CmdSnitchRefresh  extends SabreCommand {

	
	public CmdSnitchRefresh()
	{
		super();
		
		this.aliases.add("refresh");
		
		this.setHelpShort("Refreshes the controller");
		
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
		
		ItemStack is = (new SnitchController(snitch)).toItemStack();
		me.getPlayer().getInventory().setItemInHand(is);
		
		
	}
}
