package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;

public class WrapperPlayServerMultiBlockChange extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MULTI_BLOCK_CHANGE;
    
    public WrapperPlayServerMultiBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerMultiBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Chunk X.
     * <p>
     * Notes: chunk X coordinate
     * @return The current Chunk X
     */
    public ChunkCoordIntPair getChunkX() {
        return handle.getChunkCoordIntPairs().read(0);
    }
    
    /**
     * Set Chunk X.
     * @param value - new value.
     */
    public void setChunkX(ChunkCoordIntPair value) {
        handle.getChunkCoordIntPairs().write(0, value);
    }
    
    /**
     * Retrieve Chunk Z.
     * <p>
     * Notes: chunk Z Coordinate
     * @return The current Chunk Z
     */
    public ChunkCoordIntPair getChunkZ() {
        return handle.getChunkCoordIntPairs().read(0);
    }
    
    /**
     * Set Chunk Z.
     * @param value - new value.
     */
    public void setChunkZ(ChunkCoordIntPair value) {
        handle.getChunkCoordIntPairs().write(0, value);
    }
    
    // Cannot find type for b
    // Cannot find type for b
}

