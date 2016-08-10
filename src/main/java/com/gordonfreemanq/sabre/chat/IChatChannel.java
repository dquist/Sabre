package com.gordonfreemanq.sabre.chat;

import com.gordonfreemanq.sabre.SabrePlayer;

/**
 * Represents a chat channel
 * @author GFQ
 *
 */
public interface IChatChannel {
	public void chat(SabrePlayer sender, String msg);
	public void chatMe(SabrePlayer sender, String msg);
}
