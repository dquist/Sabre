package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerWorldBorder extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_BORDER;
    
    public WrapperPlayServerWorldBorder() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerWorldBorder(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    // TODO this
}

