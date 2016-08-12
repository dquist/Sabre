package com.gordonfreemanq.sabre.util;

import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mockito.Mockito;

// Mocks a Bukkit player class
public abstract class MockPlayer implements Player {
	
    public UUID ID;
    public String name;
    public boolean isOnline;
    public World world;
    public Location location;
    public LinkedList<String> messages;
    public Location bedLocation;
    
    public static MockPlayer create(String name) {
    	MockPlayer p = mock(MockPlayer.class, Mockito.CALLS_REAL_METHODS);
		p.ID = UUID.randomUUID();
    	p.name = name;
    	p.messages = new LinkedList<String>();
    	return p;
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
    
    @Override
    public Location getBedSpawnLocation() {
    	return bedLocation;
    }
    
    @Override
    public void setBedSpawnLocation(Location l) {
    	bedLocation = l;
    }
    
    @Override
    public void setBedSpawnLocation(Location l, boolean force) {
    	bedLocation = l;
    }
}
