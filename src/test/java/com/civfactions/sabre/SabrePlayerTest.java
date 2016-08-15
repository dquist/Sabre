package com.civfactions.sabre;

import static org.junit.Assert.*;

import org.bukkit.Server;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.civfactions.sabre.test.MockScheduler;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SabrePlugin.class, Server.class })
public class SabrePlayerTest {
	
	@BeforeClass
	public static void setUpClass() {
	}

	@Test
	public void test() {
	}
}
