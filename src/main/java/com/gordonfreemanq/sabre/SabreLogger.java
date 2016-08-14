package com.gordonfreemanq.sabre;

import java.util.logging.Level;

import org.bukkit.Bukkit;

public class SabreLogger {
	
	private final SabrePlugin plugin;
	
	public SabreLogger(SabrePlugin plugin) {
		this.plugin = plugin;
	}
	
	public void log(Object msg) {
		log(Level.INFO, msg);
	}

	public void log(String str, Object... args) {
		log(Level.INFO, plugin.txt().parse(str, args));
	}

	public void log(Level level, String str, Object... args) {
		log(level, plugin.txt().parse(str, args));
	}

	public void log(Level level, Object msg) {
		Bukkit.getLogger().log(level, String.format("[%s] %s", plugin.getDescription().getFullName(), msg));
	}
}
