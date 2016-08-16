package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientCloseWindow extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.CLOSE_WINDOW;
    
    public WrapperPlayClientCloseWindow() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientCloseWindow(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Window id.
     * <p>
     * Notes: this is the id of the window that was closed. 0 for inventory.
     * @return The current Window id
     */
    public int getWindowId() {
        return  handle.getIntegers().read(0);
    }
    
    /**
     * Set Window id.
     * @param value - new value.
     */
    public void setWindowId(int value) {
        handle.getIntegers().write(0, value);
    }
    
}

