package com.civfactions.sabre.snitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.data.IDataAccess;

public class ReportReqeust implements Runnable {

	private final IDataAccess db;
	private final PlayerManager pm;
	private final IPlayer p;
	private final Snitch snitch;
	private final int page;
	
	private ArrayList<SnitchLogEntry> entries;
	private List<String> report;
	private LinkedBlockingQueue<SnitchLogEntry> l;
	private List<SnitchLogEntry> reportItems;
	private  String output = "";
	
	public ReportReqeust(IDataAccess db, PlayerManager pm, IPlayer p, Snitch snitch, int page) {
		this.db = db;
		this.pm = pm;
		this.p = p;
		this.snitch = snitch;
		this.page = page;
		this.reportItems = new ArrayList<SnitchLogEntry>();
		this.report = new ArrayList<String>();
		this.l = new LinkedBlockingQueue<SnitchLogEntry>();
	}

	
	
	@Override
	public void run() {
		
		// Get the log records
		entries = db.snitchGetEntries(snitch);
		findDuplicates();
		createInfoLines();
		formatReport();
		
		MessageWriter writer = new MessageWriter(p, output);

		Bukkit.getScheduler().runTask(SabrePlugin.instance(), writer);
	}
	
	
	/**
	 * Finds duplicate entries
	 */
	public void findDuplicates() {
		// Find duplicates entries
		for (SnitchLogEntry entry : entries) {
			
			if (entry.action == SnitchAction.LOGIN 
					|| entry.action == SnitchAction.LOGOUT
					|| entry.action == SnitchAction.KILL) {
				l.clear();
			}
			
			// Try to find similar matches in the last few records
			for (SnitchLogEntry e : l) {
				if (entry.isDuplicate(e)) {
					e.count++;
					e.time = entry.time;
					entry.skip = true;
					break;
				}
			}
			
			if (entry.skip) {
				continue;
			}
			
			l.add(entry);
			
			
			if (l.size() > 10) {
				l.poll();
			}
		}
	}
	
	
	/**
	 * Creates the individual string lines
	 */
	public void createInfoLines() {		
		for (SnitchLogEntry entry : entries) {
			if (!entry.skip) {
				reportItems.add(entry);
			}
		}
		
		// Sort by date
		Collections.sort(reportItems, new DateComparator());
		
		for (SnitchLogEntry entry : reportItems) {
			if (!entry.skip) {
				report.add(createInfoString(entry));
			}
		}
	}
	
	
	/**
	 * Formats the final report
	 * @param entries
	 */
	public void formatReport() {
		
		if (10 * page > report.size()) {
			if (snitch.getSnitchName().isEmpty()) {
				p.msg("<c> * Page %d is empty.", page + 1);
			} else {
				p.msg("<c> * Page %d is empty for <a>%s<c>.", page + 1, snitch.getSnitchName());
			}
			return;
		}
		
		
		String id = " ";
		
		for (String dataEntry : report) {
			if (dataEntry.contains("["))
			{
				String data = ChatColor.stripColor(dataEntry.split("\\[")[0]);
				data = data.split(" ")[data.split(" ").length - 1];
				if (Material.matchMaterial(data) != null)
					if (!id.contains(Material.matchMaterial(data).toString()))
						id += String.format(ChatColor.WHITE + ", $" + ChatColor.RED + "%s " + ChatColor.WHITE + "= " + ChatColor.RED + "%s", Integer.parseInt(data), Material.matchMaterial(data));
			}
		}
		
		id = id.replaceFirst(",", "") + (id.length() > 1 ? "\n" : "");
		

	    output += ChatColor.WHITE + " Snitch Log for " + snitch.getSnitchName() + " "
               + ChatColor.DARK_GRAY + "-----------------------------------".substring(snitch.getDisplayName().length()) + "\n";
	    
	    output += id;
		output += ChatColor.GRAY + String.format("  %s %s %s", ChatFiller.fillString("Name", (double) 22), ChatFiller.fillString("Reason", (double) 22), ChatFiller.fillString("Details", (double) 30)) + "\n";
		
		for (int i = 0; i < 10; i++)
		{
			int offset = (10 * page) + i;
			if (offset < report.size()) {
				output += report.get(offset) + "\n";
			}
		}
		
		output += "\n";
		output += String.format("%s * Page %d ------------------------------------------", ChatColor.DARK_GRAY, page + 1);
	}
	
	
	/**
	 * Creates an info string for a long entry
	 * @param entry The entry to format
	 * @return The format string
	 */
	private String createInfoString(SnitchLogEntry entry) {
		
		String resultString = ChatColor.RED + "Error!";
		try {
			
			String initiator = "?";
			IPlayer p = pm.getPlayerById(entry.player);
        	if (p != null) {
        		initiator = p.getName();
        	}
			
			String victim = "?";
			p = pm.getPlayerById(entry.victim);
        	if (p != null) {
        		victim = p.getName();
        	}

			final String timestamp = new SimpleDateFormat("MM-dd HH:mm").format(entry.time);
			String actionString = "BUG";
	        ChatColor actionColor = ChatColor.WHITE;
	        int actionTextType = 0;
	        final int x = entry.loc.getBlockX();
	        final int y = entry.loc.getBlockY();
	        final int z = entry.loc.getBlockZ();
	        
	        
	        switch(entry.action) {
	        case ENTRY:
	            actionString = "Entry";
	            actionColor = ChatColor.BLUE;
	            actionTextType = 1;
	            break;
	        case LOGIN:
	            actionString = "Login";
	            actionColor = ChatColor.GREEN;
	            actionTextType = 1;
	            break;
	        case LOGOUT:
	            actionString = "Logout";
	            actionColor = ChatColor.GREEN;
	            actionTextType = 1;
	            break;
	        case BLOCK_BREAK:
	            actionString = "Block Break";
	            actionColor = ChatColor.DARK_RED;
	            actionTextType = 2;
	            break;
	        case BLOCK_PLACE:
	            actionString = "Block Place";
	            actionColor = ChatColor.DARK_RED;
	            actionTextType = 2;
	            break;
	        case IGNITED:
	            actionString = "Ignited";
	            actionColor = ChatColor.GOLD;
	            actionTextType = 2;
	            break;
	        case USED:
	            actionString = "Used";
	            actionColor = ChatColor.GREEN;
	            actionTextType = 2;
	            break;
	        case BUCKET_EMPTY:
	            actionString = "Bucket Empty";
	            actionColor = ChatColor.DARK_RED;
	            actionTextType = 2;
	            break;
	        case BUCKET_FILL:
	            actionString = "Bucket Fill";
	            actionColor = ChatColor.GREEN;
	            actionTextType = 2;
	            break;
	        case KILL:
	            actionString = "Killed";
	            actionColor = ChatColor.DARK_RED;
	            actionTextType = 3;
	            break;
	        }
	        
	        String actionText = "";
	        switch(actionTextType) {
	            default:
	            case 0:
	                break;
	            case 1:
	                actionText = timestamp;
	                break;
	            case 2:
	                actionText = String.format("%d [%d %d %d]", entry.material.ordinal(), x, y, z);
	                break;
	            case 3:
	                actionText = victim;
	                break;
	            case 4:
	                actionText = "?";
	                break;
	        }
	        
	        initiator = ChatFiller.fillString(initiator, 20.0);
	        actionString = ChatFiller.fillString(actionString, 20.0);
	        actionText = ChatFiller.fillString(actionText, 20.0);
            final String formatting = "  %s%s %s%s %s%s";
            resultString = String.format(formatting, ChatColor.GOLD, initiator, actionColor, actionString, ChatColor.WHITE, actionText);
	        
	        if (entry.count > 1) {
	        	resultString += String.format("%s[x%d]", ChatColor.LIGHT_PURPLE, entry.count);
	        }
	        
		} catch (Exception ex) {
			
		}

		return resultString;
	}
	
	
	/**
	 * Writes the message to the player in a synchronous thread
	 * @author GFQ
	 */
	private class MessageWriter implements Runnable {

		private final IPlayer p;
		private final String message;
		
		
		public MessageWriter(IPlayer p, String message) {
			this.p = p;
			this.message = message;
		}
		
		@Override
		public void run() {
			p.msg(message);
		}
	}
}
