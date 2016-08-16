package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerKickDisconnect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.KICK_DISCONNECT;
    
    public WrapperPlayServerKickDisconnect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerKickDisconnect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Reason.
     * <p>
     * Notes: displayed to the client when the connection terminates. Must be valid JSON.
     * @return The current Reason
     */
    public WrappedChatComponent getReason() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set Reason.
     * @param value - new value.
     */
    public void setReason(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
    
}

