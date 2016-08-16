package com.civfactions.sabre.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.PlayerManager;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.BlockManager;
import com.civfactions.sabre.blocks.BuildMode;
import com.civfactions.sabre.blocks.BuildState;
import com.civfactions.sabre.blocks.Reinforcement;
import com.civfactions.sabre.blocks.SabreBlock;
import com.civfactions.sabre.blocks.SignCollection;
import com.civfactions.sabre.customitems.SecureSign;
import com.civfactions.sabre.data.IDataAccess;
import com.civfactions.sabre.util.Guard;

public class GroupManager {

	private final SabrePlugin plugin;
	private final PlayerManager pm;
	private final BlockManager bm;
	private final IDataAccess db;
	
	private final HashMap<UUID, SabreGroup> groups;
	
	/**
	 * Creates a new GroupManager instance
	 * @param pm The player manager
	 * @param db The database
	 */
	public GroupManager(SabrePlugin plugin, PlayerManager pm, BlockManager bm, IDataAccess db) {
		Guard.ArgumentNotNull(plugin, "plugin");
		Guard.ArgumentNotNull(pm, "pm");
		Guard.ArgumentNotNull(bm, "bm");
		Guard.ArgumentNotNull(db, "db");
		
		this.plugin = plugin;
		this.pm = pm;
		this.bm = bm;
		this.db = db;
		
		this.groups = new HashMap<UUID, SabreGroup>();
	}
	
	/**
	 * Loads all the group data from the database
	 */
	public void load() {
		this.groups.clear();
		
		for (SabreGroup g : db.groupGetAll()) {
			this.groups.put(g.getID(), g);
		}
	}
	
	
	/**
	 * Gets all the groups
	 * @return A collection of all the groups
	 */
	public Collection<SabreGroup> getAllGroups() {
		return groups.values();
	}
	
	
	/**
	 * Gets all the groups a player is on
	 * @return A collection of all the groups the player is on
	 */
	public Collection<SabreGroup> getPlayerGroups(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return groups.values().stream().filter(g -> g.isMember(player)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Gets all the groups that a player is invited to
	 * @return A collection of all the the player is invited to
	 */
	public Collection<SabreGroup> getInvitedGroups(SabrePlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return groups.values().stream().filter(g -> g.isInvited(player)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Gets a group by id
	 * @param id The group id
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByID(UUID uid) {
		Guard.ArgumentNotNull(uid, "uid");
		
		return groups.get(uid);
	}
	
	
	/**
	 * Gets a player's group by name
	 * @param owner The group owner
	 * @param name The name of the group
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByName(SabrePlayer owner, String name) {
		Guard.ArgumentNotNull(owner, "owner");
		Guard.ArgumentNotNull(name, "name");
		
		for (SabreGroup g : groups.values()) {
			if (g.getName().equalsIgnoreCase(name) && g.getOwner().getID().equals(owner.getID())) {
				return g;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a faction by name
	 * @param name The name of the faction
	 * @return The faction instance if it exists, otherwise null
	 */
	public SabreFaction getFactionByName(String name) {
		Guard.ArgumentNotNull(name, "name");
		
		for (SabreGroup g : groups.values()) {
			if (g.getName().equalsIgnoreCase(name) && g.isFaction() && g instanceof SabreFaction) {
				return (SabreFaction)g;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Removes a group from the manager
	 * @param group The group instance to remove
	 * @return The group instance that was removed
	 */
	public void removeGroup(SabreGroup group) {
		Guard.ArgumentNotNull(group, "group");
		
		this.groups.remove(group.getID());
		this.db.groupDelete(group);
	}
	
	
	/**
	 * Renames a group
	 * @param group The group to rename
	 * @param n The new group name
	 */
	public void renameGroup(SabreGroup group, String name) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
		if (getGroupByName(group.getOwner().getPlayer(), name) != null) {
			throw new RuntimeException(String.format("Tried to rename group '%s' for player %s that already exists.", name, group.getOwner().getName()));
		}
		
		group.setName(name);
		db.groupUpdateName(group, name);
	}
	
	
	/**
	 * Creates a new group instance and adds it to the database
	 * @param owner The group owner
	 * @param name The name of the group
	 * @return The new group instance
	 */
	public SabreGroup createNewGroup(SabrePlayer owner, String name) {
		Guard.ArgumentNotNull(owner, "owner");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
		if (getGroupByName(owner, name) != null) {
			throw new RuntimeException(String.format("Tried to add group '%s' for player %s that already exists.", name, owner.getName()));
		}

		SabreGroup group = new SabreGroup(plugin, UUID.randomUUID(), name);
		SabreMember member = group.addMember(owner, Rank.OWNER);
		member.setRank(Rank.OWNER);
		db.groupInsert(group);
		groups.put(group.getID(), group);
		plugin.logger().log(Level.INFO, "Created new group '%s'", name);
		return group;
	}
	
	
	/**
	 * Creates a new faction instance and adds it to the database
	 * @param owner The faction owner
	 * @param name The name of the faction
	 * @return The new faction instance
	 */
	public SabreFaction createNewFaction(SabrePlayer owner, String name) {
		Guard.ArgumentNotNull(owner, "owner");
		Guard.ArgumentNotNullOrEmpty(name, "name");

		if (getFactionByName(name) != null) {
			throw new RuntimeException(String.format("Tried to add faction '%s' that already exists.", name));
		}
		
		SabreFaction faction = new SabreFaction(plugin, UUID.randomUUID(), name);
		SabreMember member = faction.addMember(owner, Rank.OWNER);
		member.setRank(Rank.OWNER);
		db.groupInsert(faction);
		groups.put(faction.getID(), faction);
		plugin.logger().log(Level.INFO, "Created new faction '%s'", name);
		return faction;
	}
	
	
	/**
	 * Adds a player to a group
	 * @param group The group to add to
	 * @param player The player to add
	 * @return The new member instance
	 */
	public SabreMember addPlayer(SabreGroup group, SabrePlayer player) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNull(player, "player");
		
		SabreMember m = null;
		
		if (group.isMember(player)) {
			throw new RuntimeException(String.format("Tried to add '%s' to group '%s' but player is already member.", player.getName(), group.getName()));
		}
		
		if (group.isFaction() && player.getFaction() != null) {
			throw new RuntimeException(String.format("Tried to add '%s' to faction '%s' but player is already in a faction.", player.getName(), group.getName()));
		}

		m = group.addMember(player, Rank.MEMBER);
		db.groupAddMember(group, m);

		if (group instanceof SabreFaction) {
			pm.setFaction(player, (SabreFaction)group);
		}
		
		updateGroupSigns(group, player);
		
		return m;
	}
	
	
	/**
	 * Removes a player from a group
	 * @param group The group to add to
	 * @param player The player to add
	 * @return The new member instance
	 */
	public SabreMember removePlayer(SabreGroup group, SabrePlayer player) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNull(player, "player");
		
		SabreMember m = null;
		
		if (!group.isMember(player)) {
			throw new RuntimeException(String.format("Tried to remove '%s' from group '%s' but player is not a member.", player.getName(), group.getName()));
		}
		
		m = group.removePlayer(player);
		db.groupRemoveMember(group,  m);
		
		if (group instanceof SabreFaction) {
			pm.setFaction(player, null);
		}
		
		checkRemoveChat(group, player);
		checkResetBuildMode(group, player);
		updateGroupSigns(group, player);
		
		return m;
	}
	
	
	/**
	 * Invites a player to a group
	 * @param group The group to add to
	 * @param player The player to add
	 */
	public void invitePlayer(SabreGroup group, SabrePlayer player) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNull(player, "player");
		
		if (!group.isMember(player) && !group.isInvited(player)) {
			group.addInvited(player);
			db.groupAddInvited(group, player.getID());
		}
	}
	
	
	/**
	 * Uninvites a player from a group
	 * @param group The group to add to
	 * @param player The player to remove
	 */
	public void uninvitePlayer(SabreGroup group, SabrePlayer player) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNull(player, "player");
		
		if (group.isInvited(player)) {
			group.removeInvited(player);
			db.groupRemoveInvited(group, player.getID());
		}
	}
	
	
	/**
	 * Sets a new rank for a member
	 * @param member The member
	 * @param rank The new rank
	 */
	public void setPlayerRank(SabreMember member, Rank rank) {
		Guard.ArgumentNotNull(member, "member");
		Guard.ArgumentNotNull(rank, "rank");
		
		if (!groups.values().contains(member.getGroup())) {
			throw new RuntimeException(String.format("Tried to set the rank of non-registered group '%s'.", member.getGroup().getName()));
		}
		
		if (!member.getGroup().isMember(member.getPlayer())) {
			throw new RuntimeException(String.format("Tried to set the rank of a non group member."));
		}
		
		member.setRank(rank);
		db.groupUpdateMemberRank(member.getGroup(), member);
	}
	
	
	/**
	 * Removes a player from the chat group
	 * @param group The group to remove from
	 * @param player The removed player
	 */
	private void checkRemoveChat(SabreGroup group, SabrePlayer player) {
		Guard.ArgumentNotNull(group, "group");
		Guard.ArgumentNotNull(player, "player");
		
		// Remove from group chat
		if(player.getChatChannel().equals(group)) {
			player.moveToGlobalChat();
			player.msg(Lang.chatMovedGlobal);
		}
	}
	
	
	/**
	 * Resets the build mode for a removed player
	 * @param group The group
	 * @param player The removed player
	 */
	private void checkResetBuildMode(SabreGroup group, SabrePlayer player) {		
		// Reset the build mode for the player
		BuildState state = player.getBuildState();
		SabreGroup buildGroup = state.getGroup();
		if (buildGroup != null && buildGroup.equals(group) && !state.equals(BuildMode.OFF)) {
			state.reset();
			player.getBuildState().setMode(BuildMode.OFF);
			player.msg(Lang.blockBuildMode, BuildMode.OFF);
		}
	}
	
	
	/**
	 * Updates all the group signs for a player
	 * @param group The group for the signs
	 * @param player The player to update
	 */
	private void updateGroupSigns(SabreGroup group, SabrePlayer player) {		
		if (!player.isOnline()) {
			return;
		}
		
		Chunk chunk = player.getPlayer().getLocation().getChunk();
		World w = chunk.getWorld();
		Chunk c;
		SecureSign s;
		Reinforcement r;
		
		SignCollection signs = bm.getSigns();
		
		for (int i = -4; i <= 4; i++) {
			for (int j = -4; j <= 4; j++) {
				int x = chunk.getX() + i;
				int z = chunk.getZ() + j;
				
				// If chunk is loaded, update all the signs on the group
				c = w.getChunkAt(x, z);
				if (c.isLoaded()) {
					
					for (SabreBlock b : signs.getChunkBlocks(c)) {
						s = (SecureSign)b;
						r = b.getReinforcement();
						
						if (r != null && r.getGroup().equals(group)) {
							s.updatefor(player);
						}
					}
				}
			}
		}
	}
}
