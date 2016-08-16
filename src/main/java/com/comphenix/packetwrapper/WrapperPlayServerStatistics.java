package com.comphenix.packetwrapper;

import java.util.Map;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedStatistic;

public class WrapperPlayServerStatistics extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.STATISTICS;
    
    public WrapperPlayServerStatistics() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerStatistics(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    @SuppressWarnings("unchecked")
    public Map<WrappedStatistic, Integer> getStatistics() {
        return handle.getSpecificModifier(Map.class).read(0);
    }

    public void setStatistics(Map<WrappedStatistic, Integer> value) {
        handle.getSpecificModifier(Map.class).write(0, value);
    }
}

