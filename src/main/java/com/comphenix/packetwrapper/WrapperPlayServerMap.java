package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerMap extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.MAP;
    
    public WrapperPlayServerMap() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerMap(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Item Damage.
     * <p>
     * Notes: the damage value of the map being modified
     * @return The current Item Damage
     */
    public int getItemDamage() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Item Damage.
     * @param value - new value.
     */
    public void setItemDamage(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Scale.
     * @return The current Scale
     */
    public byte getScale() {
        return handle.getBytes().read(0);
    }
    
    /**
     * Set Scale.
     * @param value - new value.
     */
    public void setScale(byte value) {
        handle.getBytes().write(0, value);
    }
    
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
    // Cannot find type for c
}

