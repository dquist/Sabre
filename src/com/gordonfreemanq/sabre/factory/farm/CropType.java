package com.gordonfreemanq.sabre.factory.farm;

import org.bukkit.Material;

import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public enum CropType {
	WHEAT,
	CARROT,
	POTATO
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
		case CARROT:
			return CustomItems.getInstance().getByName("Bushel of Carrots");
		case POTATO:
			return CustomItems.getInstance().getByName("Bushel of Potatos");
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
		case CARROT:
			return new FarmSurveyor(Material.CARROT);
		case POTATO:
			return new FarmSurveyor(Material.POTATO);
		default:
			return new FarmSurveyor(Material.CROPS);
		}
	}
}
