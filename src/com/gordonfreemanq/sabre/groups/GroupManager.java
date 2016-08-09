package com.gordonfreemanq.sabre.groups;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.BuildMode;
import com.gordonfreemanq.sabre.blocks.BuildState;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.blocks.SignCollection;
import com.gordonfreemanq.sabre.chat.GlobalChat;
import com.gordonfreemanq.sabre.core.ISabreLog;
import com.gordonfreemanq.sabre.customitems.SecureSign;
import com.gordonfreemanq.sabre.data.IDataAccess;

public class GroupManager {

	private final IDataAccess db;
	private final ISabreLog logger;
	private final HashMap<UUID, SabreGroup> groups;
	
	private static GroupManager instance;
	
	public static GroupManager getInstance() {
		return instance;
	}
	
	public GroupManager(IDataAccess db, ISabreLog logger) {
		this.db = db;
		this.logger = logger;
		this.groups = new HashMap<UUID, SabreGroup>();
		
		instance = this;
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
		return groups.values().stream().filter(g -> g.isMember(player)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Gets all the groups that a player is invited to
	 * @return A collection of all the the player is invited to
	 */
	public Collection<SabreGroup> getInvitedGroups(SabrePlayer p) {
		return groups.values().stream().filter(g -> g.isInvited(p)).collect(Collectors.toSet());
	}
	
	
	/**
	 * Adds a new group to the group manager
	 * @param owner The group owner
	 * @param group The new group to add
	 */
	public void addGroup(SabrePlayer owner, SabreGroup g) {
		String name = g.getName();
		if (getGroupByName(owner, name) != null) {
			throw new RuntimeException(String.format("Tried to add group '%s' for player %s that already exists.", g.getName(), owner.getName()));
		}
		
		if (g.isFaction() && getFactionByName(name) != null) {
			throw new RuntimeException(String.format("Tried to add faction '%s' that already exists.", g.getName()));
		}
		
		db.groupInsert(g);
		groups.put(g.getID(), g);
		logger.log(Level.INFO, "Added new group '%s'", name);
	}
	
	
	/**
	 * Gets a group by id
	 * @param id The group id
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByID(UUID id) {
		return groups.get(id);
	}
	
	
	/**
	 * Gets a player's group by name
	 * @param owner The group owner
	 * @param name The name of the group
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup getGroupByName(SabrePlayer owner, String name) {
		for (SabreGroup g : groups.values()) {
			if (g.getName().equalsIgnoreCase(name) && g.getOwnerID().equals(owner.getID())) {
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
	public SabreGroup getFactionByName(String name) {
		for (SabreGroup g : groups.values()) {
			if (g.getName().equalsIgnoreCase(name) && g.isFaction()) {
				return g;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Removes a group from the manager
	 * @param name The group instance to remove
	 * @return The group instance that was removed
	 */
	public void removeGroup(SabreGroup g) {
		this.groups.remove(g);
		this.db.groupDelete(g);
	}
	
	
	/**
	 * Renames a group
	 * @param g The group to rename
	 * @param n The new group name
	 */
	public void renameGroup(SabreGroup g, String n) {
		this.groups.remove(g);
		g.setName(n);
		db.groupUpdateName(g, n);
	}
	
	
	/**
	 * Creates a new group instance
	 * @param owner The group owner
	 * @param name The name of the group
	 * @param isFaction Whether the group is a faction group
	 * @return The new group instance
	 */
	public SabreGroup createNewGroup(SabrePlayer owner, String name, boolean isFaction) {
		SabreGroup g = new SabreGroup(UUID.randomUUID(), name, isFaction);
		SabreMember member = g.addMember(owner, Rank.OWNER);
		member.setRank(Rank.OWNER);
		return g;
	}
	
	
	/**
	 * Adds a player to a group
	 * @param g The group to add to
	 * @param p The player to add
	 * @return The new member instance
	 */
	public SabreMember addPlayer(SabreGroup g, SabrePlayer p) {
		SabreMember m = null;
		
		if (!g.isMember(p)) {
			m = g.addMember(p, Rank.MEMBER);
			db.groupAddMember(g, m);
			updateGroupSigns(g, p);
		}
		
		return m;
	}
	
	
	/**
	 * Removes a player from a group
	 * @param g The group to add to
	 * @param p The player to add
	 * @return The new member instance
	 */
	public SabreMember removePlayer(SabreGroup g, SabrePlayer p) {
		SabreMember m = null;
		
		if (g.isMember(p)) {
			m = g.removePlayer(p);
			db.groupRemoveMember(g,  m);
			checkRemoveChat(g, p);
			checkResetBuildMode(g, p);
			updateGroupSigns(g, p);
		}
		
		return m;
	}
	
	
	/**
	 * Invites a player to a group
	 * @param g The group to add to
	 * @param p The player to add
	 */
	public void invitePlayer(SabreGroup g, SabrePlayer p) {
		if (!g.isMember(p) && !g.isInvited(p)) {
			g.addInvited(p);
			db.groupAddInvited(g, p.getID());
		}
	}
	
	
	/**
	 * Uninvites a player from a group
	 * @param g The group to add to
	 * @param p The player to remove
	 */
	public void uninvitePlayer(SabreGroup g, SabrePlayer p) {
		if (g.isInvited(p)) {
			g.removeInvited(p);
			db.groupRemoveInvited(g, p.getID());
		}
	}
	
	
	/**
	 * Sets a new rank for a member
	 * @param m The member
	 * @param r The new rank
	 */
	public void setPlayerRank(SabreMember m, Rank r) {
		m.setRank(r);
		db.groupUpdateMemberRank(m.getGroup(), m);
	}
	
	
	
	private void checkRemoveChat(SabreGroup g, SabrePlayer p) {
		// Remove from group chat
		if(p.getChatChannel().equals(g)) {
			p.setChatChannel(GlobalChat.getInstance());
			p.msg(Lang.chatMovedGlobal);
		}
	}
	
	
	private void checkResetBuildMode(SabreGroup g, SabrePlayer p) {
		// Reset the build mode for the player
		BuildState state = p.getBuildState();
		SabreGroup buildGroup = state.getGroup();
		if (buildGroup != null && buildGroup.equals(g) && !state.equals(BuildMode.OFF)) {
			state.reset();
			p.getBuildState().setMode(BuildMode.OFF);
			p.msg(Lang.blockBuildMode, BuildMode.OFF);
		}
	}
	
	
	/**
	 * Updates all the group signs for a player
	 * @param g The group for the signs
	 * @param p The player to update
	 */
	private void updateGroupSigns(SabreGroup g, SabrePlayer p) {
		if (!p.isOnline()) {
			return;
		}
		
		Chunk chunk = p.getPlayer().getLocation().getChunk();
		World w = chunk.getWorld();
		Chunk c;
		SecureSign s;
		Reinforcement r;
		
		SignCollection signs = BlockManager.getInstance().getSigns();
		
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
						
						if (r != null && r.getGroup().equals(g)) {
							s.updatefor(p);
						}
					}
				}
			}
		}
	}
}
