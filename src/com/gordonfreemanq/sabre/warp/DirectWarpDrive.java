package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.util.SabreUtil;

public class DirectWarpDrive extends AbstractWarpDrive {

	
	public static final String blockName = "Direct Warp Drive";
	
	
	/**
	 * Creates a new NetherWarpDrive instance
	 * @param location The drive location
	 * @param typeName The block type name
	 */
	public DirectWarpDrive(Location location, String typeName) {
		super(location, blockName);
	}
	
	
	/**
	 * Performs the warp
	 */
	public boolean performWarp(SabrePlayer sp, TeleportPad sourcePad) {

		Location sourcePadLocation = sourcePad.getLocation();
		Location destPadLocation = sourcePad.getDestPadLocation();
		boolean destIsValid = false;
		BlockManager bm = BlockManager.getInstance();
		TeleportPad destPad = null;
		
		// The drive must be linked to the overworld drive
		if (!this.padLocation.equals(sourcePadLocation)) {
			sp.msg(Lang.warpMissingDrive);
			return false;
		}
		
		if (destPadLocation == null) {
			sp.msg(Lang.warpMissingPad);
			return false;
		}
		
		// Check if there is a valid pad on the other end
		destPad = (TeleportPad)bm.getBlockAt(destPadLocation);
		if (destPad != null) {
			destIsValid = true;
		}
		
		if (!destIsValid) {
			sp.msg(Lang.warpNoPadFound);
			return false;
		}
		
		Location destWarpDriveLocation = destPad.getDriveLocation();
		if (destWarpDriveLocation == null) {
			sp.msg(Lang.warpMissingDestDrive);
		}
		
		SabreBlock destWarpDrive = (SabreBlock)bm.getBlockAt(destWarpDriveLocation);
		if (!(destWarpDrive instanceof DirectWarpDrive)) {
			sp.msg(Lang.warpMissingDestDrive);
		}
		
		this.clearSpace(destPadLocation);
		
		// Do the teleport
		sp.msg(Lang.warping);
		SabreUtil.tryToTeleport(sp.getPlayer(), destPadLocation);
		
		return true;
	}
	
	/**
	 * Whether the drive supports direct linking
	 * @return
	 */
	@Override
	public boolean canDirectLink() {
		return true;
	}
}
