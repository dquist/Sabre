package com.gordonfreemanq.sabre.chat;

import java.util.logging.Level;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;

public class GlobalChat implements IChatChannel {

	private final SabrePlugin plugin;
	private final SabreConfig config;
	
	
	/**
	 * Creates a new GlobalChat instance
	 * @param plugin The plugin instance
	 */
	public GlobalChat(SabrePlugin plugin, SabreConfig config) {
		this.plugin = plugin;
		this.config = config;
	}


	/**
	 * Gets the global chat radius
	 * @return The global chat radius
	 */
	public int getGlobalChatRadius() {
		return config.ChatRadius;
	}
	
	
	/**
	 * Sets the global chat radius
	 * @param globalChatRadius The new global chat radius
	 */
	public void setChatRadius(int globalChatRadius) {
		config.ChatRadius = globalChatRadius;
	}
	
	
	@Override
	public void chat(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		boolean found = false;
		String formatted = plugin.txt.parse("<w>%s: %s", senderName, msg);
		SabrePlugin.log(Level.INFO, formatted);
		
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			if (p.getDistanceFrom(sender) <= getGlobalChatRadius() && p.getPlayer().getWorld().equals(sender.getPlayer().getWorld())) {
				p.getPlayer().sendMessage(formatted);
				
				if (!p.equals(sender)) {
					found = true;
				}
			}
		}
		
		// So sad :(
		if (!found) {
			sender.msg(Lang.chatNoOneHears);
		}
	}
	
	
	@Override
	public void chatMe(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		boolean found = false;
		String formatted = plugin.txt.parse("<silver><it>%s %s", senderName, msg);
		
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			if (p.getDistanceFrom(sender) <= getGlobalChatRadius() && p.getPlayer().getWorld().equals(sender.getPlayer().getWorld())) {
				p.getPlayer().sendMessage(formatted);
				
				if (!p.equals(sender)) {
					found = true;
				}
			}
		}
		
		// So sad :(
		if (!found) {
			sender.msg(Lang.chatNoOneHears);
		}
	}
}
