package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerWorldEvent extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_EVENT;
    
    public WrapperPlayServerWorldEvent() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerWorldEvent(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Effect ID.
     * <p>
     * Notes: the ID of the effect, see below.
     * @return The current Effect ID
     */
    public int getEffectId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Effect ID.
     * @param value - new value.
     */
    public void setEffectId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Location.
     * <p>
     * Notes: the location of the effect
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
     * Retrieve Data.
     * <p>
     * Notes: extra data for certain effects, see below.
     * @return The current Data
     */
    public int getData() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Data.
     * @param value - new value.
     */
    public void setData(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Disable relative volume.
     * <p>
     * Notes: see above
     * @return The current Disable relative volume
     */
    public boolean getDisableRelativeVolume() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }
    
    /**
     * Set Disable relative volume.
     * @param value - new value.
     */
    public void setDisableRelativeVolume(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }
    
}

