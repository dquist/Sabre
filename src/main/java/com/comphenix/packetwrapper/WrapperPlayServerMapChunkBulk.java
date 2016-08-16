package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerMapChunkBulk extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP_CHUNK_BULK;
    
    public WrapperPlayServerMapChunkBulk() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerMapChunkBulk(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Sky light sent.
     * <p>
     * Notes: whether or not the chunk data contains a light nibble array. This is true in the main world, false in the end + nether
     * @return The current Sky light sent
     */
    public boolean getSkyLightSent() {
        return (boolean) handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set Sky light sent.
     * @param value - new value.
     */
    public void setSkyLightSent(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, (boolean) value);
    }
    
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
}

