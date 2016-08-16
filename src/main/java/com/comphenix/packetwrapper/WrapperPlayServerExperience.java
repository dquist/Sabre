package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerExperience extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPERIENCE;
    
    public WrapperPlayServerExperience() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerExperience(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Experience bar.
     * <p>
     * Notes: between 0 and 1
     * @return The current Experience bar
     */
    public float getExperienceBar() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Experience bar.
     * @param value - new value.
     */
    public void setExperienceBar(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve Level.
     * @return The current Level
     */
    public int getLevel() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Level.
     * @param value - new value.
     */
    public void setLevel(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Total Experience.
     * @return The current Total Experience
     */
    public int getTotalExperience() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Total Experience.
     * @param value - new value.
     */
    public void setTotalExperience(int value) {
        handle.getIntegers().write(0, value);
    }
    
}

