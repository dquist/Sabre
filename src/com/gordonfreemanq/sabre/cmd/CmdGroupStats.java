package com.gordonfreemanq.sabre.cmd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.core.NameComparer;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;


public class CmdGroupStats extends SabreCommand {

	public CmdGroupStats()
	{
		super();
		this.aliases.add("stats");

		this.optionalArgs.put("group", "none");

		this.setHelpShort("Checks stats for yourself or a group");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		if (this.args.size() == 0) {
			getSelfStats();
		} else {
			getGroupStats();
		}
	}
	
	
	/**
	 * Gets stats for the player
	 */
	private void getSelfStats() {
		ArrayList<SabreGroup> owners = new ArrayList<SabreGroup>();
		ArrayList<SabreGroup> admins = new ArrayList<SabreGroup>();
		ArrayList<SabreGroup> officers = new ArrayList<SabreGroup>();
		ArrayList<SabreGroup> builders = new ArrayList<SabreGroup>();
		ArrayList<SabreGroup> members = new ArrayList<SabreGroup>();
		
		
		for (SabreGroup gr: gm.getPlayerGroups(me)) {
			SabreMember m = gr.getMember(me);
			if (m == null) {
				continue;
			}
			
			Rank r = m.getRank();
			if (r.equals(Rank.OWNER)) {
				owners.add(gr);
			} else if (r.equals(Rank.ADMIN)) {
				admins.add(gr);
			} else if (r.equals(Rank.OFFICER)) {
				officers.add(gr);
			} else if (r.equals(Rank.BUILDER)) {
				builders.add(gr);
			} else {
				members.add(gr);
			}
		}
		
		Collections.sort(owners, new NameComparer());
		Collections.sort(admins, new NameComparer());
		Collections.sort(officers, new NameComparer());
		Collections.sort(builders, new NameComparer());
		Collections.sort(members, new NameComparer());
		
		StringBuilder ownerNames = new StringBuilder();
		if(owners.size() > 0) {
			for (SabreGroup m : owners) {
				ownerNames.append(String.format("%s, ", m.getName()));
			}
			ownerNames.setLength(ownerNames.length() - 2);
		}		
		
		StringBuilder adminNames = new StringBuilder();
		if(admins.size() > 0) {
			for (SabreGroup m : admins) {
				adminNames.append(String.format("%s, ", m.getName()));
			}
			adminNames.setLength(adminNames.length() - 2);
		}
		
		StringBuilder officerNames = new StringBuilder();
		if(officers.size() > 0) {
			for (SabreGroup m : officers) {
				officerNames.append(String.format("%s, ", m.getName()));
			}
			officerNames.setLength(officerNames.length() - 2);
		}
		
		StringBuilder builderNames = new StringBuilder();
		if(builders.size() > 0) {
			for (SabreGroup m : builders) {
				builderNames.append(String.format("%s, ", m.getName()));
			}
			builderNames.setLength(builderNames.length() - 2);
		}
		
		StringBuilder memberNames = new StringBuilder();
		if(members.size() > 0) {
			for (SabreGroup m : members) {
				memberNames.append(String.format("%s, ", m.getName()));
			}
			memberNames.setLength(memberNames.length() - 2);
		}
		
		long millis = me.getPlaytime();

		String timePlayed = String.format("%dD %dH %dM", TimeUnit.MILLISECONDS.toDays(millis),
			    TimeUnit.MILLISECONDS.toHours(millis) % TimeUnit.DAYS.toHours(1),
			    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1));
		
		StringBuilder sb = new StringBuilder();
		sb.append(plugin.txt.titleize(String.format("Stats for player <c>%s<g>:", me.getName())) + "\n");
		sb.append(String.format("<a>Player since: <n>%s\n", new SimpleDateFormat("yyyy-MM-dd").format(me.getFirstLogin())));
		sb.append(String.format("<a>Total playtime: <n>%s\n", timePlayed));
		
		sb.append("<l>GROUPS:\n");
		sb.append(String.format("<a>OWNER (%d): <n>%s\n", owners.size(), ownerNames.toString()));
		sb.append(String.format("<a>ADMIN (%d): <n>%s\n", admins.size(), adminNames.toString()));
		sb.append(String.format("<a>OFFICER (%d): <n>%s\n", officers.size(), officerNames.toString()));
		sb.append(String.format("<a>BUILDER (%d): <n>%s\n", builders.size(),  builderNames.toString()));
		sb.append(String.format("<a>MEMBER (%d): <n>%s\n", members.size(), memberNames.toString()));
		
		msg(sb.toString());
	}
	
	
	/**
	 * Gets the stats for a particular group as arg 1
	 */
	private void getGroupStats() {
		String groupName = this.argAsString(0);

		SabreGroup g = checkGroupExists(groupName, true);
		if (g == null) {
			return;
		}
		
		// Get the correct name
		groupName = g.getName();
		
		SabreMember memberMe = g.getMember(me);
		if (memberMe == null || memberMe.getRank().ordinal() < Rank.OFFICER.ordinal()) {
			msg(Lang.noPermission, groupName);
			return;
		}
		
		String ownerName = "null";
		ArrayList<SabreMember> admins = new ArrayList<SabreMember>();
		ArrayList<SabreMember> officers = new ArrayList<SabreMember>();
		ArrayList<SabreMember> builders = new ArrayList<SabreMember>();
		ArrayList<SabreMember> members = new ArrayList<SabreMember>();
		
		
		for (SabreMember m: g.getMembers()) {
			Rank r = m.getRank();
			if (r.equals(Rank.OWNER)) {
				ownerName = m.getName();
			} else if (r.equals(Rank.ADMIN)) {
				admins.add(m);
			} else if (r.equals(Rank.OFFICER)) {
				officers.add(m);
			} else if (r.equals(Rank.BUILDER)) {
				builders.add(m);
			} else {
				members.add(m);
			}
		}
		
		StringBuilder adminNames = new StringBuilder();
		if(admins.size() > 0) {
			for (SabreMember m : admins) {
				adminNames.append(String.format("%s, ", m.getName()));
			}
			adminNames.setLength(adminNames.length() - 2);
		}
		
		StringBuilder officerNames = new StringBuilder();
		if(officers.size() > 0) {
			for (SabreMember m : officers) {
				officerNames.append(String.format("%s, ", m.getName()));
			}
			officerNames.setLength(officerNames.length() - 2);
		}
		
		StringBuilder builderNames = new StringBuilder();
		if(builders.size() > 0) {
			for (SabreMember m : builders) {
				builderNames.append(String.format("%s, ", m.getName()));
			}
			builderNames.setLength(builderNames.length() - 2);
		}
		
		StringBuilder memberNames = new StringBuilder();
		if(members.size() > 0) {
			for (SabreMember m : members) {
				memberNames.append(String.format("%s, ", m.getName()));
			}
			memberNames.setLength(memberNames.length() - 2);
		}
		
		StringBuilder stats = new StringBuilder();
		stats.append(plugin.txt.titleize(String.format("Stats for group <c>%s<g>:", g.getName())) + "\n");
		stats.append(String.format("<a>OWNER (1): <n>%s\n", ownerName));
		stats.append(String.format("<a>ADMIN (%d): <n>%s\n", admins.size(), adminNames));
		stats.append(String.format("<a>OFFICER (%d): <n>%s\n", officers.size(), officerNames));
		stats.append(String.format("<a>BUILDER (%d): <n>%s\n", builders.size(),  builderNames));
		stats.append(String.format("<a>MEMBER (%d): <n>%s\n", members.size(), memberNames));
		
		msg(stats.toString());
	}
}
