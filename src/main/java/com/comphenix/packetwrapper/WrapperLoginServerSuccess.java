package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

public class WrapperLoginServerSuccess extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.SUCCESS;
    
    public WrapperLoginServerSuccess() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerSuccess(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve UUID.
     * @return The current UUID
     */
    public WrappedGameProfile getUuid() {
        return handle.getGameProfiles().read(0);
    }
    
    /**
     * Set UUID.
     * @param value - new value.
     */
    public void setUuid(WrappedGameProfile value) {
        handle.getGameProfiles().write(0, value);
    }
    
    /**
     * Retrieve Username.
     * @return The current Username
     */
    public WrappedGameProfile getUsername() {
        return handle.getGameProfiles().read(0);
    }
    
    /**
     * Set Username.
     * @param value - new value.
     */
    public void setUsername(WrappedGameProfile value) {
        handle.getGameProfiles().write(0, value);
    }
    
}

