package com.gordonfreemanq.sabre.cmd.factory;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.FactoryController;


public class CmdFactoryUpgrade extends SabreCommand {

	public CmdFactoryUpgrade()
	{
		super();
		this.aliases.add("upgrade");

		this.setHelpShort("Shows/hides the upgrade recipes");
		
		this.senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		// Try to parse a location
		Location l = FactoryController.parseLocation(me, true);
		
		// No location
		if (l == null) {
			return;
		}
		
		BaseFactory factory = (BaseFactory)bm.getFactories().get(l);
		
		if (factory == null) {
			msg(Lang.factoryNotFound);
			AbstractController.normalizeHeldController(me);
			return;
		}
		
		if (factory.getRunning()) {
			msg(Lang.factoryCantDoWhileRunning);
			return;
		}
		
		if (factory.getUpgradeMode()) {
			factory.setUpgradeMode(false);
			msg("<i>Disabled factory upgrade mode.");
			return;
		}
		
		factory.setUpgradeMode(true);
		msg("<g>Enabled factory upgrade recipes.");
	}
}
