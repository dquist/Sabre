package com.gordonfreemanq.sabre.factory.farm;

import org.bukkit.Location;

/**
 * Surveyor for a crop that needs to see the open sky
 * @author GFQ
 *
 */
public class SkySurveyor extends FarmSurveyor {

	@Override
	public double surveyFarm(Location l) {
		if (!l.getChunk().isLoaded()) {
			return 0;
		}
		
		
		
		return 0;
	}

}
