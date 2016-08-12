package com.gordonfreemanq.sabre.cmd.pearl;

import java.util.logging.Level;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;

public class CmdPearlLocate extends SabreCommand {

	public CmdPearlLocate()
	{
		super();
		this.aliases.add("locate");
		this.aliases.add("ppl");

		this.senderMustBePlayer = true;
		this.setHelpShort("Locates your prison pearl");
	}

	@Override
	public void perform() 
	{		
		PrisonPearl pp = pearls.getById(me.getID());
		
		if (pp == null) {
			msg(Lang.pearlNotImprisoned);
			return;
		}
		
		if (pp.verifyLocation()) {

			Location l = pp.getHolder().getLocation();
			String name = pp.getHolder().getName();
			
			msg(Lang.pearlPearlIsHeld, name, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
			
			String bcastMsg = SabrePlugin.instance().txt.parse(Lang.pearlBroadcast, me.getName(), 
					name, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
			
			for(SabrePlayer p : me.getBcastPlayers()) {
				if (p.isOnline()) {
					p.msg(bcastMsg);
				}
			}
			
		} else {
			SabrePlugin.log(Level.INFO, "%s is freed because the pearl could not be located.", pp.getLocation());
			pearls.freePearl(pp);
		}
	}
}
