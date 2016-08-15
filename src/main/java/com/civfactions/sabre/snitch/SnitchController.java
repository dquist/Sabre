package com.civfactions.sabre.snitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.snitch.CmdSnitch;

public class SnitchController extends AbstractController {	
		
	public static String name = "Snitch Controller";
	
	private final Snitch snitch;
	
	public SnitchController(Snitch snitch) {
		super(name, snitch.getLocation());
		
		this.snitch = snitch;
	}

	
	@Override
	public List<String> getLore() {
		
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", name));
		lore.add(parse("<a>Location: <n>%d, %d, %d, %s", l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));
		lore.add(parse("<a>Name: <n>%s", snitch.getSnitchName()));
		lore.add(parse("<a>Group: <n>%s", snitch.getGroupName()));
		lore.add(parse("<a>Placed by: <n>%s", snitch.getPlacedByName()));
		lore.add(parse("<a>Placed on: <n>%s", new SimpleDateFormat("yyyy-MM-dd").format(snitch.getPlacedOn())));
		lore.add(parse("<a>Notifications: <n>%s", snitch.getNotifyString()));
		lore.add(parse(""));
		lore.add(parse("<l>Commands:"));
		lore.add(parse(CmdSnitch.getInstance().cmdInfo.getUsageTemplate(true)));
		lore.add(parse(CmdSnitch.getInstance().cmdClear.getUsageTemplate(true)));
		lore.add(parse(CmdSnitch.getInstance().cmdNotify.getUsageTemplate(true)));
		lore.add(parse(CmdSnitch.getInstance().cmdRefresh.getUsageTemplate(true)));
		lore.add(parse(CmdSnitch.getInstance().cmdRename.getUsageTemplate(true)));
		
		return lore;
	}
	
	
	public static Location parseLocation(SabrePlayer p, boolean warn) {
		return parseControllerLocation(p, name, warn);
	}
}
