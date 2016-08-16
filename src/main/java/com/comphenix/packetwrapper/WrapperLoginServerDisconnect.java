package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperLoginServerDisconnect extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.DISCONNECT;
    
    public WrapperLoginServerDisconnect() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerDisconnect(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve JSON Data.
     * @return The current JSON Data
     */
    public WrappedChatComponent getJsonData() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set JSON Data.
     * @param value - new value.
     */
    public void setJsonData(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
    
}

