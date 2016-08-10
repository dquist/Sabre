package com.gordonfreemanq.sabre.cmd.snitch;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.cmd.SabreCommand;

public class CmdSnitchFind  extends SabreCommand {

	
	public CmdSnitchFind()
	{
		super();
		
		this.aliases.add("find");
		
		this.requiredArgs.add("x");
		this.requiredArgs.add("y");
		this.requiredArgs.add("z");
		this.optionalArgs.put("world", "world");
		
		this.setHelpShort("Finds a snitch at a location.");
	}
	
	@Override
	public void perform()
	{
		int x = this.argAsInt(0);
		int y = this.argAsInt(1);
		int z = this.argAsInt(2);
		String world = "world";
		
		if (args.size() > 3) {
			world = this.argAsString(3);
		}
		
		World w = Bukkit.getWorld(world);
		if (w == null) {
			msg(Lang.unknownWorld, world);
			return;
		}
		
		Location l = new Location(w, x, y, z);
		
		Snitch snitch = (Snitch)bm.getBlockAt(l);
		if (snitch == null || !snitch.canPlayerModify(me)) {
			msg(Lang.snitchNotFound);
			return;
		}
		
		snitch.createSnitchController(me);
	}
}
