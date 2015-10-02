package com.gordonfreemanq.sabre.cmd;

import org.bukkit.Location;
import org.bukkit.World;

import com.gordonfreemanq.sabre.Lang;


public class CmdAdminSetSpawn extends SabreCommand {

	public CmdAdminSetSpawn()
	{
		super();
		this.aliases.add("setspawn");

		this.setHelpShort("Sets the spawn location");
		
		this.errorOnToManyArgs = false;
	}

	@Override
	public void perform() 
	{
		Location l = me.getPlayer().getLocation();
		World w = me.getPlayer().getWorld();
		w.setSpawnLocation(l.getBlockX(), l.getBlockX(), l.getBlockX());
		me.msg(Lang.adminSetSpawn);
	}
}
