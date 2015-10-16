package com.gordonfreemanq.sabre.factory;

import com.gordonfreemanq.sabre.blocks.CustomItems;
import com.gordonfreemanq.sabre.blocks.SabreItemStack;

public enum CropType {
	WHEAT,
	CARROT,
	POTATO
	;
	
	
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
}
