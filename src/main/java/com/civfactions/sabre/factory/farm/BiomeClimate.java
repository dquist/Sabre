package com.civfactions.sabre.factory.farm;

import org.bukkit.block.Biome;

public enum BiomeClimate {

	NONE,
	PRAIRIE,
	TEMPERATE,
	JUNGLE,
	MUSHROOM,
	ARID,
	DRY,
	COLD,
	SNOWY
	;
	
	
	/**
	 * Gets the climate for a particular biome
	 * @param b The biome to check
	 * @return The matching climate
	 */
	public static BiomeClimate getClimate(Biome b) {
		
		
		switch (b) {
		
		case PLAINS:
		case SUNFLOWER_PLAINS:
			return PRAIRIE;
			
		case FOREST:
		case FLOWER_FOREST:
		case BIRCH_FOREST:
		case BIRCH_FOREST_HILLS:
		case BIRCH_FOREST_HILLS_MOUNTAINS:
		case BIRCH_FOREST_MOUNTAINS:
		case ROOFED_FOREST:
		case ROOFED_FOREST_MOUNTAINS:
			return TEMPERATE;
			
		case JUNGLE:
		case JUNGLE_EDGE:
		case JUNGLE_EDGE_MOUNTAINS:
		case JUNGLE_HILLS:
		case JUNGLE_MOUNTAINS:
		case SWAMPLAND:
		case SWAMPLAND_MOUNTAINS:
			return JUNGLE;
			
		case MUSHROOM_ISLAND:
		case MUSHROOM_SHORE:
			return MUSHROOM;
			
		case SAVANNA:
		case SAVANNA_MOUNTAINS:
		case SAVANNA_PLATEAU:
		case SAVANNA_PLATEAU_MOUNTAINS:
			return ARID;
			
		case DESERT:
		case DESERT_HILLS:
		case DESERT_MOUNTAINS:
		case MESA:
		case MESA_BRYCE:
		case MESA_PLATEAU:
		case MESA_PLATEAU_FOREST:
		case MESA_PLATEAU_FOREST_MOUNTAINS:
		case MESA_PLATEAU_MOUNTAINS:
			return DRY;
		
		
		case EXTREME_HILLS:
		case EXTREME_HILLS_MOUNTAINS:
		case EXTREME_HILLS_PLUS:
		case EXTREME_HILLS_PLUS_MOUNTAINS:
		case TAIGA:
		case TAIGA_HILLS:
		case TAIGA_MOUNTAINS:
		case MEGA_SPRUCE_TAIGA:
		case MEGA_SPRUCE_TAIGA_HILLS:
		case MEGA_TAIGA:
		case MEGA_TAIGA_HILLS:
			return COLD;
		
		case ICE_PLAINS:
		case ICE_PLAINS_SPIKES:
		case ICE_MOUNTAINS:
		case COLD_TAIGA:
		case COLD_TAIGA_HILLS:
		case COLD_TAIGA_MOUNTAINS:
			return SNOWY;
		
		case FROZEN_RIVER:
		case FROZEN_OCEAN:
		case BEACH:
		case COLD_BEACH:
		case STONE_BEACH:
		case OCEAN:
		case DEEP_OCEAN:
		case SKY:
		case HELL:
		default:
			return NONE;
		}
	}
}
