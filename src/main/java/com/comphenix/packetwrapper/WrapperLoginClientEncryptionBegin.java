package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginClientEncryptionBegin extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Client.ENCRYPTION_BEGIN;
    
    public WrapperLoginClientEncryptionBegin() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginClientEncryptionBegin(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Length.
     * <p>
     * Notes: length of shared secret
     * @return The current Length
     */
    public int getLength() {
        return handle.getByteArrays().read(0).length;
    }
    
    /**
     * Retrieve Shared Secret.
     * @return The current Shared Secret
     */
    public byte[] getSharedSecret() {
        return handle.getByteArrays().read(1);
    }
    
    /**
     * Set Shared Secret.
     * @param value - new value.
     */
    public void setSharedSecret(byte[] value) {
        handle.getByteArrays().write(1, value);
    }
    
    // Cannot generate field Length
    // Cannot generate field Verify Token
}

