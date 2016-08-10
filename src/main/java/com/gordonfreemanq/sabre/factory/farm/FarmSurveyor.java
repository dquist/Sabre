package com.gordonfreemanq.sabre.factory.farm;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.util.SabreUtil;

/**
 * Class to calculate a farm's efficiency factor based
 * on the crop layout of a given location.
 * 
 * Since checking every single block would be time prohibitive,
 * the algorithm checks a random number of blocks within the farm
 * area each time it does a new survey.
 * 
 * @author GFQ
 *
 */
public class FarmSurveyor {
	
	private static final int MAX_SAMPLE_ATTEMPTS = 100;
	
	// Maximum light level on a block
	private static final double MAX_LIGHT_INTENSITY = 15.0;
	
	// How much solid ground must be under the crop
	private static final int MAX_SUBSTRATE_DEPTH = 4;

	// The calculated coverage factor
	private double coverageFactor;
	
	// The biome factor for the farm chunks
	private double biomeFactor;
	
	// The fertility factor for the farm chunks
	private double fertilityFactor;
	
	// The farm chunk radius
	// A radius of 1 would result in a 3x3 chunk farm
	// A radius of 2 would result in a 5x5 chunk farm
	private final int chunkRadius;
	
	// The number of blocks to sample during a survey
	private final int squareLength;
	
	// The number of samples to get for each survey
	private final int maxSamples;
	
	// The stored layout samples
	private double surveyLayoutTotal;
	
	// The stored biome samples
	private double surveyBiomeTotal;
	
	// The world the farm is located in
	protected World farmWorld;
	
	// The biome the farm is located in
	protected Biome farmBiome;

	// The crop type
	private final CropType cropType;
	
	// The farm material
	protected final Material plantMaterial;
	
	private final Random rand;
	private Location factoryLocation;
	private int numSamples;
	private int minY;
	private int maxY;
	
	/**
	 * Creates a new FarmSurveyor instance
	 */
	public FarmSurveyor(CropType cropType, Material plantMaterial) {
		this.cropType = cropType;
		this.plantMaterial = plantMaterial;
		this.coverageFactor = 0.0;
		this.biomeFactor = 0.0;
		this.chunkRadius = SabrePlugin.getPlugin().getSabreConfig().getFarmChunkRadius();
		this.maxSamples = SabrePlugin.getPlugin().getSabreConfig().getFarmSurveySampleSize();
		this.squareLength = (chunkRadius * 2 * 16) + 16;
		this.rand = new Random();
	}
	
	
	/**
	 * Surveys the farm at a given location and calculates the
	 * efficiency parameter
	 * @param l The location
	 * @return The efficiency factor
	 */
	public void surveyFarm(FarmFactory f) {
		factoryLocation = f.getLocation();
		
		if (!factoryLocation.getChunk().isLoaded()) {
			return;
		}
		
		minY = factoryLocation.getBlockY() - 20;
		maxY = factoryLocation.getBlockY() + 20;
		
		// Get the corners of the farm
		farmWorld = factoryLocation.getWorld();
		farmBiome = factoryLocation.getBlock().getBiome();
		Chunk c = factoryLocation.getChunk();
		int cornerX = (c.getX() - chunkRadius) * 16;
		int cornerZ = (c.getZ() - chunkRadius) * 16;
		
		calculateFertility();
		
		numSamples = 0;
		surveyLayoutTotal = 0;
		surveyBiomeTotal = 0;
		int x = 0;
		int z = 0;
		
		// Attempt to get the amount of farm samples
		for (int i = 0; i < MAX_SAMPLE_ATTEMPTS; i++) {
			x = cornerX + rand.nextInt(squareLength);
			z = cornerZ + rand.nextInt(squareLength);
			
			// Skip over locations that are not loaded
			if (farmWorld.isChunkLoaded(x >> 4, z >> 4)) {
				this.sampleLocation(x, z);
				
				// See if have enough good samples
				if (numSamples >= maxSamples) {
					break; // Yep!
				}
			}
		}
		
		// Calculate and return the new coverage factor
		this.coverageFactor = surveyLayoutTotal / numSamples;
		this.biomeFactor = surveyBiomeTotal / numSamples;
	}
	
	
	/**
	 * Calculates the farm fertility by averaging all the farm chunks
	 */
	private void calculateFertility() {

		double fertility = 0;
		int count = 0;
		int worldHash = factoryLocation.getWorld().hashCode();
		
		Chunk c = factoryLocation.getChunk();
		int cornerX = c.getX() - chunkRadius;
		int cornerZ = c.getZ() - chunkRadius;
		int chunkLength = (chunkRadius * 2) + 1;
		
		for (int x = 0; x < chunkLength; x++) {
			for (int z = 0; z < chunkLength; z++) {
				fertility += SabreUtil.getChunkFertility(worldHash, cornerX + x, cornerZ + z);
				count++;
			}	
		}
		
		this.fertilityFactor = fertility / count;
	}
	
	
	/**
	 * Samples the given crop location
	 * 
	 * The default pass criteria are follows
	 * 		- the crop must have sunlight, glass roof is fine
	 * 		- there must be 5 solid blocks under the crop
	 * 
	 * @param x The sample x
	 * @param z The sample z
	 * @return true if the sample passed
	 */
	public void sampleLocation(int x, int z) {
		Block b = findBottomLightBlock(x, z);
		Material blockType = b.getType();
		
		// If water, try a relative
		if (b.getType().equals(Material.WATER)) {
			b = b.getRelative(BlockFace.NORTH_WEST);
		}
		
		boolean hasLight = blockHasSunlight(b);
		boolean isCrop = blockType.equals(this.plantMaterial);
		
		if (hasLight && isCrop && isCropMature(b)) {
			double lFactor = sampleSubstrateDepth(b.getRelative(BlockFace.DOWN), MAX_SUBSTRATE_DEPTH);
			surveyLayoutTotal += lFactor;
		}
		double bFactor = cropType.getBiomeFactor(b.getBiome());
		surveyBiomeTotal += bFactor;
		
		numSamples++;
	}
	
	
	/**
	 * Gets the coverage factor
	 * @return The coverage factor
	 */
	public double getCoverageFactor() {
		return this.coverageFactor;
	}
	
	
	/**
	 * Gets the biome factor
	 * @return The biome factor
	 */
	public double getBiomeFactor() {
		return this.biomeFactor;
	}
	
	
	/**
	 * Gets the fertility factor
	 * @return The fertility factor
	 */
	public double getFertilityFactor() {
		return this.fertilityFactor;
	}
	
	
	/**
	 * Gets whether a block has full sunlight
	 * @param block The block to check
	 * @return true if it has full intensity
	 */
	public static boolean blockHasSunlight(Block block) {
		int sunlightIntensity;
		if (block.getType().isTransparent()) {
			sunlightIntensity = block.getLightFromSky();
		} else {
			sunlightIntensity = block.getRelative(BlockFace.UP).getLightFromSky();
		}
		// apply multiplier if the sunlight is not at maximum
		if (sunlightIntensity == MAX_LIGHT_INTENSITY) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Finds the lowest block with full sunlight intensity. This should be the crop
	 * @param x The X coord
	 * @param z the Z coord
	 * @return the block location
	 */
	public Block findBottomLightBlock(int x, int z) {

		Block b = farmWorld.getHighestBlockAt(x, z);
		Material blockType = b.getType();
		if (blockType == Material.AIR) {
			b = b.getRelative(BlockFace.DOWN);
		}
		
		// Some easy checks
		if (b.getY() <= maxY && b.getY() >= minY) {
			if (b.getType() == this.plantMaterial) {
				return b;
			}
			
			if (blockHasSunlight(b) && !blockHasSunlight(b.getRelative(BlockFace.DOWN))) {
				return b;
			}
		}
		
		int min = minY;
		int max = maxY;
		int searchY = 0;
		int diff = max - min;
		
		while(diff > 1) {
			searchY = min + (diff >> 1);
			b = farmWorld.getBlockAt(x, searchY, z);
			if (b.getLightFromSky() == MAX_LIGHT_INTENSITY) {
				max = searchY;
			} else {
				min = searchY;
			}
			diff = max - min;
		}
		
		return b;
	}
	
	
	/**
	 * Validates that there is enough ground under a crop
	 * @param b The block to validate
	 * @return true if there is enough ground under it
	 */
	private double sampleSubstrateDepth(Block b, int num) {
		
		
		Material substrate = this.getSubstrate();
		double factor = 0.2;
		
		for (int i = 0; i < num; i++) {
			b = b.getRelative(BlockFace.DOWN);
			if (b.getType().equals(substrate)) {
				factor += 0.2;
			}
		}
		
		return factor;
	}
	
	
	/**
	 * Checks if a crop is mature
	 * @param b The block to check
	 * @return true if the block is mature
	 */
	@SuppressWarnings("deprecation")
	private boolean isCropMature(Block b) {
		Material m = b.getType();
		
		if (m == Material.CROPS || m == Material.POTATO || m == Material.CARROT) {
			if (b.getData() == 7) {
				return true;
			}
		}
		
		return false;
	}
	
	
	/**
	 * Gets the substrate material
	 * @return
	 */
	protected Material getSubstrate() {
		return Material.CLAY;
	}
	
}
