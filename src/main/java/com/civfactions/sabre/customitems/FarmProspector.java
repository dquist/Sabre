package com.civfactions.sabre.customitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.blocks.SabreItemStack;
import com.civfactions.sabre.util.SabreUtil;

public class FarmProspector extends SabreItemStack {	
		
	public static String itemName = "Farm Prospector";
	
	public FarmProspector() {
		super(Material.STICK, itemName, 1);
	}
	
	/**
	 * Gets the lore
	 * @return The lore
	 */
	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<i>Use this tool to show the farm fertility of a given chunk."));
		lore.add("");
		lore.add(parse("<c>The average fertility of all chunks in a farm will determine the"));
		lore.add(parse("<c>total farm fertility."));
		lore.add(parse("<c>A farm with a higher fertility value will have a higher output rate."));
		
		return lore;
	}
	
	
	@Override
	public void onPlayerInteract(SabrePlayer sp, PlayerInteractEvent e) {
		double fertility = SabreUtil.getChunkFertility(e.getPlayer().getLocation());
		
		sp.msg(Lang.factoryFertility, Math.round(fertility * 100.0));
	}
}
