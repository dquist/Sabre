package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerEntityStatus extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_STATUS;
    
    public WrapperPlayServerEntityStatus() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityStatus(PacketContainer packet) {
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
     * Retrieve Entity Status.
     * <p>
     * Notes: see below
     * @return The current Entity Status
     */
    public byte getEntityStatus() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Entity Status.
     * @param value - new value.
     */
    public void setEntityStatus(byte value) {
        handle.getBytes().write(0, value);
    }
    
}

