package com.gordonfreemanq.sabre;

import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.test.MockSabreAPI;

public class SabreAPI {
	
	public static boolean IsUnitTesting;
	
	private static SabreAPI INSTANCE;
	
	protected SabreAPI() { }
    
    
    /**
     * Returns the SabreAPI instance
     * @return The SabreAPI instance
     */
    public static SabreAPI getInstance() {
    	
    	if (INSTANCE == null) {
        	if (IsUnitTesting) {
        		INSTANCE = new MockSabreAPI();
        	} else {
        		INSTANCE = new SabreAPI();
        	}
    	}
    	
        return INSTANCE;
    }
    
    
	/**
	 * Returns the global chat instance
	 * @return The global chat instance
	 */
	public IChatChannel getGlobalChat() {
		return SabrePlugin.getPlugin().getGlobalChat();
	}

}
