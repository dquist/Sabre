package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientEnchantItem extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ENCHANT_ITEM;
    
    public WrapperPlayClientEnchantItem() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientEnchantItem(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Window ID.
     * <p>
     * Notes: the ID sent by Open Window
     * @return The current Window ID
     */
    public int getWindowId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Window ID.
     * @param value - new value.
     */
    public void setWindowId(byte value) {
        handle.getIntegers().write(0, (int) value);
    }
    
    /**
     * Retrieve Enchantment.
     * <p>
     * Notes: the position of the enchantment on the enchantment table window, starting with 0 as the topmost one.
     * @return The current Enchantment
     */
    public int getEnchantment() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Enchantment.
     * @param value - new value.
     */
    public void setEnchantment(int value) {
        handle.getIntegers().write(1, value);
    }
    
}

