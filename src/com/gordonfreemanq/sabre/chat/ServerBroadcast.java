package com.gordonfreemanq.sabre.chat;

import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;

public class ServerBroadcast implements IChatChannel {

	private final PlayerManager pm;
	
	public ServerBroadcast(PlayerManager pm) {
		this.pm = pm;
	}
	
	
	@Override
	public void chat(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		
		String formatted = SabrePlugin.getPlugin().txt.parse("<gold>[Server] <lime>%s: <w>%s", senderName, msg);
		
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
	
	
	@Override
	public void chatMe(SabrePlayer sender, String msg) {
		String senderName = sender.getName();
		String formatted = SabrePlugin.getPlugin().txt.parse("<h><it>%s %s", senderName, msg);
		
		for (SabrePlayer p : pm.getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
}
