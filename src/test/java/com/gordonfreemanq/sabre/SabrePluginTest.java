package com.gordonfreemanq.sabre.test;

import org.junit.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.powermock.modules.junit4.PowerMockRunner;

import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.util.TestInstanceCreator;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, SabrePlugin.class, Permission.class, Bukkit.class, PluginDescriptionFile.class })
public class SabrePluginTest {
	
    private TestInstanceCreator creator;
    private SabrePlugin plugin;
    private CommandSender mockCommandSender;
    
	
	@Before
	public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        plugin = creator.getPlugin();
        mockCommandSender = creator.getCommandSender();
	}
	
	@After
	public void tearDown() throws Exception {
		creator.tearDown();
	}
	
	@Test
	public void test() throws Exception {
        // Initialize a fake command
        Command mockCommand = mock(Command.class);
        when(mockCommand.getName()).thenReturn("mv");
		
	}
}
