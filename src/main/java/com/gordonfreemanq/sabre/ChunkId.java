package com.gordonfreemanq.sabre;

import org.bukkit.Chunk;

public class ChunkId 
{
	
	public static String getName(Chunk chunk)
	{
		return chunk.getWorld().getName() + ":" + String.valueOf(chunk.getX()) + ":" + String.valueOf(chunk.getZ());
	}
	
	public ChunkId(Chunk chunk)
	{
		name = chunk.getWorld().getName() + ":" + String.valueOf(chunk.getX()) + ":" + String.valueOf(chunk.getZ());
	}
	
	private String name;
	public String getName()
	{
		return name;
	}

}
