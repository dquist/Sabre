package com.civfactions.sabre.cmd.factory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.cmd.SabreCommand;
import com.civfactions.sabre.customitems.FarmProspector;


public class CmdFactoryProspect extends SabreCommand {

	public CmdFactoryProspect()
	{
		super();
		this.aliases.add("prospect");

		this.setHelpShort("Creates a farm prospector");
		
		this.senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		ItemStack inHand = me.getPlayer().getItemInHand();
		if (!inHand.getType().equals(Material.STICK) 
				|| inHand.getAmount() != 1
				|| inHand.hasItemMeta()) {
			me.msg(Lang.factoryMustHoldStick);
			return;
		}
		
		ItemStack is = plugin.getCustomItems().getByName(FarmProspector.itemName);
		me.getPlayer().setItemInHand(is);
		me.msg(Lang.factoryCreatedProspector);
	}
}
