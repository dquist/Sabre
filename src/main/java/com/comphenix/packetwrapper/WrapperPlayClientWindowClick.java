package com.comphenix.packetwrapper;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientWindowClick extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.WINDOW_CLICK;
    
    public WrapperPlayClientWindowClick() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientWindowClick(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Window ID.
     * <p>
     * Notes: the id of the window which was clicked. 0 for player inventory.
     * @return The current Window ID
     */
    public int getWindowId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Window ID.
     * @param value - new value.
     */
    public void setWindowId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Slot.
     * <p>
     * Notes: the clicked slot. See below.
     * @return The current Slot
     */
    public int getSlot() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Slot.
     * @param value - new value.
     */
    public void setSlot(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Button.
     * <p>
     * Notes: the button used in the click. See below.
     * @return The current Button
     */
    public int getButton() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Button.
     * @param value - new value.
     */
    public void setButton(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Action number.
     * <p>
     * Notes: a unique number for the action, used for transaction handling (See the Transaction packet).
     * @return The current Action number
     */
    public short getActionNumber() {
        return handle.getShorts().read(0);
    }
    
    /**
     * Set Action number.
     * @param value - new value.
     */
    public void setActionNumber(short value) {
        handle.getShorts().write(0, value);
    }
    
    /**
     * Retrieve Mode.
     * <p>
     * Notes: inventory operation mode. See below.
     * @return The current Mode
     */
    public int getMode() {
        return handle.getIntegers().read(3);
    }
    
    /**
     * Set Mode.
     * @param value - new value.
     */
    public void setMode(int value) {
        handle.getIntegers().write(3,  value);
    }
    
    /**
     * Retrieve Clicked item.
     * @return The current Clicked item
     */
    public ItemStack getClickedItem() {
        return handle.getItemModifier().read(0);
    }
    
    /**
     * Set Clicked item.
     * @param value - new value.
     */
    public void setClickedItem(ItemStack value) {
        handle.getItemModifier().write(0, value);
    }
    
}

