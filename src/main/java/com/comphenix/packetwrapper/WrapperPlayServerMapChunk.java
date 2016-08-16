package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerMapChunk extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP_CHUNK;
    
    public WrapperPlayServerMapChunk() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerMapChunk(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Chunk X.
     * <p>
     * Notes: chunk X coordinate
     * @return The current Chunk X
     */
    public int getChunkX() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Chunk X.
     * @param value - new value.
     */
    public void setChunkX(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Chunk Z.
     * <p>
     * Notes: chunk Z coordinate
     * @return The current Chunk Z
     */
    public int getChunkZ() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Chunk Z.
     * @param value - new value.
     */
    public void setChunkZ(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Ground-Up continuous.
     * <p>
     * Notes: this is True if the packet represents all sections in this vertical column, where the primary bit map specifies exactly which sections are included, and which are air
     * @return The current Ground-Up continuous
     */
    public boolean getGroundUpContinuous() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set Ground-Up continuous.
     * @param value - new value.
     */
    public void setGroundUpContinuous(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
    
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
}

