package com.civfactions.sabre.util;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabreConfig;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.SabreBlock;

public class PlayerSpawner {	
	private final PlayerManager pm;
	private final BlockManager bm;
	private final SabreConfig config;
	
	// Track the last spawn location for each player
	private HashMap<SabrePlayer, PlayerSpawnResult> lastRandomSpawn;
	
	// Blocks that should not be spawned on
	private HashSet<Material> nospawnBlocks;

	/**
	 * Creates a new PlayerSpawner instance
	 */
	public PlayerSpawner(PlayerManager pm, BlockManager bm, SabreConfig config) {
		this.pm = pm;
		this.bm = bm;
		this.config = config;
		
		lastRandomSpawn = new HashMap<SabrePlayer, PlayerSpawnResult>();
		
		nospawnBlocks = new HashSet<Material>();
		nospawnBlocks.add(Material.WATER);
		nospawnBlocks.add(Material.STATIONARY_WATER);
		nospawnBlocks.add(Material.LAVA);
		nospawnBlocks.add(Material.STATIONARY_LAVA);
		nospawnBlocks.add(Material.LEAVES);
		nospawnBlocks.add(Material.LEAVES_2);
		nospawnBlocks.add(Material.FIRE);
		nospawnBlocks.add(Material.CACTUS);
		nospawnBlocks.add(Material.VINE);
		nospawnBlocks.add(Material.ACACIA_FENCE_GATE);
		nospawnBlocks.add(Material.BIRCH_FENCE_GATE);
		nospawnBlocks.add(Material.DARK_OAK_FENCE_GATE);
		nospawnBlocks.add(Material.FENCE_GATE);
		nospawnBlocks.add(Material.JUNGLE_FENCE_GATE);
		nospawnBlocks.add(Material.SPRUCE_FENCE_GATE);
	}
	
	
	/**
	 * Bed spawns a player in the default world if their bed exists, otherwise random spawns them
	 * @param p The player to spawn
	 */
	public Location spawnPlayerBed(SabrePlayer sp, World world) {
		Location l = sp.getBedLocation();
		boolean useBed = false;
		
		if (l != null) {
			if (l.getBlock().getType() == Material.BED_BLOCK) {
				SabreBlock b = bm.getBlockAt(SabreUtil.getRealBlock(l.getBlock()).getLocation());
				if (b == null || b.canPlayerAccess(sp)) {
					useBed = true;
				}
			}
		}
		
		if (useBed) {
			sp.getPlayer().setBedSpawnLocation(l, true);
		} else {
			if (l != null) {
				sp.msg(Lang.playerBedMissing);
			}
			
			pm.setBedLocation(sp, null);
			sp.getPlayer().setBedSpawnLocation(null);
			l = spawnPlayerRandom(sp, world);
		}

		lastRandomSpawn.put(sp, new PlayerSpawnResult(l, useBed));
		return l;
	}
	
	
	/**
	 * Bed spawns a player in the default world if their bed exists, otherwise random spawns them
	 * @param p The player to spawn
	 */
	public Location spawnPlayerBed(SabrePlayer sp) {
		return spawnPlayerBed(sp, Bukkit.getServer().getWorld(config.getFreeWorldName()));
	}
	
	
	/**
	 * Random spawns a player in the given world
	 * @param p The player to spawn
	 * @param world The world to use
	 */
	public Location spawnPlayerRandom(SabrePlayer sp, World world) {
		Location spawnLocation = chooseSpawn(world, config.getRespawnRadius());
		SabreUtil.sendToGround(sp.getPlayer(), spawnLocation);
		sp.teleport(spawnLocation);
		lastRandomSpawn.put(sp, new PlayerSpawnResult(spawnLocation, false));
		sp.msg(Lang.playerYouWakeUp);
		return spawnLocation;
	}
	
	
	/**
	 * Random spawns a player in the default world
	 * @param p The player to spawn
	 */
	public Location spawnPlayerRandom(SabrePlayer sp) {
		return spawnPlayerRandom(sp, Bukkit.getServer().getWorld(config.getFreeWorldName()));
	}

	
	/**
	 * Chooses a random spawn location in the given world
	 * @param world The world to spawn in
	 * @param distance The max radius
	 * @return The chosen spawn location
	 */
	private Location chooseSpawn(World world, int distance) {		
		double xmin = -distance;
		double xmax = distance;
		double zmin = -distance;
		double zmax = distance;				
		double xrand = 0;
		double zrand = 0;
		double xcenter = xmin + (xmax - xmin)/2;
		double zcenter = zmin + (zmax - zmin)/2;
		double y = -1;
		
		while (y == -1) {
			double r = Math.random() * (xmax - xcenter);
			double phi = Math.random() * 2 * Math.PI;

			xrand = xcenter + Math.cos(phi) * r;
			zrand = zcenter + Math.sin(phi) * r;

			y = getValidHighestY(world, xrand, zrand);
		}
	
		return new Location(world, xrand, y, zrand);
	}

	
	/**
	 * Get's the highest valid block to spawn on at a x,z coordinate
	 * @param world The world
	 * @param x The X location
	 * @param z The Z location
	 * @return The highest valid Y value or -1 if none found
	 */
	private double getValidHighestY(World world, double x, double z) {
		
		world.getChunkAt(new Location(world, x, 0, z)).load();

		double y = 0;
		Material blockType = Material.AIR;

		if(world.getEnvironment().equals(Environment.NETHER)) {
			Material blockYType = world.getBlockAt((int) x, (int) y, (int) z).getType();
			Material blockY2Type = world.getBlockAt((int) x, (int) (y+1), (int) z).getType();
			while(y < 128 && !(blockYType == Material.AIR && blockY2Type == Material.AIR)) {				
				y++;
				blockYType = blockY2Type;
				blockY2Type = world.getBlockAt((int) x, (int) (y+1), (int) z).getType();
			}
			if(y == 127) {
				return -1;
			}
		} else {
			y = 257;
			while(y >= 0 && blockType == Material.AIR) {
				y--;
				blockType = world.getBlockAt((int) x, (int) y, (int) z).getType();
			}
			if(y == 0) {
				return -1;
			}
		}

		if (!nospawnBlocks.contains(blockType)){
			return y;
		}		

		return -1;
	}
	
	/**
	 * Gets the last spawn location for a given player
	 * @param sp The player
	 * @return The last spawn location
	 */
	public PlayerSpawnResult getLastSpawnLocation(SabrePlayer sp) {
		return lastRandomSpawn.get(sp);
	}
}
