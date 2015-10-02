package com.gordonfreemanq.sabre.core;

import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;

import com.gordonfreemanq.sabre.util.PermUtil;
import com.gordonfreemanq.sabre.util.TextUtil;


public abstract class AbstractSabrePlugin extends JavaPlugin implements ISabreLog
{
	// Some utils
	public TextUtil txt;
	public PermUtil perm;
	
	// Persist related
	public Gson gson;	
	private Integer saveTask = null;
	private boolean autoSave = true;
	protected boolean loadSuccessful = false;
	public boolean getAutoSave() {return this.autoSave;}
	public void setAutoSave(boolean val) {this.autoSave = val;}
	

	// -------------------------------------------- //
	// ENABLE
	// -------------------------------------------- //
	private long timeEnableStart;
	public boolean preEnable()
	{		
		log("=== ENABLE START ===");
		timeEnableStart = System.currentTimeMillis();
		
		// Ensure basefolder exists!
		this.getDataFolder().mkdirs();
		
		// Create Utility Instances
		this.perm = new PermUtil(this);
		this.gson = this.getGsonBuilder().create();
		
		this.txt = new TextUtil();
		createTags();

		loadSuccessful = true;
		return true;
	}
	
	public void postEnable()
	{
		log("=== ENABLE DONE (Took "+(System.currentTimeMillis()-timeEnableStart)+"ms) ===");
	}
	
	public void onDisable()
	{
		if (saveTask != null)
		{
			this.getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}

		log("Disabled");
	}
	
	public void suicide()
	{
		log("Now I suicide!");
		this.getServer().getPluginManager().disablePlugin(this);
	}
	
	public abstract String GetCommandAlias();

	// -------------------------------------------- //
	// Some inits...
	// You are supposed to override these in the plugin if you aren't satisfied with the defaults
	// The goal is that you always will be satisfied though.
	// -------------------------------------------- //

	public GsonBuilder getGsonBuilder()
	{
		return new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.serializeNulls()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
	}
	
	public void createTags()
	{
		this.txt.tags.put("l", TextUtil.parseColor("<green>"));  // logo
		this.txt.tags.put("a", TextUtil.parseColor("<gold>"));  // art
		this.txt.tags.put("n", TextUtil.parseColor("<silver>"));  // notice
		this.txt.tags.put("i", TextUtil.parseColor("<yellow>"));  // info
		this.txt.tags.put("g", TextUtil.parseColor("<lime>"));  // good
		this.txt.tags.put("b", TextUtil.parseColor("<rose>"));  // bad
		this.txt.tags.put("h", TextUtil.parseColor("<pink>"));  // highligh
		this.txt.tags.put("c", TextUtil.parseColor("<aqua>"));  // parameter
		this.txt.tags.put("p", TextUtil.parseColor("<teal>"));  // parameter
		this.txt.tags.put("w", TextUtil.parseColor("<white>"));  // parameter
		this.txt.tags.put("lp", TextUtil.parseColor("<lpurple>"));
	}
	
	
	// -------------------------------------------- //
	// HOOKS
	// -------------------------------------------- //
	public void preAutoSave()
	{
		
	}
	
	public void postAutoSave()
	{
		
	}
	
	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(Object msg)
	{
		log(Level.INFO, msg);
	}

	public void log(String str, Object... args)
	{
		log(Level.INFO, this.txt.parse(str, args));
	}

	public void log(Level level, String str, Object... args)
	{
		log(level, this.txt.parse(str, args));
	}

	public void log(Level level, Object msg)
	{
		Bukkit.getLogger().log(level, "["+this.getDescription().getFullName()+"] "+msg);
	}
}
