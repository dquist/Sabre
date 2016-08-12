package com.gordonfreemanq.sabre.cmd.factory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.cmd.SabreCommand;


public class CmdFactoryCreate extends SabreCommand {

	public CmdFactoryCreate()
	{
		super();
		this.aliases.add("create");

		this.setHelpShort("Creates a base factory");
		
		this.senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		ItemStack inHand = me.getPlayer().getItemInHand();
		if (!inHand.getType().equals(Material.FURNACE) 
				|| inHand.getAmount() != 1
				|| inHand.hasItemMeta()) {
			me.msg(Lang.factoryMustHoldFurnace);
			return;
		}
		
		ItemStack is = plugin.getCustomItems().getByName("Base Factory");
		me.getPlayer().setItemInHand(is);
		me.msg(Lang.factoryCreatedBaseFactory);
	}
}
