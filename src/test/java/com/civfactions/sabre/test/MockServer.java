package com.civfactions.sabre.test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

public abstract class MockServer implements Server {

	private TestFixture fixture;
	private ItemFactory mockItemFactory;
	private MockScheduler mockScheduler;
	
	public static MockServer create(TestFixture fixture) {
		MockServer server = mock(MockServer.class, Mockito.CALLS_REAL_METHODS);
		server.fixture = fixture;
		server.mockItemFactory = mock(ItemFactory.class);
		server.mockScheduler = MockScheduler.create();
        when(server.mockItemFactory.getItemMeta(any())).thenReturn(PowerMockito.mock(ItemMeta.class));
        Logger.getLogger("Minecraft").setParent(TestFixture.logger);
		return server;
	}
	
	@Override
	public String getName() {
		return "TestBukkit";
	}
	
	@Override
	public Logger getLogger() {
		return TestFixture.logger;
	}
	
	@Override
	public File getWorldContainer() {
		return TestFixture.worldsDirectory;
	}
	
	@Override
	public ItemFactory getItemFactory() {
		return mockItemFactory;
	}

	@Override
    public World getWorld(String name) {
		return fixture.getWorld(name);
    }

	@Override
    public World getWorld(UUID uid) {
		return fixture.getWorld(uid);
    }
	
	@Override
    public List<World> getWorlds() {
		return new ArrayList<World>(fixture.getWorlds());
    }
	
	@Override
	public PluginManager getPluginManager() {
		return fixture.getMockPluginManager();
	}
	
	@Override
	public boolean unloadWorld(String name, boolean save) {
		return true;
	}
	
	@Override
	public World createWorld(WorldCreator creator) {
		return fixture.createMockWorld(creator);
	}
	
	@Override
	public BukkitScheduler getScheduler() {
		return mockScheduler;
	}
	
	@Override
    public String getVersion() {
		return "0.0.0";
	}

	@Override
    public String getBukkitVersion() {
		return "0.0.0";
	}
}
