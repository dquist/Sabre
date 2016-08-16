package com.civfactions.sabre.cmd.pearl;

import java.util.logging.Level;

import org.bukkit.Location;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.prisonpearl.PrisonPearl;

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
			
			String bcastMsg = SabrePlugin.instance().txt().parse(Lang.pearlBroadcast, me.getName(), 
					name, l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName());
			
			for(IPlayer p : me.getBcastPlayers()) {
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
