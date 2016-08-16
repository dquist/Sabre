package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerRemoveEntityEffect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.REMOVE_ENTITY_EFFECT;
    
    public WrapperPlayServerRemoveEntityEffect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerRemoveEntityEffect(PacketContainer packet) {
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
     * Retrieve Effect ID.
     * @return The current Effect ID
     */
    public int getEffectId() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Effect ID.
     * @param value - new value.
     */
    public void setEffectId(int value) {
        handle.getIntegers().write(1, value);
    }
    
}

