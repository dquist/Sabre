package com.gordonfreemanq.sabre.factory.farm;

import org.bukkit.Material;
import org.bukkit.block.Biome;

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
			return new FarmSurveyor(WHEAT, Material.CROPS);
		case POTATO:
			return new FarmSurveyor(POTATO, Material.POTATO);
		case CARROT:
			return new FarmSurveyor(CARROT, Material.CARROT);
		case CACTUS:
			return new FarmSurveyor(CACTUS, Material.CACTUS);
		case COCOA:
			return new FarmSurveyor(COCOA, Material.COCOA);
		case MELON:
			return new FarmSurveyor(MELON, Material.MELON_BLOCK);
		case PUMPKIN:
			return new FarmSurveyor(PUMPKIN, Material.PUMPKIN);
		case SUGAR_CANE:
			return new FarmSurveyor(SUGAR_CANE, Material.SUGAR_CANE_BLOCK);
		default:
			return new FarmSurveyor(WHEAT, Material.CROPS);
		}
	}
	
	
	/**
	 * Gets the crop efficiency for a given biome
	 * @param b The biome to check
	 * @return the biome factor
	 */
	public double getBiomeFactor(Biome b) {
		
		BiomeClimate climate = BiomeClimate.getClimate(b);
		
		// Mushroom 1.0 for everything
		if (climate == BiomeClimate.MUSHROOM) {
			return 1.0;
		}
		
		double factor = 0.0;
		
		switch (this) {
		
		case WHEAT:
			factor = getWheatBiomeFactor(climate);
			break;
		case POTATO:
			factor = getPotatoBiomeFactor(climate);
			break;
		case CARROT:
			factor = getCarrotBiomeFactor(climate);
			break;
		case CACTUS:
			factor = getCactusBiomeFactor(climate);
			break;
		case COCOA:
			factor = getCocoaBiomeFactor(climate);
			break;
		case MELON:
			factor = getMelonBiomeFactor(climate);
			break; 
		case PUMPKIN:
			factor = getPumpkinBiomeFactor(climate);
			break;
		case SUGAR_CANE:
			factor = getSugarCaneBiomeFactor(climate);
			break;
		default:
			factor = 0.0;
		}
		
		factor = Math.min(factor, 1);
		factor = Math.max(factor, 0);
		return factor;
	}
	
	
	private double getWheatBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 1.0;
		case TEMPERATE:
			return 0.8;
		case JUNGLE:
			return 0.7;
		case COLD:
			return 0.4;
		case ARID:
			return 0.7;
		default:
			return 0;
		}
	}
	
	private double getPotatoBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 0.8;
		case TEMPERATE:
			return 1.0;
		case JUNGLE:
			return 0.4;
		case COLD:
			return 0.6;
		case ARID:
			return 0.7;
		default:
			return 0;
		}
	}
	
	private double getCarrotBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 1.0;
		case TEMPERATE:
			return 0.6;
		case JUNGLE:
			return 1.0;
		case COLD:
			return 0.6;
		case ARID:
			return 0.6;
		default:
			return 0;
		}
	}
	
	private double getCactusBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case ARID:
		case DRY:
			return 1.0;
		case TEMPERATE:
			return 0.4;
		default:
			return 0;
		}
	}
	
	private double getCocoaBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case JUNGLE:
			return 1.0;
		case TEMPERATE:
			return 0.4;
		default:
			return 0;
		}
	}
	
	private double getMelonBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 0.8;
		case TEMPERATE:
			return 0.6;
		case JUNGLE:
			return 1.0;
		default:
			return 0;
		}
	}
	
	private double getPumpkinBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 0.6;
		case TEMPERATE:
			return 0.8;
		case COLD:
		case SNOWY:
			return 1.0;
		default:
			return 0;
		}
	}
	
	private double getSugarCaneBiomeFactor(BiomeClimate climate) {
		switch (climate) {
		case PRAIRIE:
			return 0.7;
		case TEMPERATE:
			return 0.4;
		case JUNGLE:
			return 1.0;
		default:
			return 0;
		}
	}
}
