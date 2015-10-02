package com.gordonfreemanq.sabre.groups;

public enum Rank 
{
	MEMBER("MEMBER"),
	BUILDER("BUILDER"),
	OFFICER("OFFICER"),
	ADMIN("ADMIN"),
	OWNER("OWNER");

	private final String text;

	Rank(String text) {
		this.text = text;
	}

	public static Rank fromString(String text) {
		if (text != null) {
			for (Rank b : Rank.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}
}
