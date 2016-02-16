package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.customitems.SpecialBlock;
import com.mongodb.BasicDBObject;

public abstract class AbstractWarpDrive extends SpecialBlock {
	
	private final WarpDriveType driveType;
	
	public AbstractWarpDrive(Location location, String typeName, WarpDriveType driveType) {
		super(location, typeName);
		this.driveType = driveType;
		
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
		
		// Pad and drive must be within radius and same world
		if (location.getWorld() != pad.getLocation().getWorld() ||
				Math.abs(location.getBlockX() - pad.getLocation().getBlockX()) > maxDist ||
				Math.abs(location.getBlockZ() - pad.getLocation().getBlockZ()) > maxDist) {
			sp.msg(Lang.warpTooFar, maxDist);
			return;
		}
		
		// Good to go, perform the link
		pad.setDriveLocation(this.location);
		pad.saveSettings();
		sp.msg(Lang.warpLinked, maxDist);
		sp.getPlayer().getItemInHand().setItemMeta(null);
	}
	
	
	/**
	 * Gets the warp drive type
	 * @return The drive type
	 */
	public WarpDriveType getDriveType() {
		return this.driveType;
	}

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		BasicDBObject doc = new BasicDBObject();
		doc = doc.append("warp_drive", true);
		
		return doc;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
	}
}
