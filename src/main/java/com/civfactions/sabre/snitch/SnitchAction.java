package com.civfactions.sabre.snitch;

public enum SnitchAction {

    ENTRY("ENTRY"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
	KILL("KILL"),
    BLOCK_PLACE("BLOCK_PLACE"),
    BLOCK_BREAK("BLOCK_BREAK"),
    USED("USED"),
    BUCKET_FILL("BUCKET_FILL"),
    BUCKET_EMPTY("BUCKET_EMPTY"),
    IGNITED("IGNITED");
	
	private final String text;


	
    private SnitchAction(String name) {
        this.text = name;
    }
    
    public static SnitchAction fromString(String text) {
		if (text != null) {
			for (SnitchAction b : SnitchAction.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
    
    
    public static SnitchAction fromInt(int val) {
    	for (SnitchAction b : SnitchAction.values()) {
			if (b.ordinal() == val) {
				return b;
			}
		}
		return null;
	}
}
