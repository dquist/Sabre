package com.gordonfreemanq.sabre.util;

import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.generator.ChunkGenerator;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

import static org.mockito.Mockito.*;

public class MockWorldFactory {

    private static final Map<String, World> createdWorlds = new HashMap<String, World>();
    private static final Map<UUID, World> worldUIDS = new HashMap<UUID, World>();

    private static final Map<World, Boolean> pvpStates = new WeakHashMap<World, Boolean>();
    private static final Map<World, Boolean> keepSpawnInMemoryStates = new WeakHashMap<World, Boolean>();
    private static final Map<World, Difficulty> difficultyStates = new WeakHashMap<World, Difficulty>();

    private MockWorldFactory() {
    }

    private static void registerWorld(World world) {
        createdWorlds.put(world.getName(), world);
        worldUIDS.put(world.getUID(), world);
        new File(TestInstanceCreator.worldsDirectory, world.getName()).mkdir();
    }

    private static World basics(String world, World.Environment env, WorldType type) {
    	MockWorld mockWorld = mock(MockWorld.class, Mockito.CALLS_REAL_METHODS);
    	mockWorld.init();
    	mockWorld.name = world;
    	mockWorld.env = env;
    	mockWorld.worldType = type;
        mockWorld.worldFolder = new File(TestInstanceCreator.serverDirectory, mockWorld.getName());

        when(mockWorld.getBlockAt(any(Location.class))).thenAnswer(getBlockAtAnswer);
        when(mockWorld.getBlockAt(anyInt(), anyInt(), anyInt())).thenAnswer(getBlockAtAnswer);
        
        when(mockWorld.getUID()).thenReturn(UUID.randomUUID());
        when(mockWorld.getLoadedChunks()).thenReturn(new Chunk[0]);
        when(mockWorld.getChunkAt(any(Location.class))).thenAnswer(chunkAnswer);
        when(mockWorld.getChunkAt(any(Block.class))).thenAnswer(chunkAnswer);
        when(mockWorld.getChunkAt(any(int.class), any(int.class))).thenAnswer(chunkAnswer);
        return mockWorld;
    }
    
    static Answer<Chunk> chunkAnswer = new Answer<Chunk>() {
        @Override
        public Chunk answer(InvocationOnMock invocation) throws Throwable {
            Location loc = null;
            World mockWorld = (World)invocation.getMock();
            
            Object[] args = invocation.getArguments();
            try {
	            if (args[0] instanceof Location) {
	                loc = (Location) args[0];
	            } else if (args[0] instanceof Block) {
	            	loc = ((Block)args[0]).getLocation();
	            } else if (invocation.getArguments().length == 3){
	            	loc = new Location(mockWorld, (int)args[0], (int)args[1], (int)args[2]);
	            }
            } catch (Exception e) {
            	return null;
            }

            Chunk mockChunk = mock(Chunk.class);

            Block b = loc.getBlock();
            when(mockChunk.getBlock(anyInt(), anyInt(), anyInt())).thenReturn(b);
            when(mockChunk.getWorld()).thenReturn(loc.getWorld());
            when(mockChunk.getX()).thenReturn(loc.getBlockX() >> 4);
            when(mockChunk.getZ()).thenReturn(loc.getBlockZ() >> 4);
            return mockChunk;
        }
    };
        
    static Answer<Block> getBlockAtAnswer = new Answer<Block>() {
        @SuppressWarnings("deprecation")
		@Override
        public Block answer(InvocationOnMock invocation) throws Throwable {
            
        	Object[] args = invocation.getArguments();
        	Location loc;
            try {
            	if (args.length == 3) {
            		loc = new Location((World)invocation.getMock(), (int)args[0], (int)args[1], (int)args[2]);
            	} else {
            		loc = (Location) invocation.getArguments()[0];
            	}
            } catch (Exception e) {
                return null;
            }
            Material blockType = Material.AIR;
            Block mockBlock = mock(Block.class);
            if (loc.getBlockY() < 64) {
                blockType = Material.DIRT;
            }

            when(mockBlock.getType()).thenReturn(blockType);
            when(mockBlock.getTypeId()).thenReturn(blockType.getId());
            when(mockBlock.getWorld()).thenReturn(loc.getWorld());
            when(mockBlock.getX()).thenReturn(loc.getBlockX());
            when(mockBlock.getY()).thenReturn(loc.getBlockY());
            when(mockBlock.getZ()).thenReturn(loc.getBlockZ());
            when(mockBlock.getLocation()).thenReturn(loc);
            when(mockBlock.isEmpty()).thenReturn(blockType == Material.AIR);
            return mockBlock;
        }
    };

    public static World makeNewMockWorld(String world, World.Environment env, WorldType type) {
        World w = basics(world, env, type);
        registerWorld(w);
        return w;
    }

    public static World makeNewMockWorld(String world, World.Environment env, WorldType type, long seed,
            ChunkGenerator generator) {
        World mockWorld = basics(world, env, type);
        when(mockWorld.getGenerator()).thenReturn(generator);
        when(mockWorld.getSeed()).thenReturn(seed);
        registerWorld(mockWorld);
        return mockWorld;
    }

    public static World getWorld(String name) {
        return createdWorlds.get(name);
    }

    public static World getWorld(UUID worldUID) {
        return worldUIDS.get(worldUID);
    }

    public static List<World> getWorlds() {
        // we have to invert the order!
        ArrayList<World> myList = new ArrayList<World>(createdWorlds.values());
        List<World> retList = new ArrayList<World>();
        for (int i = (myList.size() - 1); i >= 0; i--) {
            retList.add(myList.get(i));
        }
        return retList;
    }

    public static void clearWorlds() {
        for (String name : createdWorlds.keySet())
            new File(TestInstanceCreator.worldsDirectory, name).delete();
        createdWorlds.clear();
        worldUIDS.clear();
    }
}
