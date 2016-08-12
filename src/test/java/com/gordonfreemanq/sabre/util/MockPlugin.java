package com.gordonfreemanq.sabre.util;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.gordonfreemanq.sabre.SabrePlugin;

public class MockPlugin extends SabrePlugin {
	
    /**
     * Constructor for unit testing.
     * @deprecated
     */
    @Deprecated
    public MockPlugin(PluginLoader loader, Server server, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, server, description, dataFolder, file);
    }

	public static MockPlugin create(TestFixture fixture) throws Exception {
		
		// Mock plugin loader
		PluginLoader mockLoader = Mockito.mock(PluginLoader.class);
		
		// Mock PDF
        PluginDescriptionFile pdf = PowerMockito.spy(new PluginDescriptionFile("Sabre", "0.1.12", "com.gordonfreemanq.sabre.SabrePlugin"));
        when(pdf.getAuthors()).thenReturn(new ArrayList<String>());
		
		MockPlugin plugin = PowerMockito.spy(new MockPlugin(mockLoader, fixture.getMockServer(), pdf, TestFixture.pluginDirectory, new File(TestFixture.pluginDirectory, "testPluginFile")));
		
		// Put all files in bin/test
		doReturn(TestFixture.pluginDirectory).when(plugin).getDataFolder();
		
        doReturn(true).when(plugin).isEnabled();
        doReturn(Util.logger).when(plugin).getLogger();
        plugin.setServerFolder(TestFixture.serverDirectory);
        
        // Set mock database
        MockDataAccess db = PowerMockito.spy(new MockDataAccess());
        Field field = SabrePlugin.class.getDeclaredField("db");
        field.setAccessible(true);
        field.set(plugin, db);
		
		return plugin;
	}
}
