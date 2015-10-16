package com.gordonfreemanq.sabre.factory.farm;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;

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
	 * @param w The sample world
	 * @param x The sample x
	 * @param z The sample z
	 * @return Whether it passed or failed
	 */
	public boolean sampleLocation(int x, int z) {
		return true;
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
	
	
	
}
