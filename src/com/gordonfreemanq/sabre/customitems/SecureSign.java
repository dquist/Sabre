package com.gordonfreemanq.sabre.customitems;

import org.bukkit.Location;
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
	
	
	public boolean getVisible() {
		return this.visible;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
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
	 * Updates the sign for a player
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
}
