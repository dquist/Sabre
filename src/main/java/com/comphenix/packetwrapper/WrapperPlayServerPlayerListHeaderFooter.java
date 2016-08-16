package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerPlayerListHeaderFooter extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER;
    
    public WrapperPlayServerPlayerListHeaderFooter() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerPlayerListHeaderFooter(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Header.
     * @return The current Header
     */
    public WrappedChatComponent getHeader() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set Header.
     * @param value - new value.
     */
    public void setHeader(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
    
    /**
     * Retrieve Footer.
     * @return The current Footer
     */
    public WrappedChatComponent getFooter() {
        return handle.getChatComponents().read(1);
    }
    
    /**
     * Set Footer.
     * @param value - new value.
     */
    public void setFooter(WrappedChatComponent value) {
        handle.getChatComponents().write(1, value);
    }
    
}

