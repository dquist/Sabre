package com.gordonfreemanq.sabre.customitems;

import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SignHandler;
import com.mongodb.BasicDBObject;

public class SecureSign extends SpecialBlock {

	protected boolean visible;
	
	public static final String blockName = "Secure Sign";
	
	public SecureSign(Location location, String typeName) {
		super(location, blockName);
		
		this.hasEffectRadius = false;
		this.visible = true;
		this.requireAccessForName = true;
	}
	

	/**
	 * Toggles the secure status of the sign
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		
		Reinforcement r = this.getReinforcement();
		if (r == null) {
			sp.msg(Lang.signNotReinforced);
			return;
		}
		
		if (!r.getGroup().isBuilder(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}

		this.visible = !visible;
		
		if (this.visible) {
			sp.msg(Lang.signNowVisible);
		} else {
			sp.msg(Lang.signNowHidden);
		}
		
		saveSettings();
		SignHandler.getInstance().updateSign(this);
	}
	
	
	/**
	 * Gets the visible status of the sign
	 * @return The visible status
	 */
	public boolean getVisible() {
		return this.visible;
	}
	
	
	/**
	 * Sets the visible status of the sign
	 * @param visible The new status
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	
	/**
	 * Updates the sign for a given player
	 */
	public void updatefor(SabrePlayer p) {
		SignHandler.getInstance().updateSign(this);
	}
	

	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		return new BasicDBObject("visible", visible);
	}
	
	
	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		if (o != null) {
			this.visible = o.getBoolean("visible", false);
		}
	}
	
	
	/**
	 * Handles a reinforcement broken event
	 * Does not get called for bypassed blocks.
	 * Called after onBlockBreaking and before onBlockBroken.
	 * @param p The player that broke the block
	 * @param e The event args
	 */
	@Override
	public void onReinforcementBroken(SabrePlayer p, BlockBreakEvent e) {
		// If the reinforcement is broken, this will make it so a normal
		// sign is dropped instead of a Secure Sign
		dropsBlock = false;
	}
	
	
	/**
	 * Handles the block broken event.
	 * Called after onBlockBreaking and onReinforcementBroken.
	 * @param p The player that broke the block
	 * @param e The event args
	 */
	@Override
	public void onBlockBroken(SabrePlayer p, BlockBreakEvent e) {
		if (!dropsBlock) {
			e.setCancelled(false); // Allows the normal sign to drop naturally
		}
	}
}
