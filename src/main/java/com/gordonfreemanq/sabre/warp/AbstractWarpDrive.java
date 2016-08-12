package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.customitems.SpecialBlock;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.mongodb.BasicDBObject;

public abstract class AbstractWarpDrive extends SpecialBlock {
	
	// Location of the pad
	protected Location padLocation;
	
	public AbstractWarpDrive(Location location, String typeName) {
		super(location, typeName);
		
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
		
		TeleportPad pad = (TeleportPad)BlockManager.instance().getBlockAt(l);
		if (pad == null) {
			sp.msg(Lang.warpNoPadFound);
		}
		
		if (!pad.canPlayerModify(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}
		
		int maxDist = 32;
		
		// Pad and drive must be within radius and same world
		if (location.getWorld() != pad.getLocation().getWorld() ||
				Math.abs(location.getBlockX() - pad.getLocation().getBlockX()) > maxDist ||
				Math.abs(location.getBlockZ() - pad.getLocation().getBlockZ()) > maxDist) {
			sp.msg(Lang.warpTooFar, maxDist);
			return;
		}
		
		// Good to go, perform the link
		pad.setDriveLocation(this.location); // Link pad to the drive block
		pad.setDestPadLocation(null);
		pad.saveSettings();
		this.padLocation = pad.getLocation(); // Link the drive block to the pad
		this.saveSettings();
		sp.msg(Lang.warpLinkedPadDrive, maxDist);
		sp.getPlayer().getItemInHand().setItemMeta(null);
	}
	
	
	/**
	 * Gets the pad location
	 * @return The pad location
	 */
	public Location getPadLocation() {
		return this.padLocation;
	}
	
	
	/**
	 * Sets the pad location
	 * @param linkedPadLocation The pad location
	 */
	public void setPadLocation(Location padLocation) {
		this.padLocation = padLocation;
	}
	

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		BasicDBObject doc = new BasicDBObject();
		if (padLocation != null) {
			doc = doc.append("pad", SabreUtil.serializeLocation(this.padLocation));
		}
		
		return doc;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		if (o.containsField("pad")) {
			this.padLocation = SabreUtil.deserializeLocation(o.get("pad"));
		}
	}
	
	/**
	 * Clears some space above the destination pad
	 * @param destPad the destination pad
	 */
	protected void clearSpace(Location destPad) {

		// Clear some space 3 blocks above the pad
		Block b = destPad.getBlock().getRelative(BlockFace.UP);
		b.setType(Material.AIR);
		b = b.getRelative(BlockFace.UP);
		b.setType(Material.AIR);
	}
	
	/**
	 * Performs the warp to location
	 * @param from the source teleport pad
	 * @return true if warps succeeds
	 */
	public abstract boolean performWarp(SabrePlayer sp, TeleportPad sourcePad);
	
	
	/**
	 * Whether the drive supports direct linking
	 * @return
	 */
	public boolean canDirectLink() {
		return false;
	}
}
