package com.civfactions.sabre.test;

import org.junit.*;

public class SabreUtilTest {

	@Ignore
	@Test
	public void testGetChunkFertility() {
		

		System.out.print("Testing chunk fertility values\n");
		for (int i = -1200; i < 1200; i += 5) {

			int cornerX = 15;
			int cornerZ = i;
			int width = 5;
			double fertility = 0;
			int count = 0;
			double total = 0.0;
			
			for (int x = cornerX; x < cornerX + width; x++) {
				for (int z = cornerZ; z < cornerZ + width; z++) {
					fertility = getChunkFertility(0, x, z);
					//System.out.printf("Chunk %4d,%4d: %1.2f\n", x, z, fertility);
					
					total += fertility;
					count++;
				}
			}
			
			System.out.printf("Average fertility: %1.2f\n", total / count);
		}
		
		//fail("Not yet implemented");
	}
	
	
	/**
	 * Gets a pseuso-random fertility number for a given chunk
	 * @return
	 */
	public static double getChunkFertility(int worldHash, int x, int z) {
	
		byte hash = 3;
		hash = (byte)(73 * hash + worldHash);
		hash = (byte)(hash + x * 379);
		hash = (byte)(hash + z * 571);
		hash = (byte)(hash & 0x7F); // make 'unsigned'
		hash = (byte)((hash * 100) / 128); // scale 0 - 100
		
		double hashFactor = getChunkHashFactor(hash);
		int dist = (int)Math.sqrt(x * x + z * z);
		double distFactor = getChunkDistanceFactor(dist);
		return (hashFactor + distFactor) / 2;
	}
	
	private static double getChunkHashFactor(byte hash) {
		
		if (hash < 2) {
			return 15;
		} else if (hash < 5 ) {
			return 8;
		} else if (hash < 10) {
			return 5;
		} else if (hash < 20) {
			return 2;
		} else if (hash < 60) {
			return 1;
		} else if (hash < 90) {
			return 0.2;
		} else if (hash < 95) {
			return 0.05;
		} else {
			return 0.01;
		}
	}
	
	private static double getChunkDistanceFactor(int num) {
		num = Math.abs(num);
		
		if (num < 75) {
			return 1.5;
		} else if (num < 150) {
			return 1.3;
		} else if (num < 300) {
			return 1.1;
		} else if (num < 500) {
			return 1.0;
		} else if (num < 700) {
			return 0.9;
		} else if (num < 800) {
			return 0.8;
		} else if (num < 900) {
			return 0.6;
		} else if (num < 1000) {
			return 0.5;
		} else {
			return 0.2;
		}
	}
}
