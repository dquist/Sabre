package com.civfactions.sabre.cmd.snitch;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.snitch.Snitch;
import com.civfactions.sabre.snitch.SnitchController;

public class CmdSnitchRename  extends SabreCommand {

	
	public CmdSnitchRename()
	{
		super();
		
		this.aliases.add("rename");
		
		this.requiredArgs.add("name");
		
		this.setHelpShort("Sets the snitch name");
		
		this.senderMustBePlayer = true;
		this.errorOnToManyArgs = false;
	}
	
	@Override
	public void perform()
	{
		Location l = SnitchController.parseLocation(me, true);
		if (l == null) {
			msg(Lang.snitchNotFound);
			AbstractController.normalizeHeldController(me);
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
		 
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i));
			sb.append(" ");
		}
		
		String name = sb.toString().trim();
		snitch.setSnitchName(name);
		msg("<g>Changed name to <c>%s<g>.", name);
		ItemStack is = (new SnitchController(snitch)).toItemStack();
		me.getPlayer().getInventory().setItemInHand(is);
		
		
	}
}
