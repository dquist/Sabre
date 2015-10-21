package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;


public class CmdAdminGive extends SabreCommand {

	public CmdAdminGive()
	{
		super();
		this.aliases.add("give");

		this.setHelpShort("Gives a custom item by name");
		
		this.errorOnToManyArgs = false;
		this.requiredArgs.add("item name");
		this.optionalArgs.put("amount", "1");
	}

	@Override
	public void perform() 
	{
		String itemName = this.args.get(0);
		
		SabreItemStack item = CustomItems.getInstance().getByName(itemName);
		if (item == null) {
			msg(Lang.adminInvalidItem);
			return;
		}
		
		int amount = this.argAsInt(1, 1);
		item.setAmount(amount);
		
		me.getPlayer().getInventory().setItemInHand(item);
	}
}
