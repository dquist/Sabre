package com.comphenix.packetwrapper;

import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientSetCreativeSlot extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.SET_CREATIVE_SLOT;
    
    public WrapperPlayClientSetCreativeSlot() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientSetCreativeSlot(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Slot.
     * <p>
     * Notes: inventory slot
     * @return The current Slot
     */
    public int getSlot() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Slot.
     * @param value - new value.
     */
    public void setSlot(int value) {
        handle.getIntegers().write(0, value);
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

