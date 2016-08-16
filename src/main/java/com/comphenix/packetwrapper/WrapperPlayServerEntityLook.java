package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerEntityLook extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_LOOK;
    
    public WrapperPlayServerEntityLook() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityLook(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Yaw.
     * <p>
     * Notes: the X Axis rotation as a fraction of 360
     * @return The current Yaw
     */
    public byte getYaw() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Yaw.
     * @param value - new value.
     */
    public void setYaw(byte value) {
        handle.getBytes().write(0, value);
    }
    
    /**
     * Retrieve Pitch.
     * <p>
     * Notes: the Y Axis rotation as a fraction of 360
     * @return The current Pitch
     */
    public byte getPitch() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Pitch.
     * @param value - new value.
     */
    public void setPitch(byte value) {
        handle.getBytes().write(0, value);
    }
    
    /**
     * Retrieve On Ground.
     * @return The current On Ground
     */
    public boolean getOnGround() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set On Ground.
     * @param value - new value.
     */
    public void setOnGround(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
    
}

