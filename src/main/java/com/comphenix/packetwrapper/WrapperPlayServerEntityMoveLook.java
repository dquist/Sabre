package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerEntityMoveLook extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_MOVE_LOOK;
    
    public WrapperPlayServerEntityMoveLook() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityMoveLook(PacketContainer packet) {
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
     * Retrieve DX.
     * <p>
     * Notes: change in X position as a Fixed-Point number
     * @return The current DX
     */
    public byte getDx() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set DX.
     * @param value - new value.
     */
    public void setDx(byte value) {
        handle.getBytes().write(0, value);
    }
    
    /**
     * Retrieve DY.
     * <p>
     * Notes: change in Y position as a Fixed-Point number
     * @return The current DY
     */
    public byte getDy() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set DY.
     * @param value - new value.
     */
    public void setDy(byte value) {
        handle.getBytes().write(0, value);
    }
    
    /**
     * Retrieve DZ.
     * <p>
     * Notes: change in Z position as a Fixed-Point number
     * @return The current DZ
     */
    public byte getDz() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set DZ.
     * @param value - new value.
     */
    public void setDz(byte value) {
        handle.getBytes().write(0, value);
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

