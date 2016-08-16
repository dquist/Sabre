package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerSpawnEntityPainting extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SPAWN_ENTITY_PAINTING;
    
    public WrapperPlayServerSpawnEntityPainting() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerSpawnEntityPainting(PacketContainer packet) {
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
     * Retrieve Title.
     * <p>
     * Notes: name of the painting. Max length 13
     * @return The current Title
     */
    public String getTitle() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Title.
     * @param value - new value.
     */
    public void setTitle(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Location.
     * <p>
     * Notes: center coordinates
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Location.
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }
    
    // TODO: Direction
    
}

