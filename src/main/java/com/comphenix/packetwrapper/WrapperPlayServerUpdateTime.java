package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

public class WrapperPlayServerUpdateTime extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.UPDATE_TIME;
    
    public WrapperPlayServerUpdateTime() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerUpdateTime(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Age of the world.
     * <p>
     * Notes: in ticks; not changed by server commands
     * @return The current Age of the world
     */
    public long getAgeOfTheWorld() {
        return handle.getLongs().read(0);
    }
    
    /**
     * Set Age of the world.
     * @param value - new value.
     */
    public void setAgeOfTheWorld(long value) {
        handle.getLongs().write(0, value);
    }
    
    /**
     * Retrieve Time of day.
     * <p>
     * Notes: the world (or region) time, in ticks. If negative the sun will stop moving at the Math.abs of the time
     * @return The current Time of day
     */
    public long getTimeOfDay() {
        return handle.getLongs().read(1);
    }
    
    /**
     * Set Time of day.
     * @param value - new value.
     */
    public void setTimeOfDay(long value) {
        handle.getLongs().write(1, value);
    }
    
}

