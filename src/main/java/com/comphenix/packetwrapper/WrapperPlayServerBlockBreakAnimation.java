package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerBlockBreakAnimation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.BLOCK_BREAK_ANIMATION;
    
    public WrapperPlayServerBlockBreakAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerBlockBreakAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
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
     * Retrieve Location.
     * <p>
     * Notes: block Position
     * @return The current Location
     */
    public BlockPosition getLocation() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Location.
     * @param value - new value.
     */
    public void setLocation(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }
    
    /**
     * Retrieve Destroy Stage.
     * <p>
     * Notes: 0 - 9
     * @return The current Destroy Stage
     */
    public int getDestroyStage() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Destroy Stage.
     * @param value - new value.
     */
    public void setDestroyStage(int value) {
        handle.getIntegers().write(1, value);
    }
    
}

