package com.gordonfreemanq.sabre.chat;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;

public class GlobalChat implements IChatChannel {

	private final PlayerManager pm;
	private final SabreConfig config;
	private int globalChatRadius;
	
	private static GlobalChat instance;
	
	public static GlobalChat getInstance() {
		return instance;
	}
	
	public GlobalChat(PlayerManager pm, SabreConfig config) {
		this.pm = pm;
		this.config = config;
		this.globalChatRadius = 1000;
		
		instance = this;
	}


	/**
	 * Gets the global chat radius
	 * @return The global chat radius
	 */
	public int getGlobalChatRadius() {
		return this.globalChatRadius;
	}
	
	
	/**
	 * Sets the global chat radius
	 * @param globalChatRadius The new global chat radius
	 */
	public void setChatRadius(int globalChatRadius) {
		this.globalChatRadius = globalChatRadius;
		this.config.ChatRadius = globalChatRadius;
	}
	
	
	@Override
	public void chat(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		boolean found = false;
		String formatted = SabrePlugin.getPlugin().txt.parse("<w>%s: %s", senderName, msg);
		
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			if (p.getDistanceFrom(sender) <= globalChatRadius && p.getPlayer().getWorld().equals(sender.getPlayer().getWorld())) {
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
		String formatted = SabrePlugin.getPlugin().txt.parse("<silver><it>%s %s", senderName, msg);
		
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			if (p.getDistanceFrom(sender) <= globalChatRadius && p.getPlayer().getWorld().equals(sender.getPlayer().getWorld())) {
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
