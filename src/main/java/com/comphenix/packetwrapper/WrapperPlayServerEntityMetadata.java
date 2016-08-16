package com.comphenix.packetwrapper;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

public class WrapperPlayServerEntityMetadata extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_METADATA;
    
    public WrapperPlayServerEntityMetadata() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityMetadata(PacketContainer packet) {
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
     * Retrieve Metadata.
     * @return The current Metadata
     */
    public List<WrappedWatchableObject> getMetadata() {
    	return handle.getWatchableCollectionModifier().read(0);
    }
    
    /**
     * Set Metadata.
     * @param value - new value.
     */
    public void setMetadata(List<WrappedWatchableObject> value) {
        handle.getWatchableCollectionModifier().write(0, value);
    }
    
}

