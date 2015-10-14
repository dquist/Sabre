package com.gordonfreemanq.sabre.factory.recipe;

/**
 * The types of special recipes
 * @author GFQ
 *
 */
public enum SpecialRecipeType {
	STRENGTHEN_PRISON_PEARL,
	CHARGE_MOKSHA_ROD;
	
	
	/**
	 * Gets a special recipe instance from the type
	 * @return
	 */
	public IRecipe getRecipeInstance(String name, int productionSpeed, int fuelCost) {
		switch (this)
		{
		case STRENGTHEN_PRISON_PEARL:
			return new StrengthenPrisonPearlRecipe(name, productionSpeed, fuelCost);
		default:
			return null;
		}
	}
}
