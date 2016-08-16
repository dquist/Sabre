package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerSpawnEntityWeather extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_WEATHER;
    
    public WrapperPlayServerSpawnEntityWeather() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnEntityWeather(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: the entity ID of the thunderbolt
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
     * Notes: the global entity type, currently always 1 for thunderbolt.
     * @return The current Type
     */
    public int getType() {
        return handle.getIntegers().read(4);
    }
    
    /**
     * Set Type.
     * @param value - new value.
     */
    public void setType(int value) {
        handle.getIntegers().write(4, value);
    }
    
    /**
     * Retrieve X.
     * <p>
     * Notes: thunderbolt X a fixed-point number
     * @return The current X
     */
    public int getX() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set X.
     * @param value - new value.
     */
    public void setX(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Y.
     * <p>
     * Notes: thunderbolt Y a fixed-point number
     * @return The current Y
     */
    public int getY() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Y.
     * @param value - new value.
     */
    public void setY(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Z.
     * <p>
     * Notes: thunderbolt Z a fixed-point number
     * @return The current Z
     */
    public int getZ() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(int value) {
        handle.getIntegers().write(3, value);
    }
    
}

