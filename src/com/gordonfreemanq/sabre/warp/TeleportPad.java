package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.customitems.SpecialBlock;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.mongodb.BasicDBObject;

public class TeleportPad extends SpecialBlock {

	public static final String blockName = "Teleport Pad";

	// Location of the linked warp drive
	protected Location driveLocation;
	
	public TeleportPad(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
	}
	
	
	/**
	 * Allows the player to link the teleport pad with a warp drive
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		Reinforcement r = this.getReinforcement();
		if (r == null) {
			sp.msg(Lang.warpNotReinforced);
			return;
		}
		
		if (!r.getGroup().isBuilder(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}
		
		ItemStack is = (new TeleportLinker(this)).toItemStack();
		sp.getPlayer().getInventory().setItemInHand(is);
		sp.msg(Lang.warpHitDrive);
	}
	

	/**
	 * Teleport the player
	 * @param p The player interacting
	 */
	public void onInteract(PlayerInteractEvent e, SabrePlayer sp) {
		// Prevent pearled players from teleporting
		if (PearlManager.getInstance().isImprisoned(sp)) {
			sp.msg(Lang.pearlCantDoThat);
			return;
		}
		
		if (driveLocation == null) {
			sp.msg(Lang.warpMissingDrive);
			return;
		}
		
		BlockManager bm = BlockManager.getInstance();
		
		AbstractWarpDrive drive = (AbstractWarpDrive)bm.getBlockAt(driveLocation);
		if (drive == null) {
			sp.msg(Lang.warpMissingDrive);
			return;
		}
		
		// Get the destination location
		Location dest = drive.getDriveType().getTeleportLocation(sp.getPlayer().getLocation());
		
		// Make sure there is a pad on the other end
		TeleportPad destPad = (TeleportPad)bm.getBlockAt(dest);		
		if (destPad == null) {
			destPad = new TeleportPad(dest, blockName);
			bm.addBlock(destPad);
		}
		
		// Clear some space 2 blocks above the pad
		Block b = dest.getBlock().getRelative(BlockFace.UP);
		b.setType(Material.AIR);
		b = b.getRelative(BlockFace.UP);
		b.setType(Material.AIR);
		
		// Do the teleport
		SabreUtil.tryToTeleport(sp.getPlayer(), dest);
	}
	

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		BasicDBObject doc = new BasicDBObject();
		
		if (driveLocation != null) {
			doc = doc.append("drive", SabreUtil.serializeLocation(this.driveLocation));
		}
		
		return doc;
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		if (o.containsField("drive")) {
			this.driveLocation = SabreUtil.deserializeLocation(o.get("drive"));
		}
	}
	
	/**
	 * Gets the drive location
	 * @return The drive location
	 */
	public Location getDriveLocation() {
		return this.driveLocation;
	}
	
	/**
	 * Sets the drive location
	 * @param driveLocation The drive location
	 */
	public void setDriveLocation(Location driveLocation) {
		this.driveLocation = driveLocation;
	}
}
