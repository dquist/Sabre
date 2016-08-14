package com.gordonfreemanq.sabre.util;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Bed;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.util.Vector;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlugin;
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

	/**
	 * Sends a player to the ground at a specified location
	 * @param player The player to move
	 * @param location Their location to move to
	 * @return The final landing location
	 */
	public static Location sendToGround(Player player, Location location){

		location.getChunk().load();
		Block block = location.getBlock();

		World world = location.getWorld();

		for(int y = 0 ; y <= location.getBlockY() + 2; y++){
			block = world.getBlockAt(location.getBlockX(), y, location.getBlockZ());
			player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
		}
		
		return block.getLocation();
	}

	private static String uuidStartString = parse("<a>UUID: <n>");
	private static Pattern uuidPattern = Pattern.compile(parse(uuidStartString + "(.+)"));
	//private static Pattern keyValuePattern = Pattern.compile(parse(".+: (.+)"));
	
	/**
	 * Finds the UUID value in a lore list in the form of
	 * UUID: <UUID-value>
	 * @param lore The lore to search
	 * @return The UUID if it was found
	 */
	public static UUID parseLoreId(List<String> lore) {
		
		String value = parseLoreString(lore, uuidStartString, uuidPattern);
		if (value != null) {
			return UUID.fromString(value);
		}
		
		return null;
	}
	
	/**
	 * Parses a lore int
	 * @param lore The lore to parse
	 * @param key A key value
	 * @return The parsed value
	 */
	public static int parseLoreInt(List<String> lore, String startString, Pattern pattern) {
		return Integer.parseInt(parseLoreString(lore, startString, pattern));
	}
	
	
	/**
	 * Parses a generic lore value
	 * @param lore The lore to parse
	 * @param key A key value
	 * @return The parsed value
	 */
	public static String parseLoreString(List<String> lore, String startString, Pattern pattern) {
		if (lore == null) {
			return null;
		}
		
		for (String s : lore) {
			if (s.startsWith(startString)) {
				Matcher match = pattern.matcher(s);
				
				if (match.find()) {
					return match.group(1);
				}				
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if the lore contains a specific string 
	 * @param lore The lore to check
	 * @param field The string to match
	 * @return true if it is contained
	 */
	public static boolean itemContainsLoreString(ItemStack is, String match) {
		ItemMeta im = is.getItemMeta();
		if (im == null) {
			return false;
		}
		
		return loreContainsString(im.getLore(), match);
	}
	
	/**
	 * Checks if the lore contains a specific string 
	 * @param lore The lore to check
	 * @param field The string to match
	 * @return true if it is contained
	 */
	public static boolean loreContainsString(List<String> lore, String match) {
		if (lore == null) {
			return false;
		}
		
		for (String s : lore) {
			if (s.contains(match)) {
				return true;
			}
		}
		return false;
	}
	
	
	protected static String parse(String str) {
		return SabrePlugin.instance().txt().parse(str);
	}
	
	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
	
	
	/**
	 * Gets a pseuso-random fertility number for a given chunk
	 * @return
	 */
	public static double getChunkFertility(Location l) {
	
		int worldHash = l.getWorld().hashCode();
		int x = l.getChunk().getX();
		int z = l.getChunk().getZ();
		return getChunkFertility(worldHash, x, z);
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
		int dist = (int)(x * x + z * z) / 3125000;
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
	

	/**
	 * Formats the chunk string for a location
	 * DON'T USE getChunk() because it loads the chunk
	 * @param l The location
	 * @return The chunk string
	 */
	public static String formatChunkName(Location l) {
		return String.format("%s,%s,%s", l.getWorld().getName(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
	}
	
	public static String formatChunkName(Chunk c) {
		return String.format("%s,%s,%s", c.getWorld().getName(), c.getX(), c.getZ());
	}
}
