package com.gordonfreemanq.sabre.util;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MockWorldFactory {

    private static final Map<String, World> createdWorlds = new HashMap<String, World>();
    private static final Map<UUID, World> worldUIDS = new HashMap<UUID, World>();

    private MockWorldFactory() {
    }

    private static void registerWorld(World world) {
        createdWorlds.put(world.getName(), world);
        worldUIDS.put(world.getUID(), world);
        new File(TestFixture.worldsDirectory, world.getName()).mkdir();
    }

    private static World basics(String name, World.Environment env, WorldType type) {
    	MockWorld mockWorld = MockWorld.create(name, env, type);
        mockWorld.worldFolder = new File(TestFixture.serverDirectory, mockWorld.getName());
        return mockWorld;
    }

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
            new File(TestFixture.worldsDirectory, name).delete();
        createdWorlds.clear();
        worldUIDS.clear();
    }
}
