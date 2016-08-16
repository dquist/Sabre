package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerUpdateHealth extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_HEALTH;
    
    public WrapperPlayServerUpdateHealth() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerUpdateHealth(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Health.
     * <p>
     * Notes: 0 or less = dead, 20 = full HP
     * @return The current Health
     */
    public float getHealth() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Health.
     * @param value - new value.
     */
    public void setHealth(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve Food.
     * <p>
     * Notes: 0 - 20
     * @return The current Food
     */
    public int getFood() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Food.
     * @param value - new value.
     */
    public void setFood(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Food Saturation.
     * <p>
     * Notes: seems to vary from 0.0 to 5.0 in integer increments
     * @return The current Food Saturation
     */
    public float getFoodSaturation() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set Food Saturation.
     * @param value - new value.
     */
    public void setFoodSaturation(float value) {
        handle.getFloat().write(1, value);
    }
    
}

