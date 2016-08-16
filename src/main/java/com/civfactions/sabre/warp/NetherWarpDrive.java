package com.civfactions.sabre.warp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabreConfig;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.SabreItemStack;
import com.civfactions.sabre.util.SabreUtil;

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
		double destX = l.getBlockX() >> 2; // divide by 4
		double destZ = l.getBlockZ() >> 2; // divide by 4
		double destY = Math.min(120, l.getBlockY() >> 1); // divide by 2
		
		return new Location(netherWorld, destX, destY, destZ);
	}
	
	
	/**
	 * Performs the warp
	 */
	@SuppressWarnings("deprecation")
	public boolean performWarp(IPlayer sp, TeleportPad sourcePad) {

		Location sourcePadLocation = sourcePad.getLocation();
		Location destPadLocation = sourcePad.getDestPadLocation();
		boolean destIsValid = false;
		BlockManager bm = SabrePlugin.instance().getBlockManager();
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
		
		SabreItemStack is = SabrePlugin.instance().getCustomItems().getByName(TeleportPad.blockName);
		
		// Check if there is a valid pad on the other end
		if (destPadLocation != null) {
			destPad = (TeleportPad)bm.getBlockAt(destPadLocation);
			if (destPad != null && destPadLocation.getBlock().getType() == is.getType()) {
				destIsValid = true;
			}
		}
		
		if (!destIsValid) {
			destPadLocation = getDefaultNetherLocation(sourcePadLocation);
		}

		// Only create a new teleport pad when going from Overworld -> Nether
		if (!destIsValid && sourcePadLocation.getWorld().getEnvironment() == Environment.NORMAL) {
			destPadLocation.getChunk().load();
			
			// Create the physical block
			Block b = destPadLocation.getBlock();
			b.setType(is.getType());
			b.setData(is.getData().getData());
			destIsValid = true;
			
			destPad = (TeleportPad)SabrePlugin.instance().getBlockManager().createBlockFromItem(is, destPadLocation);
			bm.addBlock(destPad);
		}

		// Link the two pads together 
		destPad.setDriveLocation(sourcePad.getDriveLocation());
		destPad.setDestPadLocation(sourcePadLocation);
		destPad.setNetherGenerated(true);
		destPad.saveSettings();
		sourcePad.setDestPadLocation(destPadLocation);
		sourcePad.saveSettings();
		
		if (!destIsValid) {
			sp.msg(Lang.warpNoPadFound);
			return false;
		}
		
		// If going from Nether -> Overworld, warp drive must be linked to destination
		if (sourcePadLocation.getWorld().getEnvironment() == Environment.NETHER) {
			if (!this.padLocation.equals(destPadLocation)) {
				sp.msg(Lang.warpMissingDrive);
				return false;
			}
		}
		
		this.clearSpace(destPadLocation);
		
		// Do the teleport
		sp.msg(Lang.warping);
		Location warpLocation = destPadLocation;
		//warpLocation.add(0, 1, 0);
		SabreUtil.tryToTeleport(sp.getPlayer(), warpLocation);
		
		return true;
	}
}
