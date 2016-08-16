package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientCustomPayload extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CUSTOM_PAYLOAD;
    
    public WrapperPlayClientCustomPayload() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientCustomPayload(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Channel.
     * <p>
     * Notes: name of the "channel" used to send the data.
     * @return The current Channel
     */
    public String getChannel() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Channel.
     * @param value - new value.
     */
    public void setChannel(String value) {
        handle.getStrings().write(0, value);
    }
    
    // Cannot find type for b
}

