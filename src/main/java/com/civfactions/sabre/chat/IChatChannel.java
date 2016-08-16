package com.civfactions.sabre.chat;

import com.civfactions.sabre.IPlayer;

/**
 * Represents a chat channel
 * @author GFQ
 *
 */
public interface IChatChannel {
	public void chat(IPlayer sender, String msg);
	public void chatMe(IPlayer sender, String msg);
}
