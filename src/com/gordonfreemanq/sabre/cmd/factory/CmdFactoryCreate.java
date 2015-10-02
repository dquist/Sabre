package com.gordonfreemanq.sabre.cmd.factory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.CustomItems;
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
		if (!me.getPlayer().getItemInHand().getType().equals(Material.FURNACE)) {
			me.msg(Lang.factoryMustHoldFurnace);
			return;
		}
		
		ItemStack is = CustomItems.getInstance().getByName("Base Factory");
		me.getPlayer().setItemInHand(is);
		me.msg(Lang.factoryCreatedBaseFactory);
	}
}
