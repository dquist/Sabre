package com.comphenix.packetwrapper;

import java.util.List;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;

public class WrapperPlayServerExplosion extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.EXPLOSION;

    public WrapperPlayServerExplosion() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerExplosion(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve X.
     * @return The current X
     */
    public double getX() {
        return handle.getDoubles().read(0);
    }

    /**
     * Set X.
     * @param value - new value.
     */
    public void setX(double value) {
        handle.getDoubles().write(0, value);
    }

    /**
     * Retrieve Y.
     * @return The current Y
     */
    public double getY() {
        return handle.getDoubles().read(1);
    }

    /**
     * Set Y.
     * @param value - new value.
     */
    public void setY(double value) {
        handle.getDoubles().write(1, value);
    }

    /**
     * Retrieve Z.
     * @return The current Z
     */
    public double getZ() {
        return handle.getDoubles().read(2);
    }

    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(double value) {
        handle.getDoubles().write(2, value);
    }

    /**
     * Retrieve Radius.
     * <p>
     * Notes: currently unused in the client
     * @return The current Radius
     */
    public float getRadius() {
        return handle.getFloat().read(0);
    }

    /**
     * Set Radius.
     * @param value - new value.
     */
    public void setRadius(float value) {
        handle.getFloat().write(0, value);
    }

    /**
     * Retrieve Record count.
     * <p>
     * Notes: this is the count, not the size. The size is 3 times this value.
     * @return The current Record count
     */
    public List<BlockPosition> getRecors() {
        return handle.getBlockPositionCollectionModifier().read(0);
    }

    /**
     * Set Record count.
     * @param value - new value.
     */
    public void setRecords(List<BlockPosition> value) {
        handle.getBlockPositionCollectionModifier().write(0, value);
    }

    public float getPlayerVelocityX() {
        return handle.getFloat().read(0);
    }

    public void setPlayerVelocityX(float value) {
        handle.getFloat().write(0, value);
    }

    public float getPlayerVelocityY() {
        return handle.getFloat().read(1);
    }

    public void setPlayerVelocityY(float value) {
        handle.getFloat().write(1, value);
    }

    public float getPlayerVelocityZ() {
        return handle.getFloat().read(2);
    }

    public void setPlayerVelocityZ(float value) {
        handle.getFloat().write(2, value);
    }

}
