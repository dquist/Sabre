package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerHeldItemSlot extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.HELD_ITEM_SLOT;
    
    public WrapperPlayServerHeldItemSlot() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerHeldItemSlot(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Slot.
     * <p>
     * Notes: the slot which the player has selected (0-8)
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
    
}

