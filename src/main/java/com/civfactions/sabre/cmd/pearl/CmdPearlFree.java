package com.civfactions.sabre.cmd.pearl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

public class CmdPearlFree extends SabreCommand {

	public CmdPearlFree()
	{
		super();
		this.aliases.add("free");

		this.senderMustBePlayer = true;
		this.setHelpShort("Frees a prison pearl");
	}

	@Override
	public void perform() 
	{
		PrisonPearl pp = pearls.getPearlByItem(me.getPlayer().getItemInHand());
		if (pp == null) {
			msg(Lang.pearlNotHoldingPearl);
			return;
		}
		
		if (pearls.freePearl(pp)) {
			me.msg(Lang.pearlYouFreed, pp.getName());
			me.getPlayer().setItemInHand(new ItemStack(Material.AIR));
			
			if (!pp.getPlayer().isOnline()) {
				pm.setFreedOffline(pp.getPlayer(),  true);
			}
		}
		
	}
}
