package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.nbt.NbtBase;

public class WrapperPlayServerUpdateEntityNbt extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_ENTITY_NBT;
    
    public WrapperPlayServerUpdateEntityNbt() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerUpdateEntityNbt(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Tag.
     * @return The current Tag
     */
    public NbtBase<?> getTag() {
        return handle.getNbtModifier().read(0);
    }
    
    /**
     * Set Tag.
     * @param value - new value.
     */
    public void setTag(NbtBase<?> value) {
        handle.getNbtModifier().write(0, value);
    }
    
}

