package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerPosition extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.POSITION;
    
    public WrapperPlayServerPosition() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerPosition(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve X.
     * <p>
     * Notes: absolute/Relative position
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
     * Retrieve Y.
     * <p>
     * Notes: absolute/Relative position
     * @return The current Y
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }
    
    /**
     * Set Y.
     * @param value - new value.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }
    
    /**
     * Retrieve Z.
     * <p>
     * Notes: absolute/Relative position
     * @return The current Z
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }
    
    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }
    
    /**
     * Retrieve Yaw.
     * <p>
     * Notes: absolute/Relative rotation on the X Axis, in degrees
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
     * Notes: absolute/Relative rotation on the Y Axis, in degrees
     * @return The current Pitch
     */
    public float getPitch() {
        return handle.getFloat().read(1);
    }
    
    /**
     * Set Pitch.
     * @param value - new value.
     */
    public void setPitch(float value) {
        handle.getFloat().write(1, value);
    }
    
    // TODO: Flags
}

