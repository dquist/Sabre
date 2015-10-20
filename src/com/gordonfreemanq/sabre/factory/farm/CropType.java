package com.gordonfreemanq.sabre.factory.farm;

import org.bukkit.Material;

import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public enum CropType {
	WHEAT,
	POTATO,
	CARROT,
	CACTUS,
	COCOA,
	MELON,
	PUMPKIN,
	SUGAR_CANE
	;
	
	
	/**
	 * Creates a crop item for the crop type
	 * @return The crop item stack
	 */
	public SabreItemStack createCropItem() {
		switch(this)
		{
		case WHEAT:
			return CustomItems.getInstance().getByName("Bushel of Wheat");
		case POTATO:
			return CustomItems.getInstance().getByName("Bushel of Potatoes");
		case CARROT:
			return CustomItems.getInstance().getByName("Bushel of Carrots");
		case CACTUS:
			return CustomItems.getInstance().getByName("Bushel of Cactus");
		case COCOA:
			return CustomItems.getInstance().getByName("Bushel of Cocoa");
		case MELON:
			return CustomItems.getInstance().getByName("Bushel of Melons");
		case PUMPKIN:
			return CustomItems.getInstance().getByName("Bushel of Pumpkins");
		case SUGAR_CANE:
			return CustomItems.getInstance().getByName("Bushel of Sugar Cane");
		default:
			return null;
		}
	}
	
	
	/**
	 * Creates a surveyor instance from the crop type
	 * @return The surveyor instance
	 */
	public FarmSurveyor createSurveyor() {
		switch(this)
		{
		case WHEAT:
			return new FarmSurveyor(Material.CROPS);
		case POTATO:
			return new FarmSurveyor(Material.POTATO);
		case CARROT:
			return new FarmSurveyor(Material.CARROT);
		case CACTUS:
			return new FarmSurveyor(Material.CACTUS);
		case COCOA:
			return new FarmSurveyor(Material.COCOA);
		case MELON:
			return new FarmSurveyor(Material.MELON_BLOCK);
		case PUMPKIN:
			return new FarmSurveyor(Material.PUMPKIN);
		case SUGAR_CANE:
			return new FarmSurveyor(Material.SUGAR_CANE_BLOCK);
		default:
			return new FarmSurveyor(Material.CROPS);
		}
	}
}
