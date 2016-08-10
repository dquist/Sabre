package com.gordonfreemanq.sabre.core;

import java.util.logging.Level;

public interface ISabreLog {
	public void log(Object msg);
	public void log(String str, Object... args);
	public void log(Level level, String str, Object... args);
	public void log(Level level, Object msg);
}
