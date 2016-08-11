package com.gordonfreemanq.sabre;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.chat.IChatChannel;

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
