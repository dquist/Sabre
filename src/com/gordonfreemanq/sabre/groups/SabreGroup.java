package com.gordonfreemanq.sabre.groups;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.core.INamed;

/**
 * Representation of a Sabre group
 * @author GFQ
 */
public class SabreGroup implements INamed, IChatChannel {

	private final UUID id;
	private String name;
	private final HashSet<SabreMember> members;
	private final HashSet<UUID> invited;


	/**
	 * Constructor
	 * @param name The group name
	 * @param owner The group owner
	 */
	public SabreGroup(UUID id, String name) {
		this.id = id;
		this.name = name;
		this.members = new HashSet<SabreMember>();

		this.invited = new HashSet<UUID>();
	}


	/**
	 * Gets the group ID
	 * @return The group ID
	 */
	public UUID getID() {
		return this.id;
	}


	/**
	 * Gets the name of the group
	 * @return The name of the group
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Sets the name of the group
	 * @param name The new name of the group
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets all the group members
	 * @return The collection of group members
	 */
	public Collection<SabreMember> getMembers() {
		return members;
	}


	/**
	 * Gets a group member by player
	 * @param player The player
	 * @return The group member instance
	 */
	public SabreMember getMember(SabrePlayer player) {
		for (SabreMember m : members) {
			if (m.getID().equals(player.getID())) {
				return m;
			}
		}

		return null;
	}


	/**
	 * Adds a new group member
	 * @param player The new player to add
	 */
	public SabreMember addMember(SabrePlayer player, Rank rank) {
		SabreMember member = new SabreMember(this, player, rank);
		members.add(member);
		return member;
	}


	/**
	 * Removes an existing group member
	 * @param player The player to remove
	 * @return The removed member
	 */
	public SabreMember removePlayer(SabrePlayer player) {
		for (SabreMember m : members) {
			if (m.getID().equals(player.getID())) {
				members.remove(m);
				return m;
			}
		}
		return null;
	}


	/**
	 * Removes an existing group member
	 * @param player The member to remove
	 * @return The removed member
	 */
	public SabreMember removeMember(SabreMember member) {
		members.remove(member);
		return member;
	}


	/**
	 * Adds a new invited player
	 * @param id The player ID to invite
	 */
	public void addInvited(UUID id) {
		invited.add(id);
	}


	/**
	 * Adds a new invited player
	 * @param player The player to invite
	 */
	public void addInvited(SabrePlayer p) {
		this.addInvited(p.getID());
	}

	/**
	 * Removes an invited player
	 * @param id The player id to un-invite
	 */
	public void removeInvited(UUID id) {
		invited.remove(id);
	}


	/**
	 * Removes an invited player
	 * @param player The player to un-invite
	 */
	public void removeInvited(SabrePlayer p) {
		removeInvited(p.getID());
	}


	/**
	 * Gets whether a player has been invited or not
	 * @param player The player to check
	 * @return true if the player has been invited to join this group
	 */
	public boolean isInvited(SabrePlayer player) {
		return invited.contains(player.getID());
	}

	
	/**
	 * Gets whether a player belongs to a group
	 * @param player The player to check
	 * @return true if the player is at least a member of the group
	 */
	public boolean isMember(SabrePlayer p) {
		return getMember(p) != null;
	}
	
	
	/**
	 * Gets whether a player can build on a group
	 * @param player The player to check
	 * @return true if the player is at least a builder of the group
	 */
	public boolean isBuilder(SabrePlayer p) {
		return this.isRank(p,  Rank.BUILDER);
	}
	
	
	/**
	 * Gets whether a player is an officer
	 * @param player The player to check
	 * @return true if the player is at least a group officer
	 */
	public boolean isOfficer(SabrePlayer p) {
		return this.isRank(p,  Rank.OFFICER);
	}
	
	
	/**
	 * Gets whether is a particular rank
	 * @param player The player to check
	 * @return true if the player is at least a particular rank
	 */
	public boolean isRank(SabrePlayer p, Rank rank) {
		for (SabreMember m : members) {
			if (m.getID().equals(p.getID())) {
				if (m.getRank().ordinal() >= rank.ordinal()) {
					return true;
				} else
				{
					return false;
				}
			}
		}

		return false;
	}


	/**
	 * Gets the owner of the group
	 * @return
	 */
	public UUID getOwnerID() {
		for (SabreMember m : members) {
			if (m.getRank() == Rank.OWNER) {
				return m.getID();
			}
		}

		// This should never happen
		return null;
	}


	/**
	 * Sets the owner of the group
	 * @param player The group owner
	 */
	public void setOwner(SabrePlayer player) {
		SabreMember owner = null;

		// Get the owner instance
		for (SabreMember m : members) {
			if (m.getRank() == Rank.OWNER) {
				owner = m;
				break;
			}
		}

		// Get the instance of the other member or create it
		SabreMember member = this.getMember(player);
		if (member == null) {
			member = this.addMember(player, Rank.MEMBER);
		}

		// Out with the old, in with the new
		owner.setRank(Rank.ADMIN);
		member.setRank(Rank.OWNER);
	}
	
	
	public void msgAll(String str) {
		for (SabreMember m : members) {
			SabrePlayer p = m.getPlayer();
			if (p.isOnline()) {
				p.getPlayer().sendMessage(str);
			}
		}
	}


	public void msgAll(String str, Object... args) {
		String formatStr = SabrePlugin.getPlugin().txt.parse(str, args);
		msgAll(formatStr);
	}

	public void msgAllBut(SabrePlayer player, String str, Object... args) {
		String formatStr = SabrePlugin.getPlugin().txt.parse(str, args);

		for (SabreMember m : members) {
			SabrePlayer p = m.getPlayer();
			if (p.isOnline() && p != player) {
				p.getPlayer().sendMessage(formatStr);
			}
		}
	}


	@Override
	public void chat(SabrePlayer sender, String msg) {
		String formatStr = SabrePlugin.getPlugin().txt.parse("<c>[%s] <reset><gold>%s:<w> %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr);
		SabrePlugin.getPlugin().log(Level.INFO, formatStr);
	}
	
	@Override
	public void chatMe(SabrePlayer sender, String msg) {
		String formatStr = SabrePlugin.getPlugin().txt.parse("<c>[%s] <gold><it>%s %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr);
		SabrePlugin.getPlugin().log(Level.INFO, formatStr);
	}
}
