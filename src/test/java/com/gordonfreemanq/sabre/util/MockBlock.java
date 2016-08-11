package com.gordonfreemanq.sabre.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public abstract class MockBlock implements Block {

	public World world;
	public int X;
	public int Y;
	public int Z;
	public Material type;
	
	public void init() {
		type = Material.AIR;
	}
	
	@Override
	public Material getType() {
		return type;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int getTypeId() {
		return type.getId();
	}
	
	@Override
	public int getX() {
		return X;
	}
	
	@Override
	public int getY() {
		return Y;
	}
	
	@Override
	public int getZ() {
		return Z;
	}
	
	@Override
	public Location getLocation() {
		return new Location(world, X, Y, Z);
	}
	
	@Override
	public boolean isEmpty() {
		return type == Material.AIR;
	}
}
