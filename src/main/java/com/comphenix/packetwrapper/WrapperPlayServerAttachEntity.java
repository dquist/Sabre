package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerAttachEntity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ATTACH_ENTITY;
    
    public WrapperPlayServerAttachEntity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerAttachEntity(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: entity's ID
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(1, value);
    }
    
    /**
     * Retrieve Vehicle ID.
     * <p>
     * Notes: vechicle's Entity ID
     * @return The current Vehicle ID
     */
    public int getVehicleId() {
        return handle.getIntegers().read(2);
    }
    
    /**
     * Set Vehicle ID.
     * @param value - new value.
     */
    public void setVehicleId(int value) {
        handle.getIntegers().write(2, value);
    }
    
    /**
     * Retrieve Leash.
     * <p>
     * Notes: if true leashes the entity to the vehicle
     * @return The current Leash
     */
    public int getLeash() {
        return  handle.getIntegers().read(0);
    }
    
    /**
     * Set Leash.
     * @param value - new value.
     */
    public void setLeash(int value) {
        handle.getIntegers().write(0, value);
    }
    
}

