package com.gordonfreemanq.sabre;

import com.gordonfreemanq.sabre.SabreAPI;
import com.gordonfreemanq.sabre.chat.IChatChannel;

public class MockSabreAPI extends SabreAPI {
    
	private final DummyChatChannel globalChat;
	
	public MockSabreAPI() {
		globalChat = new DummyChatChannel();
	}
    
	@Override
	public IChatChannel getGlobalChat() {
		return globalChat;
	}

}
