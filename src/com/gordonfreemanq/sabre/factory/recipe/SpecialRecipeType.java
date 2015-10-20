package com.gordonfreemanq.sabre.factory.recipe;

import com.gordonfreemanq.sabre.factory.farm.CropType;

/**
 * The types of special recipes
 * @author GFQ
 *
 */
public enum SpecialRecipeType {
	FARM_WHEAT,
	FARM_CARROTS,
	FARM_POTATOES,
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
		case FARM_CARROTS:
			return new FarmRecipe(name, CropType.CARROT, productionRate);
		case FARM_POTATOES:
			return new FarmRecipe(name, CropType.POTATO, productionRate);
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
