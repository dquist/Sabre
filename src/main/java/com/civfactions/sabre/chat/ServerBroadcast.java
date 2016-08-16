package com.civfactions.sabre.chat;

import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;

public class ServerBroadcast implements IChatChannel {

	private final PlayerManager pm;
	
	public ServerBroadcast(PlayerManager pm) {
		this.pm = pm;
	}
	
	
	@Override
	public void chat(IPlayer sender, String msg) {
		String senderName = sender.getName();
		
		String formatted = SabrePlugin.instance().txt().parse("<gold>## %s: <w>%s", senderName, msg);
		
		for (IPlayer p : pm.getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
	
	
	@Override
	public void chatMe(IPlayer sender, String msg) {
		String senderName = sender.getName();
		String formatted = SabrePlugin.instance().txt().parse("<h><it>%s %s", senderName, msg);
		
		for (IPlayer p : pm.getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
	}
	
	public String chat(String senderName, String msg) {		
		String formatted = SabrePlugin.instance().txt().parse("<gold>## %s: <w>%s", senderName, msg);
		
		for (IPlayer p : pm.getOnlinePlayers()) {
			p.getPlayer().sendMessage(formatted);
		}
		return formatted;
	}
}
