package com.civfactions.sabre.groups;

import org.apache.commons.lang.NullArgumentException;

/**
 * Represents the rank of a player in a group
 * @author GFQ
 */
public enum Rank 
{
	MEMBER("MEMBER"),
	BUILDER("BUILDER"),
	OFFICER("OFFICER"),
	ADMIN("ADMIN"),
	OWNER("OWNER");

	private final String text;

	/**
	 * Creates a new Rank instance
	 * @param text The name of the rank
	 */
	private Rank(String text) {
		if (text == null) {
			throw new NullArgumentException("text");
		}
		
		this.text = text;
	}

	/**
	 * Gets the rank instance from a string
	 * @param text The rank name
	 * @return The matching rank or null if no match
	 */
	public static Rank fromString(String text) {
		if (text == null) {
			throw new NullArgumentException("text");
		}
		
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
