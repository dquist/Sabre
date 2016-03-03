package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.core.CommandVisibility;
import com.gordonfreemanq.sabre.core.Permission;


public class CmdAdminGive extends SabreCommand {

	public CmdAdminGive()
	{
		super();
		this.aliases.add("give");

		this.setHelpShort("Gives a custom item by name");
		
		this.errorOnToManyArgs = false;
		this.requiredArgs.add("item name");
		this.optionalArgs.put("amount", "1");
		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
	}

	@Override
	public void perform() 
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.size(); i++) {
			sb.append(args.get(i));
			sb.append(" ");
		}
		
		String itemName = sb.toString().trim();
		
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
