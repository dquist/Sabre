package com.gordonfreemanq.sabre.blocks;

import org.bukkit.Location;

public class BastionBlock extends SabreBlock {

	protected int testVal1;
	protected int testVal2;
	protected int testVal3;
	
	private static final String blockName = "Bastion";
	
	public BastionBlock(Location location) {
		super(location, blockName);
		
		this.hasEffectRadius = true;
	}
	
	@Override
	public String getTypeName() {
		return "Bastion";
	}
	
	@Override
	public boolean affectsLocation(Location l) {
		return false;
	}
	
	
	public int getTestVal1() {
		return this.testVal1;
	}
	
	public void setTestVal1(int testVal1) {
		this.testVal1 = testVal1;
	}
	
	public int getTestVal2() {
		return this.testVal2;
	}
	
	public void setTestVal2(int testVal2) {
		this.testVal2 = testVal2;
	}
	
	public int getTestVal3() {
		return this.testVal3;
	}
	
	public void setTestVal3(int testVal3) {
		this.testVal3 = testVal3;
	}

}
