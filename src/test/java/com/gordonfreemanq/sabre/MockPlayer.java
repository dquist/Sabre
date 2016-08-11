package com.gordonfreemanq.sabre;

import java.util.PriorityQueue;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

// Mocks a Bukkit player class
public abstract class MockPlayer implements Player {
	
    public UUID ID;
    public String name;
    public boolean isOnline;
    public World world;
    public Location location;
    public PriorityQueue<String> messages;
    
    public MockPlayer init() {
		ID = UUID.randomUUID();
    	name = "TestPlayer";
    	messages = new PriorityQueue<String>();
    	return this;
    }
    
    @Override
    public UUID getUniqueId() {
    	return ID;
    }
    
    @Override
    public String getName() {
    	return name;
    }
    
    @Override
    public boolean isOnline() {
    	return isOnline;
    }
    
    @Override
    public World getWorld() {
    	return world;
    }
    
    @Override
    public Location getLocation() {
    	return location;
    }
    
    @Override
    public boolean teleport(Location l) {
    	this.location = l;
    	this.world = l.getWorld();
    	return true;
    }
    
    @Override
    public void sendMessage(String msg) {
    	messages.add(msg);
    }
    
    @Override
    public void sendMessage(String[] msg) {
    	for(String m : msg) {
    		messages.add(m);
    	}
    }
}
