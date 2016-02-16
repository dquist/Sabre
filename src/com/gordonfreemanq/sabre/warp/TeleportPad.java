package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;
import org.bukkit.Material;
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
	
	// Location of the destination pad
	protected Location destPadLocation;
	
	public TeleportPad(Location location, String typeName) {
		super(location, typeName);
		
		this.hasEffectRadius = false;
	}
	
	
	/**
	 * Allows the player to link the teleport pad with a warp drive
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		Reinforcement r = this.getReinforcement();
		BlockManager bm = BlockManager.getInstance();
		
		if (r != null && !r.getGroup().isBuilder(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}
		
		// Attempt to link
		Location l = TeleportLinker.parseLocation(sp, false);
		if (l != null) {
			TeleportPad destPad = (TeleportPad)bm.getBlockAt(l);
			if (destPad == null) {
				sp.msg(Lang.warpNoPadFound);
				return;
			}
			
			Location destDriveLocation = destPad.getDriveLocation();
			AbstractWarpDrive destDrive = null;
			
			// Verify warp drive exists
			if (destDriveLocation != null) {
				destDrive = (AbstractWarpDrive)bm.getBlockAt(destDriveLocation);
			}
			
			if (destDrive != null && destDrive.canDirectLink()) {
				this.destPadLocation = l;
				this.saveSettings();
				destPad.destPadLocation = this.destPadLocation;
				destPad.saveSettings();
				sp.msg(Lang.warpLinkedPadToPad);
			} else {
				sp.msg(Lang.warpMissingDrive);
			}
			
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

		// Ignore stick or pick interact
		Material handItemType = sp.getPlayer().getItemInHand().getType();
		if (handItemType.equals(Material.STICK)
				|| handItemType.equals(Material.STONE_PICKAXE)
				|| handItemType.equals(Material.IRON_PICKAXE)
				|| handItemType.equals(Material.GOLD_PICKAXE)
				|| handItemType.equals(Material.DIAMOND_PICKAXE)) {
			return;
		}
		
		// Prevent pearled players from teleporting
		if (PearlManager.getInstance().isImprisoned(sp)) {
			sp.msg(Lang.pearlCantDoThat);
			return;
		}
		
		AbstractWarpDrive drive = null;
		
		// Verify warp drive exists
		if (driveLocation != null) {
			drive = (AbstractWarpDrive)BlockManager.getInstance().getBlockAt(driveLocation);
		}
		
		if (drive != null) {
			drive.performWarp(sp, this);
		} else {
			sp.msg(Lang.warpMissingDrive);
		}
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
		
		if (destPadLocation != null) {
			doc = doc.append("pad", SabreUtil.serializeLocation(this.destPadLocation));
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
		if (o.containsField("pad")) {
			this.destPadLocation = SabreUtil.deserializeLocation(o.get("pad"));
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
	
	/**
	 * Gets the destination pad location
	 * @return The destination pad location
	 */
	public Location getDestPadLocation() {
		return this.destPadLocation;
	}
	
	/**
	 * Sets the destination pad location
	 * @param destPadLocation The destination pad location
	 */
	public void setDestPadLocation(Location destPadLocation) {
		this.destPadLocation = destPadLocation;
	}
}
