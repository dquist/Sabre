package com.gordonfreeman.sabre.warp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.AbstractController;

public class TeleportLinker extends AbstractController {	
		
	public static String name = "Teleport Linker";
	
	public TeleportLinker(TeleportPad teleportPad) {
		super(name, teleportPad.getLocation());
	}

	
	@Override
	public List<String> getLore() {
		
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", name));
		lore.add(parse("<a>Location: <n>%d, %d, %d, %s", l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));
		
		return lore;
	}
	
	
	public static Location parseLocation(SabrePlayer p, boolean warn) {
		return parseControllerLocation(p, name, warn);
	}
}
