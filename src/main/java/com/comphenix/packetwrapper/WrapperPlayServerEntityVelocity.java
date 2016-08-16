package com.comphenix.packetwrapper;

import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerEntityVelocity extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.ENTITY_VELOCITY;
    
    public WrapperPlayServerEntityVelocity() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerEntityVelocity(PacketContainer packet) {
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
    
    public Vector getVelocity() {
    	return handle.getVectors().read(0);
    }

    public void setVelocity(Vector value) {
    	handle.getVectors().write(0, value);
    }
    
}

