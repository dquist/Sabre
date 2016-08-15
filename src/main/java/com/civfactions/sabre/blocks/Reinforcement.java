package com.civfactions.sabre.blocks;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Openable;

import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.groups.SabreGroup;

public class Reinforcement {

	private final Location location;
	private UUID groupID;
	private Material material;
	private int startStrength;
	private int strength;
	private long creationTime;
	private boolean isPublic;
	private boolean isInsecure;
	private boolean admin;
	
	private SabreGroup group;
	
	
	/**
	 * Creates a new Reinforcement instance
	 * @group groupID The reinforcement group ID
	 * @param durability The starting durability
	 * @param creationTime The time it was created
	 */
	public Reinforcement(Location location, UUID groupID, Material material, int startStrength, long creationTime, boolean admin) {
		this.location = location;
		this.groupID = groupID;
		this.material = material;
		this.strength = startStrength;
		this.startStrength = startStrength;
		this.creationTime = creationTime;
		this.isPublic = false;
		this.isInsecure = false;
		this.admin = admin;
	}
	
	
	/**
	 * Gets the location
	 * @return The location
	 */
	public Location getLocation() {
		return this.location;
	}
	
	
	/**
	 * Gets the starting strength
	 * @return The starting strength
	 */
	public int getStartStrength() {
		return startStrength;
	}
	
	
	/**
	 * Sets the strength of a reinforcement.
	 * @param strength The current durability.
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	
	/**
	 * Gets the current strength
	 * @return The current strength
	 */
	public int getStrength() {
		return strength;
	}
	
	
	/**
	 * Gets the creation time of the reinforcement
	 * @return The creation time, or 0 if it is mature.
	 */
	public long getCreationTime() {
		return this.creationTime;
	}
	
	
	/**
	 * Sets the maturation time of this reinforcement.
	 * @param The time in seconds it was created.
	 */
	public void setCreationTime(int creationTime) {
		this.creationTime = creationTime;
	}
	
	
	/**
	 * Gets whether the block reinforcement has matured
	 * @return true if the reinforcement is mature
	 */
	public boolean isMature() {
		return creationTime == 0;
	}
	
	
	/**
	 * Gets the reinforcement group
	 * @return The reinforcement group ID
	 */
	public SabreGroup getGroup() {
		if (group == null) {
			group = SabrePlugin.instance().getGroupManager().getGroupByID(groupID);
		}
		return group;
	}
	
	
	/**
	 * Sets the reinforcement group
	 * @param group The new reinforcement group ID
	 */
	public void setGroupID(UUID groupID) {
		this.groupID = groupID;
	}
	
	
	/**
	 * Gets the material
	 * @return The material
	 */
	public Material getMaterial() {
		return this.material;
	}
	
	
	/**
     * Returns true if the block has an inventory that can be opened.
     * @return true if the block can be opened
     */
    public boolean isSecurable() {
        Block block = this.location.getBlock();
        return block.getState() instanceof InventoryHolder || block.getState().getData() instanceof Openable;
    }
    
    
    /**
     * Gets the usage string for a protected block
     * @return A string representation of a reinforcement's health, material, ect.
     */
    public String getUsageString() {
        if (isSecurable()) {
            return "Locked";
        } else {
            return "Reinforced";
        }
    }
    
    
    /**
     * Gets the percent health percent
     * @return The health percent
     */
    public Long getHeathPercent() {
    	Double dbl = new Double((double)strength / startStrength);
    	return Math.round(dbl * 100.0);
    }
    
    
    /**
     * Gets whether the record is public
     * @return true if it is public
     */
    public boolean getPublic() {
    	return this.isPublic;
    }
    
    
    /**
     * Sets whether the record is public
     * @param isPublic The new public setting
     */
    public void setPublic(boolean isPublic) {
    	this.isPublic = isPublic;
    }
    
    
    /**
     * Gets whether the record is isInsecure
     * @return true if it is isInsecure
     */
    public boolean getInsecure() {
    	return this.isInsecure;
    }
    
    
    /**
     * Sets whether the record is isInsecure
     * @param isPublic The new isInsecure setting
     */
    public void setInsecure(boolean isInsecure) {
    	this.isInsecure = isInsecure;
    }
    
    
    /**
     * Gets whether the record is admin block
     * @return true if it is admin
     */
    public boolean isAdmin() {
    	return this.admin;
    }
    
    
    /**
     * Checks if another reinforcement is like this one
     * @param other The other to check
     * @return true if they are the same
     */
    public boolean isLike(Reinforcement other) {
    	return other != null
			&& location.equals(other.location)
			&& groupID.equals(other.groupID)
			&& strength == other.strength
			&& material.equals(other.material)
			&& isPublic == other.isPublic
			&& isInsecure == other.isInsecure;
    }
    
    
}
