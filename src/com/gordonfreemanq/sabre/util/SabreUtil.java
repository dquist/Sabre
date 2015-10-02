package com.gordonfreemanq.sabre.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.util.Vector;

import com.gordonfreemanq.sabre.Lang;
import com.mongodb.BasicDBObject;

@SuppressWarnings("deprecation")
public class SabreUtil {

	public static String getExceptionMessage(String method, Exception base) {
		return String.format("%s: %s", String.format(Lang.exceptionConsoleDuring, method), base.toString());
	}

	

    
    public static Block getAttachedChest(Block block) {
        Material mat = block.getType();
        if (mat == Material.CHEST || mat == Material.TRAPPED_CHEST) {
            for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
                Block b = block.getRelative(face);
                if (b.getType() == mat) {
                    return b;
                }
            }
        }
        return block;
    }
    
    
    public static Block getAttachedPistons(Block block) {
        for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
            Block b = block.getRelative(face);
    		BlockState state = b.getState();
    		MaterialData data = state.getData();
    		
    		if (data instanceof PistonBaseMaterial) {
    			return b;
    		}
    		else if (data instanceof PistonExtensionMaterial) {
    			return getAttachedPistons(b);
    		}
        }
        return null;
    }
    
	
	/**
     * Returns the correct block for multi-block items, 
     * example: for beds, doors we want the bottom half.
     * @param block The block to check
     * @return Returns the block we want.
     */
    public static Block getRealBlock(Block block){
    	Block b = block;
    	Block piston;
    	switch (block.getType()){
    	case CHEST:
    	case TRAPPED_CHEST:
    		b = getAttachedChest(b);
    		break;
		case WOODEN_DOOR:
		case IRON_DOOR_BLOCK:
			if (((Door) block.getState().getData()).isTopHalf())
				b = block.getRelative(BlockFace.DOWN);
			break;
		case BED_BLOCK:
			if (((Bed) block.getState().getData()).isHeadOfBed())
				b = block.getRelative(((Bed) block.getState().getData()).getFacing().getOppositeFace());
			break;
			
		default:
			piston = getAttachedPistons(block);
			if (piston != null) {
				return piston;
			}
			
			break;
		}
    	
    	
    	
    	
    	
    	
    	return b;
    }
    

    /**
     * Checks if the material is a reail
     * @param mat The material to check
     * @return true if it is a rail
     */
    public static boolean isRail(Material mat) {
        return mat.equals(Material.RAILS)
            || mat.equals(Material.POWERED_RAIL)
            || mat.equals(Material.ACTIVATOR_RAIL)
            || mat.equals(Material.DETECTOR_RAIL);
    }
    
    
    private static boolean isSoilPlant(Material mat) {
        return mat.equals(Material.WHEAT)
            || mat.equals(Material.MELON_STEM)
            || mat.equals(Material.PUMPKIN_STEM)
            || mat.equals(Material.CARROT)
            || mat.equals(Material.POTATO)
            || mat.equals(Material.CROPS)
            || mat.equals(Material.MELON_BLOCK)
            || mat.equals(Material.PUMPKIN);
    }

    private static boolean isDirtPlant(Material mat) {
        return mat.equals(Material.SUGAR_CANE_BLOCK)
            || mat.equals(Material.MELON_BLOCK)
            || mat.equals(Material.PUMPKIN);
    }

    private static boolean isGrassPlant(Material mat) {
        return mat.equals(Material.SUGAR_CANE_BLOCK)
            || mat.equals(Material.MELON_BLOCK)
            || mat.equals(Material.PUMPKIN);
    }

    private static boolean isSandPlant(Material mat) {
        return mat.equals(Material.CACTUS)
            || mat.equals(Material.SUGAR_CANE_BLOCK);
    }

    private static boolean isSoulSandPlant(Material mat) {
        return mat.equals(Material.NETHER_WARTS);
    }

    public static boolean isPlant(Block plant) {
        return isPlant(plant.getType());
    }

    /**
     * Checks if the material is a plant
     * @param mat the material to check
     * @return true if it is a plant
     */
    public static boolean isPlant(Material mat) {
        return isSoilPlant(mat)
            || isDirtPlant(mat)
            || isGrassPlant(mat)
            || isSandPlant(mat)
            || isSoulSandPlant(mat);
    }
    
    
    public static boolean isWebString(Material mat) {
    	return mat.equals(Material.WEB) 
    			|| mat.equals(Material.STRING) 
    			|| mat.equals(Material.REDSTONE_WIRE);
    }
    
    



	public static boolean tryToTeleport(Player player, Location location) {
		Location loc = location.clone();
		loc.setX(Math.floor(loc.getX()) + 0.500000D);
		loc.setY(Math.floor(loc.getY()) + 0.02D);
		loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
		final Location baseLoc = loc.clone();
		final World world = baseLoc.getWorld();
		
		// Check if teleportation here is viable
		boolean performTeleport = checkForTeleportSpace(loc);
		if (!performTeleport) {
			loc.setY(loc.getY() + 1.000000D);
			performTeleport = checkForTeleportSpace(loc);
		}
		if (performTeleport) {
			player.setVelocity(new Vector());
			player.teleport(loc);
			return true;
		}
		loc = baseLoc.clone();
		// Create a sliding window of block types and track how many of those
		//  are solid. Keep fetching the block below the current block to move down.
		int air_count = 0;
		LinkedList<Material> air_window = new LinkedList<Material>();
		loc.setY((float)world.getMaxHeight() - 2);
		Block block = world.getBlockAt(loc);
		for (int i = 0; i < 4; ++i) {
			Material block_mat = block.getType();
			if (!block_mat.isSolid()) {
				++air_count;
			}
			air_window.addLast(block_mat);
			block = block.getRelative(BlockFace.DOWN);
		}
		// Now that the window is prepared, scan down the Y-axis.
		while (block.getY() >= 1) {
			Material block_mat = block.getType();
			if (block_mat.isSolid()) {
				if (air_count == 4) {
					player.setVelocity(new Vector());
					loc = block.getLocation();
					loc.setX(Math.floor(loc.getX()) + 0.500000D);
					loc.setY(loc.getY() + 1.02D);
					loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
					player.teleport(loc);
					return true;
				}
			} else { // !block_mat.isSolid()
				++air_count;
			}
			air_window.addLast(block_mat);
			if (!air_window.removeFirst().isSolid()) {
				--air_count;
			}
			block = block.getRelative(BlockFace.DOWN);
		}
		return false;
	}

	public static boolean checkForTeleportSpace(Location loc) {
		final Block block = loc.getBlock();
		final Material mat = block.getType();
		if (mat.isSolid()) {
			return false;
		}
		final Block above = block.getRelative(BlockFace.UP);
		if (above.getType().isSolid()) {
			return false;
		}
		return true;
	}
	
	
	public static BasicDBObject serializeLocation(Location l)
	{
		BasicDBObject doc = new BasicDBObject("world", l.getWorld().getName())
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ());
		
		return doc;
	}
    
	
	public static Location deserializeLocation(Object o)
	{
		BasicDBObject doc = (BasicDBObject)o;
		String worldName = doc.getString("world");
		int x = doc.getInt("x");
		int y = doc.getInt("y");
		int z = doc.getInt("z");
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}
	

	
	
	/**
	 * Fuzzes a location
	 * @param loc The location to fuzz
	 * @return The fuzzed location
	 */
	public static Location fuzzLocation(Location loc) {
		if (loc == null)
			return null;

		double rad = Math.random()*Math.PI*2;
		Location newloc = loc.clone();
		newloc.add(1.2*Math.cos(rad), 1.2*Math.sin(rad), 0);
		return newloc;
	}
	
	
	// *------------------------------------------------------------------------------------------------------------*
	// | The following chooseSpawn method contains code made by NuclearW                                            |
	// | based on his SpawnArea plugin:                                                                             |
	// | http://forums.bukkit.org/threads/tp-spawnarea-v0-1-spawns-targetPlayers-in-a-set-area-randomly-1060.20408/ |
	// *------------------------------------------------------------------------------------------------------------*

	public static Location chooseSpawn(World world, int distance) {

		String worldName = world.getName();			

		List<Integer> blacklist = Arrays.asList(new Integer[]{8,9,10,11,18,51,81});	
		
		double xmin = -distance;
		double xmax = distance;
		double zmin = -distance;
		double zmax = distance;
				
		// Spawn area thickness near border. If 0 spawns whole area
		int thickness = 0;

		String type = "circle";
				
		double xrand = 0;
		double zrand = 0;
		double y = -1;
		
		if(type.equalsIgnoreCase("circle")){

			double xcenter = xmin + (xmax - xmin)/2;
			double zcenter = zmin + (zmax - zmin)/2;
			
			do {

				double r = Math.random() * (xmax - xcenter);
				double phi = Math.random() * 2 * Math.PI;

				xrand = xcenter + Math.cos(phi) * r;
				zrand = zcenter + Math.sin(phi) * r;

				y = getValidHighestY(world, xrand, zrand, blacklist);
								
			} while (y == -1);


		} else {

			if(thickness <= 0){

				do {
					
					xrand = xmin + Math.random()*(xmax - xmin + 1);
					zrand = zmin + Math.random()*(zmax - zmin + 1);

					y = getValidHighestY(world, xrand, zrand, blacklist);

				} while (y == -1);

			}else {

				do {
					
					int side = (int) (Math.random() * 4d);
					double borderOffset = Math.random() * (double) thickness;
					if (side == 0) {
						xrand = xmin + borderOffset;
						// Also balancing probability considering thickness
						zrand = zmin + Math.random() * (zmax - zmin + 1 - 2*thickness) + thickness;
					}
					else if (side == 1) {
						xrand = xmax - borderOffset;
						zrand = zmin + Math.random() * (zmax - zmin + 1 - 2*thickness) + thickness;
					}
					else if (side == 2) {
						xrand = xmin + Math.random() * (xmax - xmin + 1);
						zrand = zmin + borderOffset;
					}
					else {
						xrand = xmin + Math.random() * (xmax - xmin + 1);
						zrand = zmax - borderOffset;
					}

					y = getValidHighestY(world, xrand, zrand, blacklist);

				} while (y == -1);
				
			}
		}
	
		Location location = new Location(world, xrand, y, zrand);
				
		return location;
	}

	private static double getValidHighestY(World world, double x, double z, List<Integer> blacklist) {
		
		world.getChunkAt(new Location(world, x, 0, z)).load();

		double y = 0;
		int blockid = 0;

		if(world.getEnvironment().equals(Environment.NETHER)){
			int blockYid = world.getBlockTypeIdAt((int) x, (int) y, (int) z);
			int blockY2id = world.getBlockTypeIdAt((int) x, (int) (y+1), (int) z);
			while(y < 128 && !(blockYid == 0 && blockY2id == 0)){				
				y++;
				blockYid = blockY2id;
				blockY2id = world.getBlockTypeIdAt((int) x, (int) (y+1), (int) z);
			}
			if(y == 127) return -1;
		}else{
			y = 257;
			while(y >= 0 && blockid == 0){
				y--;
				blockid = world.getBlockTypeIdAt((int) x, (int) y, (int) z);
			}
			if(y == 0) return -1;
		}

		if (blacklist.contains(blockid)) return -1;
		if (blacklist.contains(81) && world.getBlockTypeIdAt((int) x, (int) (y+1), (int) z) == 81) return -1; // Check for cacti

		return y;
	}

	// Methods for a save landing :)

	public static void sendGround(Player player, Location location){

		location.getChunk().load();

		World world = location.getWorld();

		for(int y = 0 ; y <= location.getBlockY() + 2; y++){
			Block block = world.getBlockAt(location.getBlockX(), y, location.getBlockZ());
			player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
		}

	}
}
