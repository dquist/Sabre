package com.gordonfreemanq.sabre.warp;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
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
	
	protected boolean netherGenerated;
	
	public TeleportPad(Location location, String typeName) {
		super(location, typeName);
		
		this.hasEffectRadius = false;
		this.netherGenerated = false;
		this.requireAccesstoInteract = false;
	}
	
	
	/**
	 * Allows the player to link the teleport pad with a warp drive
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			return;
		}
		
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
				sp.getPlayer().getItemInHand().setItemMeta(null);
				return;
			}
			
			Location destDriveLocation = destPad.getDriveLocation();
			AbstractWarpDrive destDrive = null;
			AbstractWarpDrive sourceDrive = null;
			
			// Verify warp drive exists
			if (destDriveLocation != null) {
				destDrive = (AbstractWarpDrive)bm.getBlockAt(destDriveLocation);
			}
			if (driveLocation != null) {
				sourceDrive = (AbstractWarpDrive)bm.getBlockAt(driveLocation);
			}
			
			if (destDrive != null && destDrive.canDirectLink() && sourceDrive != null && sourceDrive.canDirectLink()) {
				this.destPadLocation = l;
				this.saveSettings();
				destPad.destPadLocation = this.location;
				destPad.saveSettings();
				sp.msg(Lang.warpLinkedPadToPad);
				sp.getPlayer().getItemInHand().setItemMeta(null);
			} else {
				sp.msg(Lang.warpDirectMissingDrive);
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
		
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}

		// Ignore stick or pick interact
		Material handItemType = sp.getPlayer().getItemInHand().getType();
		if (	   handItemType.equals(Material.STONE_PICKAXE)
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
		
		if (!sp.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).equals(this.location.getBlock())) {
			sp.msg(Lang.warpStandOnPad);
			return;
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
		
		if (netherGenerated) {
			doc = doc.append("generated", true);
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
		
		if (o.containsField("generated")) {
			this.netherGenerated = o.getBoolean("generated", false);
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
	
	@Override
	public boolean getTellBlockName() {
		if (this.driveLocation == null) {
			return true;
		}
		return false;
	}
	

	@Override
	public boolean getDropsBlock() {
		// Nether generated teleport pads should not drop their block
		if (this.netherGenerated) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * Gets whether the block was generated in the nether
	 * @return true if it was generated
	 */
	public boolean getNetherGenerated() {
		return this.netherGenerated;
	}
	
	
	/**
	 * Sets whether the pad was nether generated
	 * @param netherGenerated whether it was nether generated
	 */
	public void setNetherGenerated(boolean netherGenerated) {
		this.netherGenerated = netherGenerated;
	}
}
