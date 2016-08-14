package com.gordonfreemanq.sabre.util;

public enum Permission
{
	ADMIN("admin"),
	MOD("mod"),
	PLAYER("build");
	
	/**
	 * The node string that is referenced for permissions
	 */
	public final String node;
	
	Permission(final String node)
	{
		this.node = "sabre." + node;
	}
}
