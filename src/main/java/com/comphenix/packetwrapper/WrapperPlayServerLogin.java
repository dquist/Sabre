package com.comphenix.packetwrapper;

import org.bukkit.WorldType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Difficulty;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;

public class WrapperPlayServerLogin extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.LOGIN;
    
    public WrapperPlayServerLogin() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }
    
    public WrapperPlayServerLogin(PacketContainer packet) {
        super(packet, TYPE);
    }
    
    /**
     * Retrieve Entity ID.
     * <p>
     * Notes: the player's Entity ID
     * @return The current Entity ID
     */
    public int getEntityId() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Entity ID.
     * @param value - new value.
     */
    public void setEntityId(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Gamemode.
     * <p>
     * Notes: 0: survival, 1: creative, 2: adventure. Bit 3 (0x8) is the hardcore flag
     * @return The current Gamemode
     */
    public NativeGameMode getGamemode() {
        return handle.getGameModes().read(0);
    }
    
    /**
     * Set Gamemode.
     * @param value - new value.
     */
    public void setGamemode(NativeGameMode value) {
        handle.getGameModes().write(0, value);
    }
    
    /**
     * Retrieve Dimension.
     * <p>
     * Notes: -1: nether, 0: overworld, 1: end
     * @return The current Dimension
     */
    public int getDimension() {
        return handle.getIntegers().read(0);
    }
    
    /**
     * Set Dimension.
     * @param value - new value.
     */
    public void setDimension(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Difficulty.
     * <p>
     * Notes: 0 thru 3 for Peaceful, Easy, Normal, Hard
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
    
    /**
     * Retrieve Max Players.
     * <p>
     * Notes: used by the client to draw the player list
     * @return The current Max Players
     */
    public int getMaxPlayers() {
        return handle.getIntegers().read(1);
    }
    
    /**
     * Set Max Players.
     * @param value - new value.
     */
    public void setMaxPlayers(int value) {
        handle.getIntegers().write(0, value);
    }
    
    /**
     * Retrieve Level Type.
     * <p>
     * Notes: default, flat, largeBiomes, amplified, default_1_1
     * @return The current Level Type
     */
    public WorldType getLevelType() {
        return handle.getWorldTypeModifier().read(0);
    }
    
    /**
     * Set Level Type.
     * @param value - new value.
     */
    public void setLevelType(WorldType value) {
        handle.getWorldTypeModifier().write(0, value);
    }
    
    /**
     * Retrieve Reduced Debug Info.
     * @return The current Reduced Debug Info
     */
    public boolean getReducedDebugInfo() {
        return handle.getBooleans().read(0);
    }
    
    /**
     * Set Reduced Debug Info.
     * @param value - new value.
     */
    public void setReducedDebugInfo(boolean value) {
        handle.getBooleans().write(0, value);
    }
    
}

