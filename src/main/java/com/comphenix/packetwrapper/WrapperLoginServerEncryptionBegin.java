package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperLoginServerEncryptionBegin extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Login.Server.ENCRYPTION_BEGIN;
    
    public WrapperLoginServerEncryptionBegin() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperLoginServerEncryptionBegin(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Server ID.
     * <p>
     * Notes: appears to be empty as of 1.7.x
     * @return The current Server ID
     */
    public String getServerId() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set Server ID.
     * @param value - new value.
     */
    public void setServerId(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Length.
     * <p>
     * Notes: length of public key
     * @return The current Length
     */
    /* public int getLength() {
        return handle.getSpecificModifier(PublicKey.class).read(0);
    } */
    
    /**
     * Retrieve Public Key.
     * @return The current Public Key
     */
    public byte[] getPublicKey() {
        return handle.getByteArrays().read(0);
    }
    
    /**
     * Set Public Key.
     * @param value - new value.
     */
    public void setPublicKey(byte[] value) {
        handle.getByteArrays().write(0, value);
    }
    
    // Cannot generate field Length
    // Cannot generate field Verify Token
}

