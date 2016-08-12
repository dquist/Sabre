package com.gordonfreemanq.sabre.chat;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;

public class ServerBroadcast implements IChatChannel {

	private final SabrePlugin plugin;
	
	public ServerBroadcast(SabrePlugin plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void chat(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		
		String formatted = SabrePlugin.instance().txt.parse("<gold>## %s: <w>%s", senderName, msg);
		
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
	
	
	@Override
	public void chatMe(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		String formatted = SabrePlugin.instance().txt.parse("<h><it>%s %s", senderName, msg);
		
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
	
	public String chat(String senderName, String msg) {		
		String formatted = SabrePlugin.instance().txt.parse("<gold>## %s: <w>%s", senderName, msg);
		
		for (SabrePlayer p : plugin.getPlayerManager().getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
		return formatted;
	}
}
