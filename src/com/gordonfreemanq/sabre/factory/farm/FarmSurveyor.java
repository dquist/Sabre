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

/**
 * Class to calculate a farm's efficiency factor based
 * on the crop layout of a given location.
 * 
 * Since checking every single block would be time prohibitive,
 * the algorithm checks a random number of blocks within the farm
 * radius each time it does a new survey.
 * 
 * @author GFQ
 *
 */
public class FarmSurveyor {
	
	private static final int MAX_SAMPLE_ATTEMPTS = 100;
	
	// Maximum light level on a block
	private static final double MAX_LIGHT_INTENSITY = 15.0;
	
	// How much solid ground must be under the crop
	private static final int FARM_DEPTH = 5;

	// The calculated coverage factor
	private double coverageFactor;
	
	// The farm chunk radius
	private final int chunkRadius;
	
	// The number of blocks to sample during a survey
	private final int squareLength;
	
	private final Random rand;
	
	// The number of samples to get for each survey
	private final int numSamples;
	
	// The stored samples
	private boolean[] samples;
	
	// The world the farm is located in
	protected World farmWorld;
	
	// The biome the farm is located in
	protected Biome farmBiome;
	
	// The farm material
	protected Material material;
	
	/**
	 * Creates a new FarmSurveyor instance
	 */
	public FarmSurveyor() {
		this.coverageFactor = 0.0;
		this.chunkRadius = SabrePlugin.getPlugin().getSabreConfig().getFarmChunkRadius();
		this.numSamples = SabrePlugin.getPlugin().getSabreConfig().getFarmSurveySampleSize();
		this.squareLength = (chunkRadius * 2 * 16) + 16;
		this.rand = new Random();
		this.samples = new boolean[numSamples];
	}
	
	
	/**
	 * Surveys the farm at a given location and calculates the
	 * efficiency parameter
	 * @param l The location
	 * @return The efficiency factor
	 */
	public double surveyFarm(Location l) {
		if (!l.getChunk().isLoaded()) {
			return coverageFactor;
		}
		
		// Get the corners of the farm
		farmWorld = l.getWorld();
		farmBiome = l.getBlock().getBiome();
		Chunk c = l.getChunk();
		int cornerX = (c.getX() - chunkRadius) * 16;
		int cornerZ = (c.getZ() - chunkRadius) * 16;
		
		int curSample = 0;
		int x = 0;
		int z = 0;
		
		// Attempt to get the amount of farm samples
		for (int i = 0; i < MAX_SAMPLE_ATTEMPTS; i++) {
			x = cornerX + rand.nextInt(squareLength);
			z = cornerZ + rand.nextInt(squareLength);
			
			// Skip over locations that are not loaded
			if (farmWorld.isChunkLoaded(x >> 4, z >> 4)) {
				samples[curSample++] = this.sampleLocation(x, z);
				
				// See if have enough good samples
				if (curSample >= numSamples) {
					break; // Yep!
				}
			}
		}
		
		int passedSamples = 0;
		
		// Calculate the factor
		for (int i = 0; i < curSample; i++) {
			if (samples[i]) {
				passedSamples++;
			}
		}
		
		// Calculate and return the new coverage factor
		this.coverageFactor = passedSamples / curSample;
		return coverageFactor;
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
	public boolean sampleLocation(int x, int z) {
		Block b = findBottomLightBlock(x, z);
		boolean hasLight = blockHasSunlight(b);
		boolean isCrop = b.getType().equals(this.material);
		boolean hasDepth = false;
		
		if (hasLight && isCrop) {
			hasDepth = validateBlockEarth(b, FARM_DEPTH);
		}
		
		return hasLight && isCrop && hasDepth;
	}
	
	
	/**
	 * Gets the coverage factor
	 * @return The coverage factor
	 */
	public double getCoverage() {
		return this.coverageFactor;
	}
	
	
	/**
	 * Sets the coverage factor
	 * @param coverageFactor The coverage factor
	 */
	public void setCoverageFactor(double coverageFactor) {
		this.coverageFactor = coverageFactor;
	}
	
	
	/**
	 * Gets the crop material
	 * @return The crop material
	 */
	public Material getMaterial() {
		return this.material;
	}
	
	
	/**
	 * Sets the crop material
	 * @param material The crop material
	 */
	public void setMaterial(Material material) {
		this.material = material;
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
		int min = 0;
		int max = b.getY();
		int searchY = 0;
		int diff = 0;
		
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
	private boolean validateBlockEarth(Block b, int num) {
		
		for (int i = 0; i < num; i++) {
			b = b.getRelative(BlockFace.DOWN);
			if (b.getType() == Material.AIR) {
				return false;
			}
		}
		
		return true;
	}
	
}
