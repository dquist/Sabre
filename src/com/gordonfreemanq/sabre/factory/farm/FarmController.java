package com.gordonfreemanq.sabre.factory.farm;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.AbstractController;
import com.gordonfreemanq.sabre.cmd.factory.CmdFactory;
import com.gordonfreemanq.sabre.factory.recipe.FarmRecipe;
import com.gordonfreemanq.sabre.factory.recipe.IRecipe;

public class FarmController extends AbstractController {	
		
	public static String name = "Factory Controller";
	
	private final FarmFactory farm;
	
	public FarmController(FarmFactory factory) {
		super(name, factory.getLocation());
		
		this.farm = factory;
	}

	
	@Override
	public List<String> getLore() {
		
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", name));
		lore.add(parse("<a>Type: <n>%s", farm.getTypeName()));
		lore.add(parse("<a>Location: <n>%d, %d, %d, %s", l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getWorld().getName()));

		Location input = farm.getInputLocation();
		if (input != null) {
			lore.add(parse("<a>Input: <n>%d, %d, %d", input.getBlockX(), input.getBlockY(), input.getBlockZ()));
		} else {
			lore.add(parse("<a>Input: <n><it>Not Set"));
		}
		
		Location output = farm.getOutputLocation();
		if (output != null) {
			lore.add(parse("<a>Output: <n>%d, %d, %d", output.getBlockX(), output.getBlockY(), output.getBlockZ()));
		} else {
			lore.add(parse("<a>Output: <n><it>Not Set"));
		}
		
		Location fuel = farm.getFuelLocation();
		if (fuel != null) {
			lore.add(parse("<a>Fuel: <n>%d, %d, %d", fuel.getBlockX(), fuel.getBlockY(), fuel.getBlockZ()));
		} else {
			lore.add(parse("<a>Fuel: <n><it>Not Set"));
		}
		
		
		IRecipe recipe = farm.getRecipe();
		
		if (recipe instanceof FarmRecipe) {
			FarmRecipe fr = (FarmRecipe)recipe;
			lore.add(parse("<a>Farm Properties: <n>%s", fr.getCrop().toString()));
			lore.add(parse("    <a>Proximity: <n>%s%%", farm.getProximityFactorPercent().toString()));
			lore.add(parse("    <a>Fertility: <n>%s%%", farm.getFertilityFactorPercent().toString()));
			lore.add(parse("    <a>Layout: <n>%s%%", farm.getLayoutFactorPercent().toString()));
			lore.add(parse("    <a>Output rate: <lime>%d/%d", farm.getRealOutput(), farm.getNominalOutput()));
			lore.add(parse("    <a>Ready to Harvest: <n>%d", farm.getFarmedCrops().get(fr.getCrop())));
		}
		
		lore.add(parse(""));
		lore.add(parse("<l>Commands:"));
		lore.add(parse(CmdFactory.getInstance().cmdConfigure.getUseageTemplate(true)));
		lore.add(parse(CmdFactory.getInstance().cmdUpgrade.getUseageTemplate(true)));
		
		return lore;
	}
	
	
	public static Location parseLocation(SabrePlayer p, boolean warn) {
		return parseControllerLocation(p, name, warn);
	}
}
