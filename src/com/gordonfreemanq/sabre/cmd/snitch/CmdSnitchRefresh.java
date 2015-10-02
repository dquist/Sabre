package com.gordonfreemanq.sabre.cmd.snitch;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.snitch.SnitchController;

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
