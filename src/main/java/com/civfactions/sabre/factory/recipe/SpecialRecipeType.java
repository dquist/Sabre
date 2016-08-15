package com.civfactions.sabre.factory.recipe;

import com.civfactions.sabre.factory.farm.CropType;

/**
 * The types of special recipes
 * @author GFQ
 *
 */
public enum SpecialRecipeType {
	FARM_WHEAT,
	FARM_POTATOES,
	FARM_CARROTS,
	FARM_CACTUS,
	FARM_COCOA,
	FARM_MELONS,
	FARM_PUMPKINS,
	FARM_SUGAR_CANE,
	HARVEST_CROPS,
	STRENGTHEN_PRISON_PEARL,
	CHARGE_MOKSHA_ROD;
	
	
	/**
	 * Creates a special recipe instance from the type
	 * @return
	 */
	public IRecipe createRecipe(String name, int productionRate, int fuelCost) {
		switch (this)
		{
		case FARM_WHEAT:
			return new FarmRecipe(name, CropType.WHEAT, productionRate);
		case FARM_POTATOES:
			return new FarmRecipe(name, CropType.POTATO, productionRate);
		case FARM_CARROTS:
			return new FarmRecipe(name, CropType.CARROT, productionRate);
		case FARM_CACTUS:
			return new FarmRecipe(name, CropType.CACTUS, productionRate);
		case FARM_COCOA:
			return new FarmRecipe(name, CropType.COCOA, productionRate);
		case FARM_MELONS:
			return new FarmRecipe(name, CropType.MELON, productionRate);
		case FARM_PUMPKINS:
			return new FarmRecipe(name, CropType.PUMPKIN, productionRate);
		case FARM_SUGAR_CANE:
			return new FarmRecipe(name, CropType.SUGAR_CANE, productionRate);
		case HARVEST_CROPS:
			return new HarvestRecipe(name, productionRate);
		case STRENGTHEN_PRISON_PEARL:
			return new StrengthenPrisonPearlRecipe(name, productionRate, fuelCost);
		case CHARGE_MOKSHA_ROD:
			return new ChargeMokshaRodRecipe(name, productionRate, fuelCost);
		default:
			return null;
		}
	}
}
