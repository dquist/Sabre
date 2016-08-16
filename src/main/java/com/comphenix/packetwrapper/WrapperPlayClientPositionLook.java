package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientPositionLook extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.POSITION_LOOK;
    
    public WrapperPlayClientPositionLook() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientPositionLook(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve X.
     * <p>
     * Notes: absolute position
     * @return The current X
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }
    
    /**
     * Set X.
     * @param value - new value.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }
    
    /**
     * Retrieve FeetY.
     * <p>
     * Notes: absolute feet position. Is normally HeadY - 1.62. Used to modify the players bounding box when going up stairs, crouching, etc…
     * @return The current FeetY
     */
    public double getFeety() {
        return handle.getDoubles().read(0);
    }
    
    /**
     * Set FeetY.
     * @param value - new value.
     */
    public void setFeety(double value) {
        handle.getDoubles().write(0, value);
    }
    
    /**
     * Retrieve Z.
     * <p>
     * Notes: absolute position
     * @return The current Z
     */
    public double getZ() {
        return handle.getDoubles().read(0);
    }
    
    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(double value) {
        handle.getDoubles().write(0, value);
    }
    
    /**
     * Retrieve Yaw.
     * <p>
     * Notes: absolute rotation on the X Axis, in degrees
     * @return The current Yaw
     */
    public float getYaw() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Yaw.
     * @param value - new value.
     */
    public void setYaw(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve Pitch.
     * <p>
     * Notes: absolute rotation on the Y Axis, in degrees
     * @return The current Pitch
     */
    public float getPitch() {
        return handle.getFloat().read(0);
    }
    
    /**
     * Set Pitch.
     * @param value - new value.
     */
    public void setPitch(float value) {
        handle.getFloat().write(0, value);
    }
    
    /**
     * Retrieve On Ground.
     * <p>
     * Notes: true if the client is on the ground, False otherwise
     * @return The current On Ground
     */
    public boolean getOnGround() {
        return (boolean) handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set On Ground.
     * @param value - new value.
     */
    public void setOnGround(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, (boolean) value);
    }
    
}

