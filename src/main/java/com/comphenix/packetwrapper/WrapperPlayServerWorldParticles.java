package com.comphenix.packetwrapper;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;

public class WrapperPlayServerWorldParticles extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.WORLD_PARTICLES;

    public WrapperPlayServerWorldParticles() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerWorldParticles(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve Particle type.
     * @return The current Particle type
     */
    public Particle getParticleType() {
        return handle.getParticles().read(0);
    }

    /**
     * Set Particle type.
     * @param value - new value.
     */
    public void setParticleType(Particle value) {
        handle.getParticles().write(0, value);
    }

    /**
     * Retrieve Long Distance.
     * <p>
     * Notes: if true, particle distance increases from 256 to 65536.
     * @return The current Long Distance
     */
    public boolean getLongDistance() {
        return handle.getSpecificModifier(boolean.class).read(0);
    }

    /**
     * Set Long Distance.
     * @param value - new value.
     */
    public void setLongDistance(boolean value) {
        handle.getSpecificModifier(boolean.class).write(0, value);
    }

    /**
     * Retrieve X.
     * <p>
     * Notes: x position of the particle
     * @return The current X
     */
    public float getX() {
        return handle.getFloat().read(0);
    }

    /**
     * Set X.
     * @param value - new value.
     */
    public void setX(float value) {
        handle.getFloat().write(0, value);
    }

    /**
     * Retrieve Y.
     * <p>
     * Notes: y position of the particle
     * @return The current Y
     */
    public float getY() {
        return handle.getFloat().read(1);
    }

    /**
     * Set Y.
     * @param value - new value.
     */
    public void setY(float value) {
        handle.getFloat().write(1, value);
    }

    /**
     * Retrieve Z.
     * <p>
     * Notes: z position of the particle
     * @return The current Z
     */
    public float getZ() {
        return handle.getFloat().read(2);
    }

    /**
     * Set Z.
     * @param value - new value.
     */
    public void setZ(float value) {
        handle.getFloat().write(2, value);
    }

    /**
     * Retrieve Offset X.
     * <p>
     * Notes: this is added to the X position after being multiplied by random.nextGaussian()
     * @return The current Offset X
     */
    public float getOffsetX() {
        return handle.getFloat().read(3);
    }

    /**
     * Set Offset X.
     * @param value - new value.
     */
    public void setOffsetX(float value) {
        handle.getFloat().write(3, value);
    }

    /**
     * Retrieve Offset Y.
     * <p>
     * Notes: this is added to the Y position after being multiplied by random.nextGaussian()
     * @return The current Offset Y
     */
    public float getOffsetY() {
        return handle.getFloat().read(4);
    }

    /**
     * Set Offset Y.
     * @param value - new value.
     */
    public void setOffsetY(float value) {
        handle.getFloat().write(4, value);
    }

    /**
     * Retrieve Offset Z.
     * <p>
     * Notes: this is added to the Z position after being multiplied by random.nextGaussian()
     * @return The current Offset Z
     */
    public float getOffsetZ() {
        return handle.getFloat().read(5);
    }

    /**
     * Set Offset Z.
     * @param value - new value.
     */
    public void setOffsetZ(float value) {
        handle.getFloat().write(5, value);
    }

    /**
     * Retrieve Particle data.
     * <p>
     * Notes: the data of each particle
     * @return The current Particle data
     */
    public float getParticleData() {
        return handle.getFloat().read(6);
    }

    /**
     * Set Particle data.
     * @param value - new value.
     */
    public void setParticleData(float value) {
        handle.getFloat().write(6, value);
    }

    /**
     * Retrieve Number of particles.
     * <p>
     * Notes: the number of particles to create
     * @return The current Number of particles
     */
    public int getNumberOfParticles() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set Number of particles.
     * @param value - new value.
     */
    public void setNumberOfParticles(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve Data.
     * <p>
     * Notes: length depends on particle. ICON_CRACK, BLOCK_CRACK, and BLOCK_DUST have lengths of 2, the rest have 0.
     * @return The current Data
     */
    public int[] getData() {
        return handle.getIntegerArrays().read(0);
    }

    /**
     * Set Data.
     * @param value - new value.
     */
    public void setData(int[] value) {
        handle.getIntegerArrays().write(0, value);
    }

}
