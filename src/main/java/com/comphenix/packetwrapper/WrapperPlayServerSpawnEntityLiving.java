package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

public class WrapperPlayServerSpawnEntityLiving extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING;
    
    public WrapperPlayServerSpawnEntityLiving() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnEntityLiving(PacketContainer packet) {
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
     * Retrieve Type.
     * <p>
     * Notes: the type of mob. See Mobs
     * @return The current Type
     */
    public int getType() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Type.
     * @param value - new value.
     */
    public void setType(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve X.
     * <p>
     * Notes: x position as a Fixed-Point number
     * @return The current X
     */
    public int getX() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set X.
     * @param value - new value.
     */
    public void setX(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Y.
     * <p>
     * Notes: y position as a Fixed-Point number
     * @return The current Y
     */
    public int getY() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set Y.
     * @param value - new value.
     */
    public void setY(int value) {
        handle.getIntegers().write(3, value);
    }
    
    /**
     * Retrieve Z.
     * <p>
     * Notes: z position as a Fixed-Point number
     * @return The current Z
     */
    public int getZ() {
        return handle.getIntegers().read(4);
    }
    
    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(int value) {
        handle.getIntegers().write(4, value);
    }
    
    /**
     * Retrieve Yaw.
     * <p>
     * Notes: the yaw in steps of 2p/256
     * @return The current Yaw
     */
    public int getYaw() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Yaw.
     * @param value - new value.
     */
    public void setYaw(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Pitch.
     * <p>
     * Notes: the pitch in steps of 2p/256
     * @return The current Pitch
     */
    public int getPitch() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Pitch.
     * @param value - new value.
     */
    public void setPitch(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Head Pitch.
     * <p>
     * Notes: the pitch in steps of 2p/256
     * @return The current Head Pitch
     */
    public int getHeadPitch() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Head Pitch.
     * @param value - new value.
     */
    public void setHeadPitch(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Velocity X.
     * @return The current Velocity X
     */
    public int getVelocityX() {
        return handle.getIntegers().read(5);
    }
    
    /**
     * Set Velocity X.
     * @param value - new value.
     */
    public void setVelocityX(int value) {
        handle.getIntegers().write(5, value);
    }
    
    /**
     * Retrieve Velocity Y.
     * @return The current Velocity Y
     */
    public int getVelocityY() {
        return handle.getIntegers().read(6);
    }
    
    /**
     * Set Velocity Y.
     * @param value - new value.
     */
    public void setVelocityY(int value) {
        handle.getIntegers().write(6, value);
    }
    
    /**
     * Retrieve Velocity Z.
     * @return The current Velocity Z
     */
    public int getVelocityZ() {
        return handle.getIntegers().read(7);
    }
    
    /**
     * Set Velocity Z.
     * @param value - new value.
     */
    public void setVelocityZ(int value) {
        handle.getIntegers().write(7, value);
    }
    
    /**
     * Retrieve Metadata.
     * @return The current Metadata
     */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }
    
    /**
     * Set Metadata.
     * @param value - new value.
     */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
    
}

