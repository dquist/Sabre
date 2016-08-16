package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerResourcePackSend extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.RESOURCE_PACK_SEND;
    
    public WrapperPlayServerResourcePackSend() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerResourcePackSend(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve URL.
     * <p>
     * Notes: the URL to the resource pack.
     * @return The current URL
     */
    public String getUrl() {
        return handle.getStrings().read(0);
    }
    
    /**
     * Set URL.
     * @param value - new value.
     */
    public void setUrl(String value) {
        handle.getStrings().write(0, value);
    }
    
    /**
     * Retrieve Hash.
     * <p>
     * Notes: a 40 character hexadecimal and lower-case SHA-1 hash of the resource pack file. (must be lower case in order to work) If it's not a 40 character hexadecimal string, the client will not use it for hash verification and likely waste bandwidth - but it will still treat it as a unique id
     * @return The current Hash
     */
    public String getHash() {
        return handle.getStrings().read(1);
    }
    
    /**
     * Set Hash.
     * @param value - new value.
     */
    public void setHash(String value) {
        handle.getStrings().write(1, value);
    }
    
}

