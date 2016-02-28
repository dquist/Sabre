package com.gordonfreemanq.sabre.cmd.factory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.cmd.SabreCommand;
import com.gordonfreemanq.sabre.customitems.FarmProspector;


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
		
		ItemStack is = CustomItems.getInstance().getByName(FarmProspector.itemName);
		me.getPlayer().setItemInHand(is);
		me.msg(Lang.factoryCreatedProspector);
	}
}
