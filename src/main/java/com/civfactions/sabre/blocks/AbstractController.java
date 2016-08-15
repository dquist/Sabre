package com.civfactions.sabre.blocks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;

public abstract class AbstractController {
	
	protected final String name;
	protected final Location l;
	
	/**
	 * Creates a new abstract controller instance
	 * @param name The controller name
	 * @param location The controlled location
	 */
	public AbstractController(String name, Location location) {
		this.name = name;
		this.l = location;
	}
	
	/**
	 * Gets the name of the controller
	 * @return The name of the controller
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Gets the target location
	 * @return The target location
	 */
	public Location getTargetLocation() {
		return this.l;
	}
	
	
	/**
	 * Creates an item stack from the controller instance
	 * @return The new item stack
	 */
	public ItemStack toItemStack() {
		ItemStack is = new ItemStack(Material.STICK, 1);
		
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		im.setLore(this.getLore());
		is.setItemMeta(im);
		return is;
	}
	
	
	
	
	
	public abstract List<String> getLore();
	
	
	protected static String parse(String str) {
		return SabrePlugin.instance().txt().parse(str);
	}
	
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	 
	
	private static Pattern locationPattern = Pattern.compile(parse("<a>Location: <n>(-?\\d+), (-?\\d+), (-?\\d+), (.+)"));
	private static String locationStartString = parse("<a>Location:");
	
	/**
	 * Gets the location from a held controller stick
	 * @param p The player holding the controller
	 * @return The target location if it exists
	 */
	public static Location parseControllerLocation(SabrePlayer p, String name, boolean warn) {
		
		try {
			List<String> lore = checkControllerLore(p, name);
			if (lore == null) {
				if (warn) {
					p.msg(Lang.mustHoldController, name);
				}
				return null;
			}
			
			String locationLore = null;
			
			// Find line that starts with 'Location'
			for (String s : lore) {
				if (s.startsWith(locationStartString)) {
					locationLore = s;
					break;
				}
			}
			
			if (locationLore == null) {
				return null;
			}
			
			Matcher match = locationPattern.matcher(locationLore);
			
			if (match.find()) {
				double x = Double.parseDouble(match.group(1));
				double y = Double.parseDouble(match.group(2));
				double z = Double.parseDouble(match.group(3));
				String worldName = match.group(4);
				
				World world = Bukkit.getWorld(worldName);
				return new Location(world, x, y, z);
			}
			
			return null;
			
		} catch (Exception ex) {
			return null;
		}
	}
	
	
	/**
	 * Verifies correct controller lore
	 * @param p The player holding the item
	 * @param name The controller name
	 * @return The lore if the checks pass
	 */
	public static List<String> checkControllerLore(SabrePlayer p, String name) {
		ItemStack is = p.getPlayer().getInventory().getItemInHand();
		
		if (!is.getType().equals(Material.STICK)) {
			return null;
		}
		
		ItemMeta im = is.getItemMeta();
		if (im == null) {
			return null;
		}
		
		if (!im.hasDisplayName() || !im.hasLore()) {
			return null;
		}
		
		List<String> lore = im.getLore();
		if (lore == null) {
			return null;
		}
		
		if (!ChatColor.stripColor(lore.get(0)).equals(name)) {
			return null;
		}
		
		return lore;
	}
	
	
	/**
	 * Turns a held controller into a normal stick
	 * @param p The player holding the stick
	 */
	public static void normalizeHeldController(SabrePlayer p) {

		PlayerInventory inv = p.getPlayer().getInventory();
		ItemStack is = inv.getItemInHand();
		if (!is.getType().equals(Material.STICK)) {
			return;
		}
		
		is = new ItemStack(Material.STICK, is.getAmount());
		inv.setItemInHand(is);
	}
	
	
	
	public void onPlayerInteract(PlayerInteractEvent e) {
		// Do nothing
	}
}
