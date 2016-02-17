package com.gordonfreemanq.sabre.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.util.SabreUtil;

public class NetherWarpDrive extends AbstractWarpDrive {

	
	public static final String blockName = "Nether Warp Drive";
	
	
	/**
	 * Creates a new NetherWarpDrive instance
	 * @param location The drive location
	 * @param typeName The block type name
	 */
	public NetherWarpDrive(Location location, String typeName) {
		super(location, blockName);
	}
	
	
	/**
	 * Gets the default nether warp location
	 * @param l The source location
	 * @return The nether location
	 */
	protected Location getDefaultNetherLocation(Location l) {
		World netherWorld = Bukkit.getWorld(SabreConfig.NETHER_WORLD_NAME);
		double destX = l.getBlockX() >> 3; // divide by 8
		double destZ = l.getBlockZ() >> 3; // divide by 8
		double destY = l.getBlockY();
		
		return new Location(netherWorld, destX, destY, destZ);
	}
	
	
	/**
	 * Performs the warp
	 */
	@SuppressWarnings("deprecation")
	public boolean performWarp(SabrePlayer sp, TeleportPad sourcePad) {

		Location sourcePadLocation = sourcePad.getLocation();
		Location destPadLocation = sourcePad.getDestPadLocation();
		boolean destIsValid = false;
		BlockManager bm = BlockManager.getInstance();
		TeleportPad destPad = null;
		
		// The drive must be linked to the overworld pad
		if (sourcePadLocation.getWorld().getEnvironment() == Environment.NORMAL) {
			if (!this.padLocation.equals(sourcePadLocation)) {
				sp.msg(Lang.warpMissingDrive);
				return false;
			}
			
			// Default to scaling of 8
			if (destPadLocation == null) {
				destPadLocation = getDefaultNetherLocation(sourcePadLocation);
			}
		}
		
		// Check if there is a valid pad on the other end
		if (destPadLocation != null) {
			destPad = (TeleportPad)bm.getBlockAt(destPadLocation);
			if (destPad != null) {
				destIsValid = true;
			}
		}

		// Only create a new teleport pad when going from Overworld -> Nether
		if (!destIsValid && sourcePadLocation.getWorld().getEnvironment() == Environment.NORMAL) {
			destPad = new TeleportPad(destPadLocation, TeleportPad.blockName);
			destPad.setDisplayName(TeleportPad.blockName);
			bm.addBlock(destPad);
			
			// Create the physical block
			Block b = destPadLocation.getBlock();
			SabreItemStack is = CustomItems.getInstance().getByName(TeleportPad.blockName);
			b.setType(is.getType());
			b.setData(is.getData().getData());
			destIsValid = true;
		}
		
		if (!destIsValid) {
			sp.msg(Lang.warpNoPadFound);
			return false;
		}
		
		// Link the two pads together 
		destPad.setDriveLocation(sourcePad.getDriveLocation());
		destPad.setDestPadLocation(sourcePadLocation);
		destPad.saveSettings();
		sourcePad.setDestPadLocation(destPadLocation);
		sourcePad.saveSettings();
		
		this.clearSpace(destPadLocation);
		
		// Do the teleport
		sp.msg(Lang.warping);
		SabreUtil.tryToTeleport(sp.getPlayer(), destPadLocation);
		
		return true;
	}
}
