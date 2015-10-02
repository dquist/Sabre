package com.gordonfreemanq.sabre.blocks;

public enum BuildMode {
	OFF("OFF"),
	FORTIFY("FORTIFY"),
	REINFORCE("REINFORCE");

	private final String text;

	BuildMode(String text) {
		this.text = text;
	}

	public static BuildMode fromString(String text) {
		if (text != null) {
			for (BuildMode b : BuildMode.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}
