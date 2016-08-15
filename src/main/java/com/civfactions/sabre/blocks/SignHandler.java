package com.civfactions.sabre.blocks;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.customitems.SecureSign;
import com.civfactions.sabre.groups.SabreGroup;
import com.comphenix.packetwrapper.WrapperPlayServerUpdateSign;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class SignHandler extends PacketAdapter {
	
	private final SabrePlugin plugin;
	
	/**
	 * Creates a new SignHandler instace
	 * @param pm The player manager
	 * @param bm The block manager
	 */
	public SignHandler(SabrePlugin plugin) {
		super(plugin, PacketType.Play.Server.UPDATE_SIGN);
		this.plugin = plugin;
	}
	

	/**
	 * Handles the send sign packet event
	 */
	@Override
    public void onPacketSending(PacketEvent e) {
		PacketContainer packet = e.getPacket();
		WrapperPlayServerUpdateSign w = new WrapperPlayServerUpdateSign(packet);
		
		Location l = w.getLocation().toVector().toLocation(e.getPlayer().getWorld());
		// Is there a SabreBlock at this location?
		SabreBlock b = plugin.getBlockManager().getBlockAt(l);
		
		if (b == null) {
			return;
		}
		
		Reinforcement r = b.getReinforcement();
		if (r == null) {
			return;
		}
		
		// Are we a SecureSign?
		if (b instanceof SecureSign) {
			SecureSign sign = (SecureSign)b;
			SabrePlayer p = plugin.getPlayerManager().getPlayerById(e.getPlayer().getUniqueId());
			updateSignPacket(w, sign, p);	
		}
	}
	
	
	/**
	 * Updates the state of a sign for all online players
	 * @param sign The sign instance
	 */
	public void updateSign(SecureSign sign) {
		
		Location l = sign.getLocation();
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			
			if (p.getPlayer().getLocation().distance(l) < 64) {
				WrapperPlayServerUpdateSign w = new WrapperPlayServerUpdateSign();
				w.setLocation(new BlockPosition(sign.getLocation().toVector()));
				
				// Update the sign packet and send it off
				updateSignPacket(w, sign, p);
				w.sendPacket(p.getPlayer());
			}
		} 
	}
	
	
	/**
	 * Updates the sign packet to be formatted for the player
	 * @param w The packet wrapper
	 * @param sign The sign instance
	 * @param p The player
	 */
	private void updateSignPacket(WrapperPlayServerUpdateSign w, SecureSign sign, SabrePlayer p) {

		Reinforcement r = sign.getReinforcement();
		if (r == null) {
			return;
		}
		
		BlockState bs = sign.getLocation().getBlock().getState();
		if (!(bs instanceof Sign)) {
			return;
		}
		
		Sign signBlock = (Sign)bs;
		WrappedChatComponent[] lines = new WrappedChatComponent[4];
		
		if (sign.getVisible()) {
			// Sign is visible, just send the data
			for (int i = 0; i < lines.length; i++) {
				lines[i] = WrappedChatComponent.fromText(signBlock.getLine(i));
			}
		} else {
			// Sign is hidden, remove text for those not in the group
			SabreGroup g = r.getGroup();
			
			if (g.isMember(p)) {
				// For group members, the text shows up blue
				for (int i = 0; i < lines.length; i++) {
					String l = ChatColor.DARK_BLUE + signBlock.getLine(i);
					if (l.length() > 15) {
						l = l.substring(0, 15);
					}
					lines[i] = WrappedChatComponent.fromText(l);
				}
			} else {
				// For non-group members, remove the text
				for (int i = 0; i < lines.length; i++) {
					lines[i] = WrappedChatComponent.fromText("");
				}
			}
		}

		w.setLines(lines);
	}
}
