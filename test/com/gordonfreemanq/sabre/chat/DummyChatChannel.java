package com.gordonfreemanq.sabre.chat;

import com.gordonfreemanq.sabre.SabrePlayer;

public class DummyChatChannel implements IChatChannel {

	@Override
	public void chat(SabrePlayer sender, String msg) {
		// Does nothing
		
	}

	@Override
	public void chatMe(SabrePlayer sender, String msg) {
		// Does nothing
	}

}
