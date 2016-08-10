package com.gordonfreemanq.sabre.customitems;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;
import com.gordonfreemanq.sabre.cmd.pearl.CmdPearl;
import com.gordonfreemanq.sabre.util.SabreUtil;

public class MokshaRod extends SabreItemStack {

	public static final String itemName = "Moksha Rod";
	
	private int strength;
	private SabrePlayer boundPlayer;
	
	public MokshaRod() {
		super(Material.BLAZE_ROD, itemName, 1);
	}
	
	
	/**
	 * Gets the rod strength
	 * @return The rod strength
	 */
	public int getStrength() {
		return this.strength;
	}
	
	
	/**
	 * Sets the rod strength
	 * @param strength The new strength
	 */
	public void setStrength(int strength) {
		if (strength != this.strength) {
			this.strength = strength;
			this.updateLore();
		}
	}
	
	
	/**
	 * Gets the bound player
	 * @return The bound player
	 */
	public SabrePlayer getPlayer() {
		return this.boundPlayer;
	}
	
	
	/**
	 * Sets the bound player
	 * @param boundPlayer The bound player
	 */
	public void setBoundPlayer(SabrePlayer boundPlayer) {
		this.boundPlayer = boundPlayer;
		this.updateLore();
	}
	
	
	private static String strengthPrefix = parse("<a>Strength: <n>");
	private static Pattern strengthPattern = Pattern.compile(parse(strengthPrefix + "(.+)"));
	
	
	/**
	 * Gets the lore
	 * @return The lore
	 */
	@Override
	public List<String> getLore() {
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<c>An ancient tool used to summon the dead."));
		lore.add(parse("<a>Strength: <n>%d", this.strength));
		
		if (boundPlayer == null) {
			lore.add(parse("<a>Player: "));
			lore.add(parse("<a>UUID: "));
		} else {
			lore.add(parse("<a>Player: <n>%s", boundPlayer.getName()));
			lore.add(parse("<a>UUID: <n>%s", boundPlayer.getID().toString()));
		}
		
		if (CmdPearl.getInstance() != null) {
			lore.add(parse(""));
			lore.add(parse("<l>Commands:"));
			lore.add(parse(CmdPearl.getInstance().cmdMokshaBind.getUseageTemplate(true)));
			lore.add(parse(CmdPearl.getInstance().cmdMokshaJailbreak.getUseageTemplate(true)));
		}
		
		return lore;
	}
	
	
	/**
	 * Gets a Moksha Rod item stack from a normal item stack if possible
	 * @param is The source itemstack
	 * @return The Moksha Rod item stack
	 */
	public static MokshaRod getRodFromItem(ItemStack is) {
		if (is instanceof MokshaRod) {
			return (MokshaRod)is;
		}
		
		MokshaRod created = new MokshaRod();
		
		// See if we have the right item match
		if (!isMoksha(is)) {
			return null;
		}
		
		// Should be good at this point
		try {
			List<String> lore = is.getItemMeta().getLore();
			
			if (SabreUtil.loreContainsString(lore, "Strength:")) {
				created.setStrength(SabreUtil.parseLoreInt(lore, strengthPrefix, strengthPattern));
			}
			
			if (SabreUtil.loreContainsString(lore, "UUID:")) {
				created.setBoundPlayer(PlayerManager.getInstance().getPlayerById(SabreUtil.parseLoreId(lore)));
			}
			
			is.setItemMeta(created.getItemMeta());
			return created;
			
		} catch (Exception ex){
			return null;
		}
	}
	
	
	public static boolean isMoksha(ItemStack is) {
		if (!is.getType().equals(Material.BLAZE_ROD)) {
			return false;
		}

		ItemMeta im = is.getItemMeta();
		if (im == null) {
			return false;
		}
		
		if (!im.hasDisplayName() || !im.getDisplayName().equals(itemName)) {
			return false;
		}
		
		return im.hasLore();
	}
	
	
	/**
	 * Gets a rod from an inventory
	 * @param inv The inventory to search
	 * @return The list of contained pearls
	 */
	public static ItemStack getFromInventory(Inventory inv) {		
		for (ItemStack is : inv.all(Material.BLAZE_ROD).values()) {
			if (isMoksha(is)) {
				return is;
			}
		}
		
		return null;
	}
}
