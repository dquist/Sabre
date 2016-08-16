package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerChat extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.CHAT;
    
    public WrapperPlayServerChat() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerChat(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve JSON Data.
     * <p>
     * Notes: chat , Limited to 32767 bytes
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
    
    /**
     * Retrieve Position.
     * <p>
     * Notes: 0 - Chat (chat box) ,1 - System Message (chat box), 2 - Above action bar
     * @return The current Position
     */
    public byte getPosition() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Position.
     * @param value - new value.
     */
    public void setPosition(byte value) {
        handle.getBytes().write(0, value);
    }
    
}

