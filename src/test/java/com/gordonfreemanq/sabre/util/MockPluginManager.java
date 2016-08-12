package com.gordonfreemanq.sabre.util;

import static org.mockito.Mockito.mock;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mockito.Mockito;

public abstract class MockPluginManager implements PluginManager {
	
	private TestFixture fixture;

	public static MockPluginManager create(TestFixture fixture) {
		MockPluginManager manager = mock(MockPluginManager.class, Mockito.CALLS_REAL_METHODS);
		manager.fixture = fixture;
		return manager;
	}
	
	@Override
    public Plugin getPlugin(String name) {
		if (name == "Sabre") {
			return fixture.getPlugin();
		} else {
			return null;
		}
	}

    @Override
    public Plugin[] getPlugins() {
    	JavaPlugin[] plugins = new JavaPlugin[] { fixture.getPlugin() };
    	return plugins;
    }
    
    @Override
    public Permission getPermission(String name) {
    	return  null;
    }
}
