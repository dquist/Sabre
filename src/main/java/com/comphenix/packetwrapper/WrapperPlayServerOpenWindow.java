package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class WrapperPlayServerOpenWindow extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.OPEN_WINDOW;
    
    public WrapperPlayServerOpenWindow() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerOpenWindow(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Window id.
     * <p>
     * Notes: a unique id number for the window to be displayed. Notchian server implementation is a counter, starting at 1.
     * @return The current Window id
     */
    public int getWindowId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Window id.
     * @param value - new value.
     */
    public void setWindowId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Inventory Type.
     * <p>
     * Notes: the window type to use for display. Check below
     * @return The current Inventory Type
     */
    public String getInventoryType() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Inventory Type.
     * @param value - new value.
     */
    public void setInventoryType(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Window title.
     * <p>
     * Notes: the title of the window.
     * @return The current Window title
     */
    public WrappedChatComponent getWindowTitle() {
        return handle.getChatComponents().read(0);
    }
    
    /**
     * Set Window title.
     * @param value - new value.
     */
    public void setWindowTitle(WrappedChatComponent value) {
        handle.getChatComponents().write(0, value);
    }
    
    /**
     * Retrieve Number of Slots.
     * <p>
     * Notes: number of slots in the window (excluding the number of slots in the player inventory).
     * @return The current Number of Slots
     */
    public int getNumberOfSlots() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Number of Slots.
     * @param value - new value.
     */
    public void setNumberOfSlots(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entityHorse's entityId. Only sent when window type is equal to "EntityHorse".
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(2, value);
    }
    
}

