package com.civfactions.sabre.factory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.blocks.AbstractController;
import com.civfactions.sabre.cmd.factory.CmdFactory;

public class FactoryController extends AbstractController {	
		
	public static String name = "Factory Controller";
	
	private final BaseFactory factory;
	
	public FactoryController(BaseFactory factory) {
		super(name, factory.getLocation());
		
		this.factory = factory;
	}

	
	@Override
	public List<String> getLore() {
		
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", name));
		lore.add(parse("<a>Type: <n>%s", factory.getTypeName()));
		lore.add(parse("<a>Location: <n>%d, %d, %d, %s", l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));

		Location input = factory.getInputLocation();
		if (input != null) {
			lore.add(parse("<a>Input: <n>%d, %d, %d", input.getBlockX(), input.getBlockY(), input.getBlockZ()));
		} else {
			lore.add(parse("<a>Input: <n><it>Not Set"));
		}
		
		Location output = factory.getOutputLocation();
		if (output != null) {
			lore.add(parse("<a>Output: <n>%d, %d, %d", output.getBlockX(), output.getBlockY(), output.getBlockZ()));
		} else {
			lore.add(parse("<a>Output: <n><it>Not Set"));
		}
		
		Location fuel = factory.getFuelLocation();
		if (fuel != null) {
			lore.add(parse("<a>Fuel: <n>%d, %d, %d", fuel.getBlockX(), fuel.getBlockY(), fuel.getBlockZ()));
		} else {
			lore.add(parse("<a>Fuel: <n><it>Not Set"));
		}
		
		lore.add(parse(""));
		lore.add(parse("<l>Commands:"));
		lore.add(parse(CmdFactory.getInstance().cmdConfigure.getUsageTemplate(true)));
		lore.add(parse(CmdFactory.getInstance().cmdUpgrade.getUsageTemplate(true)));
		
		return lore;
	}
	
	
	public static Location parseLocation(IPlayer p, boolean warn) {
		return parseControllerLocation(p, name, warn);
	}
}
