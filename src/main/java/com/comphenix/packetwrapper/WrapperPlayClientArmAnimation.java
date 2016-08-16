package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayClientArmAnimation extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.ARM_ANIMATION;
    
    public WrapperPlayClientArmAnimation() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayClientArmAnimation(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    // Cannot generate field null
}

