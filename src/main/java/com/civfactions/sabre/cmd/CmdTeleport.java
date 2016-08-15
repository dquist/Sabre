package com.civfactions.sabre.cmd;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.util.Permission;
import com.civfactions.sabre.util.SabreUtil;


public class CmdTeleport extends SabreCommand {

	public CmdTeleport()
	{
		super();
		this.aliases.add("tp");

		this.setHelpShort("Teleports to a player or location");
		
		this.optionalArgs.put("player", "");
		this.optionalArgs.put("world", "");
		this.optionalArgs.put("x", "");
		this.optionalArgs.put("y", "");
		this.optionalArgs.put("z", "");

		this.permission = Permission.ADMIN.node;
		this.visibility = CommandVisibility.SECRET;
		this.senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		if (args.size() == 0) {
			me.msg(this.getUsageTemplate());
			return;
		}
		
		int coordIndex = 0;
		
		World w = me.getPlayer().getWorld();
		
		
		if (args.size() >= 4) {
			w = Bukkit.getWorld(this.argAsString(0));
			if (w == null) {
				me.msg("<b>Unknown world <c>%s", this.argAsString(0));
				return;
			}
			
			coordIndex = 1;
		} 
		
		if (args.size() >= 3) {

			int x = this.argAsInt(coordIndex, Integer.MAX_VALUE);
			int y = this.argAsInt(coordIndex + 1, Integer.MAX_VALUE);
			int z = this.argAsInt(coordIndex + 2, Integer.MAX_VALUE);
			
			if (x == Integer.MAX_VALUE || y == Integer.MAX_VALUE || z == Integer.MAX_VALUE) {
				me.msg("<b>bad coordinate values");
				return;
			}
			
			Location l = new Location(w, x, y, z);
			SabreUtil.tryToTeleport(me.getPlayer(), l);
			return;
			
		} else {
			String playerName = this.argAsString(0);
			
			SabrePlayer p = this.strAsPlayer(playerName);
			if (p == null) {
				me.msg(Lang.unknownPlayer, playerName);
				return;
			}
			
			if (!p.isOnline()) {
				me.msg("<c>%s <b>is not online.", playerName);
				return;
			}

			SabreUtil.tryToTeleport(me.getPlayer(), p.getPlayer().getLocation());
		}
	}
}
