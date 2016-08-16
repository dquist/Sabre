package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayClientTabComplete extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.TAB_COMPLETE;
    
    public WrapperPlayClientTabComplete() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientTabComplete(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Text.
     * @return The current Text
     */
    public String getText() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Text.
     * @param value - new value.
     */
    public void setText(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Has Position.
     * @return The current Has Position
     */
    public BlockPosition getHasPosition() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Has Position.
     * @param value - new value.
     */
    public void setHasPosition(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }
    
    /**
     * Retrieve Looked at block.
     * <p>
     * Notes: the position of the block being looked at. Only sent if the previous field is true
     * @return The current Looked at block
     */
    public BlockPosition getLookedAtBlock() {
        return handle.getBlockPositionModifier().read(0);
    }
    
    /**
     * Set Looked at block.
     * @param value - new value.
     */
    public void setLookedAtBlock(BlockPosition value) {
        handle.getBlockPositionModifier().write(0, value);
    }
    
}

