package com.gordonfreemanq.sabre.customitems;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.mongodb.BasicDBObject;

public class WarpDrive extends SpecialBlock {

	
	public static final String blockName = "Warp Drive";
	
	public WarpDrive(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
	}
	
	/**
	 * Allows the player to link the teleport pad with a warp drive
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		Location l = TeleportLinker.parseLocation(sp, true);
		if (l == null) {
			return;
		}
		
		TeleportPad pad = (TeleportPad)BlockManager.getInstance().getBlockAt(l);
		if (pad == null) {
			sp.msg(Lang.warpNoPadFound);
		}
		
		if (!pad.canPlayerModify(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}
		
		int maxDist = 48;
		
		if (Math.abs(location.getBlockX() - pad.getLocation().getBlockX()) > maxDist ||
				Math.abs(location.getBlockZ() - pad.getLocation().getBlockZ()) > maxDist) {
			sp.msg(Lang.warpTooFar, maxDist);
			return;
		}
		
		// Good to go, perform the link
		pad.setDriveLocation(this.location);
		pad.saveSettings();
		sp.msg(Lang.warpLinked, maxDist);
	}

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		return null;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
	}
}
