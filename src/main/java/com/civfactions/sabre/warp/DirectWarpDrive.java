package com.civfactions.sabre.warp;

import org.bukkit.Location;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.SabreBlock;
import com.civfactions.sabre.util.SabreUtil;

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
		BlockManager bm = SabrePlugin.instance().getBlockManager();
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
		Location warpLocation = destPadLocation;
		//warpLocation.add(0, 1, 0);
		SabreUtil.tryToTeleport(sp.getPlayer(), warpLocation);
		
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
