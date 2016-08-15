package com.civfactions.sabre.blocks;

import com.civfactions.sabre.groups.SabreGroup;

public class BuildState {

	
	// Building
	private BuildMode buildMode;
	private SabreGroup buildGroup;
	private ReinforcementMaterial material;
	private boolean infoMode;
	private boolean bypassMode;
	private boolean publicMode;
	private boolean insecureMode;
	
	
	public BuildState() {
		reset();
	}
	
	
	public void reset() {

		this.buildMode = BuildMode.OFF;
		this.buildGroup = null;
		this.material = null;
		this.publicMode = false;
		this.insecureMode = false;
		//this.infoMode = false;
		//this.bypassMode = false;
	}
	
	
	/**
	 * Gets the build mode
	 * @return The current build mode
	 */
	public BuildMode getMode() {
		return this.buildMode;
	}
	
	
	/**
	 * Sets the current build mode
	 * @param buildMode The new build group
	 */
	public void setMode(BuildMode buildMode) {
		this.buildMode = buildMode;
		
		if (buildMode == BuildMode.OFF) {
			this.material = null;
		}
	}
	
	
	/**
	 * Gets the build group
	 * @return The current build mode
	 */
	public SabreGroup getGroup() {
		return this.buildGroup;
	}
	
	
	/**
	 * Sets the current build group
	 * @param buildGroup The new build group
	 */
	public void setGroup(SabreGroup buildGroup) {
		this.buildGroup = buildGroup;
	}
	
	
	/**
	 * Gets the build material
	 * @return The current build material
	 */
	public ReinforcementMaterial getMaterial() {
		return this.material;
	}
	
	
	/**
	 * Sets the current build material
	 * @param material The new build material
	 */
	public void setMaterial(ReinforcementMaterial material) {
		this.material = material;
	}
	
	
	/**
	 * Gets whether block info is enabled
	 * @return Whether block info is enabled
	 */
	public boolean getInfo() {
		return this.infoMode;
	}
	
	
	/**
	 * Sets whether block info is enabled
	 * @param blockInfo The new build info setting
	 */
	public void setInfo(boolean blockInfo) {
		this.infoMode = blockInfo;
	}
	
	
	/**
	 * Gets whether block info is enabled
	 * @return Whether block info is enabled
	 */
	public boolean getBypass() {
		return this.bypassMode;
	}
	
	
	/**
	 * Sets whether block info is enabled
	 * @param blockInfo The new build info setting
	 */
	public void setBypass(boolean bypassMode) {
		this.bypassMode = bypassMode;
	}
	
	
	/**
	 * Gets whether public mode is on
	 * @return Whether public mode is on
	 */
	public boolean getPublic() {
		return this.publicMode;
	}
	
	
	/**
	 * Sets whether public mode is on
	 * @param blockInfo The new public mode
	 */
	public void setPublic(boolean publicMode) {
		this.publicMode = publicMode;
	}
	
	
	/**
	 * Gets whether insecure mode is on
	 * @return Whether insecure mode is on
	 */
	public boolean getInsecure() {
		return this.insecureMode;
	}
	
	
	/**
	 * Sets whether insecure mode is on
	 * @param blockInfo The new insecure mode
	 */
	public void setInsecure(boolean insecureMode) {
		this.insecureMode = insecureMode;
	}
	
	
	/**
	 * Gets a string describing the current group mode
	 * @return The mode string
	 */
	public String getBuildGroupString() {
		String str = buildGroup.getFullName();
		
		if (this.publicMode) {
			str += "-PUBLIC";
		} else if (this.insecureMode) {
			str += "-INSECURE";
		}
		
		return str;
	}

}
