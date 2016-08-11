package com.gordonfreemanq.sabre.util;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;

public abstract class MockWorld implements World {

	public String name;
	public Environment env;
	public WorldType worldType;
	public File worldFolder;
	
	public MockWorld init() {
		name = "world";
		env = Environment.NORMAL;
		worldType = WorldType.NORMAL;
		return this;
	}
	
	@Override
	public WorldType getWorldType() {
		return worldType;
	}
	
	@Override
	public File getWorldFolder() {
		return worldFolder;
	}
	
	@Override
	public Block getBlockAt(Location l) {
		return null;
	}
	
	@Override
	public Block getBlockAt(int x, int y, int z) {
		return null;
	}
}
