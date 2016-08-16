package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerAnimation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ANIMATION;
    
    public WrapperPlayServerAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: player ID
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
     * Retrieve Animation.
     * <p>
     * Notes: animation ID
     * @return The current Animation
     */
    public int getAnimation() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Animation.
     * @param value - new value.
     */
    public void setAnimation(int value) {
        handle.getIntegers().write(1,  value);
    }
    
}

