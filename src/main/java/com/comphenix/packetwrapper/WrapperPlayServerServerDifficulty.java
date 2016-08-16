package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;

public class WrapperPlayServerServerDifficulty extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.SERVER_DIFFICULTY;
    
    public WrapperPlayServerServerDifficulty() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerServerDifficulty(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Difficulty.
     * <p>
     * Notes: 0:PEACEFUL, 1:EASY, 2:NORMAL, 3: HARD
     * @return The current Difficulty
     */
    public Difficulty getDifficulty() {
        return handle.getDifficulties().read(0);
    }
    
    /**
     * Set Difficulty.
     * @param value - new value.
     */
    public void setDifficulty(Difficulty value) {
        handle.getDifficulties().write(0, value);
    }
    
}

