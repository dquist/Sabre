package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperStatusClientInPing extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Status.Client.IN_PING;
    
    public WrapperStatusClientInPing() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperStatusClientInPing(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Time.
     * @return The current Time
     */
    public long getTime() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set Time.
     * @param value - new value.
     */
    public void setTime(long value) {
        handle.getLongs().write(0, value);
    }
    
}

