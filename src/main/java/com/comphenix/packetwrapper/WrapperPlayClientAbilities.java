package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientAbilities extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ABILITIES;
    
    public WrapperPlayClientAbilities() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientAbilities(PacketContainer packet) {
        super(packet, TYPE);
    }

    public boolean isInvulnurable() {
    	return handle.getBooleans().read(0);
    }

    public void setInvulnurable(boolean value) {
    	handle.getBooleans().write(0, value);
    }

    public boolean isFlying() {
    	return handle.getBooleans().read(0);
    }

    public void setFlying(boolean value) {
    	handle.getBooleans().write(0, value);
    }

    public boolean canFly() {
    	return handle.getBooleans().read(0);
    }

    public void setCanFly(boolean value) {
    	handle.getBooleans().write(0, value);
    }

    public boolean canInstantlyBuild() {
    	return handle.getBooleans().read(0);
    }

    public void setCanInstantlyBuild(boolean value) {
    	handle.getBooleans().write(0, value);
    }

    public float getFlyingSpeed() {
    	return handle.getFloat().read(0);
    }

    public void setFlyingSpeed(float value) {
    	handle.getFloat().write(0, value);
    }

    public float getWalkingSpeed() {
    	return handle.getFloat().read(0);
    }

    public void setWalkingSpeed(float value) {
    	handle.getFloat().write(0, value);
    }
}

