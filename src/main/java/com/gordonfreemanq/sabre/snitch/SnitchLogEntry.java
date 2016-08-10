package com.gordonfreemanq.sabre.snitch;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

public class SnitchLogEntry {
	
	public final UUID snitchID;
	public final UUID player;
	public final SnitchAction action;
	public  Date time;
	public Material material;
	public Location loc;
	public UUID victim;
	public String entity;
	
	// Use for report generation
	public int count;
	public boolean skip;

	public SnitchLogEntry(UUID snitchID, UUID player, SnitchAction action, Date time) {
		this.snitchID = snitchID;
		this.player = player;
		this.action = action;
		this.time = time;
		
		this.count = 1;
		this.skip = false;
	}
	
	
	
	public boolean isDuplicate(SnitchLogEntry other) {
		
		if (!checkLocation(other)) {
			return false;
		}
		
		if (!checkMaterial(other)) {
			return false;
		}
		
		if (!checkVictim(other)) {
			return false;
		}
		
		if (!checkEntity(other)) {
			return false;
		}
		
		if (!this.snitchID.equals(other.snitchID) 
				|| !this.player.equals(other.player) 
				|| !this.action.equals(other.action))
		{
			return false;
		}
		
		long seconds = Math.abs((this.time.getTime() - other.time.getTime()));
		return seconds < 60000;
	}
	
	
	private boolean checkLocation(SnitchLogEntry other) {
		
		if (this.loc == null || other.loc == null) {
			if (this.loc != other.loc) {
				return false;
			}
			return true;
		}
		
		return loc.equals(other.loc);
	}
	
	private boolean checkMaterial(SnitchLogEntry other) {
		
		if (this.material == null || other.material == null) {
			if (this.material != other.material) {
				return false;
			}
			return true;
		}
		
		return material.equals(other.material);
	}
	
	
	private boolean checkVictim(SnitchLogEntry other) {
		
		if (this.victim == null || other.victim == null) {
			if (this.victim != other.victim) {
				return false;
			}
			return true;
		}
		
		return victim.equals(other.victim);
	}
	
	
	private boolean checkEntity(SnitchLogEntry other) {
		
		if (this.entity == null || other.entity == null) {
			if (this.entity != other.entity) {
				return false;
			}
			return true;
		}
		
		return entity.equals(other.entity);
	}
	
	
}
