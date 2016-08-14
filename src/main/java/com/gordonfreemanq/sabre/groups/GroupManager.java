package com.gordonfreemanq.sabre.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.Chunk;
import org.bukkit.World;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.BuildMode;
import com.gordonfreemanq.sabre.blocks.BuildState;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.blocks.SignCollection;
import com.gordonfreemanq.sabre.customitems.SecureSign;
import com.gordonfreemanq.sabre.data.IDataAccess;

public class GroupManager {

	private final PlayerManager pm;
	private final BlockManager bm;
	
	private final IDataAccess db;
	
	private final HashMap<UUID, SabreGroup> groups;
	
	/**
	 * Creates a new GroupManager instance
	 * @param pm The player manager
	 * @param db The database
	 */
	public GroupManager(PlayerManager pm, BlockManager bm, IDataAccess db) {
		if (pm == null) {
			throw new NullArgumentException("pm");
		}
		
		if (bm == null) {
			throw new NullArgumentException("bm");
		}

		if (db == null) {
			throw new NullArgumentException("db");
		}
		
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
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return groups.values().stream().filter(g -> g.isMember(player)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Gets all the groups that a player is invited to
	 * @return A collection of all the the player is invited to
	 */
	public Collection<SabreGroup> getInvitedGroups(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return groups.values().stream().filter(g -> g.isInvited(player)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Adds a new group to the group manager
	 * @param owner The group owner
	 * @param group The new group to add
	 */
	public void addGroup(SabrePlayer owner, SabreGroup group) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		String name = group.getName();
		if (getGroupByName(owner, name) != null) {
			throw new RuntimeException(String.format("Tried to add group '%s' for player %s that already exists.", group.getName(), owner.getName()));
		}
		
		if (group.isFaction() && getFactionByName(name) != null) {
			throw new RuntimeException(String.format("Tried to add faction '%s' that already exists.", group.getName()));
		}
		
		db.groupInsert(group);
		groups.put(group.getID(), group);
		SabrePlugin.log(Level.INFO, "Added new group '%s'", name);
	}
	
	
	/**
	 * Gets a group by id
	 * @param id The group id
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByID(UUID uid) {
		if (uid == null) {
			throw new NullArgumentException("uid");
		}
		
		return groups.get(uid);
	}
	
	
	/**
	 * Gets a player's group by name
	 * @param owner The group owner
	 * @param name The name of the group
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByName(SabrePlayer owner, String name) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
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
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		this.groups.remove(group);
		this.db.groupDelete(group);
	}
	
	
	/**
	 * Renames a group
	 * @param group The group to rename
	 * @param n The new group name
	 */
	public void renameGroup(SabreGroup group, String name) {
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
		group.setName(name);
		db.groupUpdateName(group, name);
	}
	
	
	/**
	 * Creates a new group instance
	 * @param owner The group owner
	 * @param name The name of the group
	 * @return The new group instance
	 */
	public SabreGroup createNewGroup(SabrePlayer owner, String name) {
		if (owner == null) {
			throw new NullArgumentException("owner");
		}
		
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
		SabreGroup g = new SabreGroup(UUID.randomUUID(), name);
		SabreMember member = g.addMember(owner, Rank.OWNER);
		member.setRank(Rank.OWNER);
		return g;
	}
	
	
	/**
	 * Creates a new faction instance
	 * @param owner The faction owner
	 * @param name The name of the faction
	 * @return The new faction instance
	 */
	public SabreFaction createNewFaction(SabrePlayer owner, String name) {
		SabreFaction g = new SabreFaction(UUID.randomUUID(), name);
		SabreMember member = g.addMember(owner, Rank.OWNER);
		member.setRank(Rank.OWNER);
		return g;
	}
	
	
	/**
	 * Adds a player to a group
	 * @param group The group to add to
	 * @param player The player to add
	 * @return The new member instance
	 */
	public SabreMember addPlayer(SabreGroup group, SabrePlayer player) {
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (member == null) {
			throw new NullArgumentException("member");
		}
		
		if (rank == null) {
			throw new NullArgumentException("rank");
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (group == null) {
			throw new NullArgumentException("group");
		}
		
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
