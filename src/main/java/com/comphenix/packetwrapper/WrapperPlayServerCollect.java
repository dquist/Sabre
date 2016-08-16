package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerCollect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.COLLECT;
    
    public WrapperPlayServerCollect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerCollect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Collected Entity ID.
     * @return The current Collected Entity ID
     */
    public int getCollectedEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Collected Entity ID.
     * @param value - new value.
     */
    public void setCollectedEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Collector Entity ID.
     * @return The current Collector Entity ID
     */
    public int getCollectorEntityId() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Collector Entity ID.
     * @param value - new value.
     */
    public void setCollectorEntityId(int value) {
        handle.getIntegers().write(1, value);
    }
    
}

