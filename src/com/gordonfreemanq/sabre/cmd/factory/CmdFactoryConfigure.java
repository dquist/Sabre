package com.gordonfreemanq.sabre.cmd.factory;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.factory.FactoryController;


public class CmdFactoryConfigure extends SabreCommand {

	public CmdFactoryConfigure()
	{
		super();
		this.aliases.add("configure");

		this.setHelpShort("Configures a factory");
		
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
		
		if (factory.getConfigureMode()) {
			factory.setConfigureMode(false);
			msg("<i>Disabled factory configuration.");
			return;
		}
		
		factory.setConfigureMode(true);
		factory.setInputLocation(null);
		factory.setOutputLocation(null);
		factory.setFuelLocation(null);
		msg("<g>Enabled factory configuration.");
		msg("<i>Hit a chest for the factory input.");
	}
}
