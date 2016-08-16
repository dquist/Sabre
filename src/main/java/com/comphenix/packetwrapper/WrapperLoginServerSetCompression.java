package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginServerSetCompression extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.SET_COMPRESSION;
    
    public WrapperLoginServerSetCompression() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerSetCompression(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Threshold.
     * <p>
     * Notes: threshold is the max size of a packet before its compressed
     * @return The current Threshold
     */
    public int getThreshold() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Threshold.
     * @param value - new value.
     */
    public void setThreshold(int value) {
        handle.getIntegers().write(0, value);
    }
    
}

