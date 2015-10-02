package com.gordonfreemanq.sabre.blocks;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.comphenix.packetwrapper.WrapperPlayServerUpdateSign;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.customitems.SecureSign;
import com.gordonfreemanq.sabre.groups.SabreGroup;

public class SignHandler extends PacketAdapter {
	
	private final PlayerManager pm;
	private final BlockManager bm;
	
	
	private static SignHandler instance;
	
	public static SignHandler getInstance() {
		return instance;
	}
	
	public SignHandler() {
		super(SabrePlugin.getPlugin(), PacketType.Play.Server.UPDATE_SIGN);
		
		pm = PlayerManager.getInstance();
		bm = BlockManager.getInstance();
		
		instance = this;
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
		SabreBlock b = bm.getBlockAt(l);
		
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
			SabrePlayer p = pm.getPlayerById(e.getPlayer().getUniqueId());
			updateSignPacket(w, sign, p);	
		}
	}
	
	
	/**
	 * Updates the state of a sign for all online players
	 * @param sign The sign instance
	 */
	public void updateSign(SecureSign sign) {
		
		// Broken
		return;
		
		/*
		Location l = sign.getLocation();
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			
			if (p.getPlayer().getLocation().distance(l) < 64) {
				WrapperPlayServerUpdateSign w = new WrapperPlayServerUpdateSign();
				w.setLocation(new BlockPosition(sign.getLocation().toVector()));
				BlockState bs = sign.getLocation().getBlock().getState();
				
				if (bs instanceof Sign) {
					Sign signBlock = (Sign)bs;
					WrappedChatComponent[] lines = w.getLines();
					for (int i = 0; i < lines.length; i++) {
						lines[i] = WrappedChatComponent.fromText(signBlock.getLine(i));
					}
					
					updateSignPacket(w, sign, p);
					w.sendPacket(p.getPlayer());
				}
			}
		} */
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
		
		// broken
		/*
		if (!sign.getVisible()) {
			// Sign is hidden, remove text for those not in the group
			SabreGroup g = r.getGroup();
			WrappedChatComponent[] lines = w.getLines();
			
			if (g.isMember(p)) {
				// For group members, the text shows up blue
				for (int i = 0; i < lines.length; i++) {
					String l = ChatColor.DARK_BLUE + lines[i].getJson();
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
		} */
	}
}
