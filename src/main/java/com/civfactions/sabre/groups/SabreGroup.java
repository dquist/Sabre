package com.civfactions.sabre.groups;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.NullArgumentException;

import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.chat.IChatChannel;
import com.civfactions.sabre.util.INamed;

/**
 * Representation of a Sabre group
 * @author GFQ
 */
public class SabreGroup implements INamed, IChatChannel {

	private final UUID id;
	private String name;
	private final HashSet<SabreMember> members;
	
	// Players who have been invited to the group
	private final HashSet<UUID> invited;
	
	// Players who have muted chat from this group - non-persistent 
	private final HashSet<SabrePlayer> mutedFromChat;

	// Players who have muted snitch reports from this group - non-persistent 
	private final HashSet<SabrePlayer> mutedFromSnitch;

	/**
	 * Constructor
	 * @param name The group name
	 * @param owner The group owner
	 */
	public SabreGroup(UUID id, String name) {
		if (id == null) {
			throw new NullArgumentException("id");
		}
		
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
		this.id = id;
		this.name = name;
		this.members = new HashSet<SabreMember>();

		this.invited = new HashSet<UUID>();
		this.mutedFromChat = new HashSet<SabrePlayer>();
		this.mutedFromSnitch = new HashSet<SabrePlayer>();
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
	 * Gets the full name of the group with the owner
	 * @return The full name of the group in the format GROUP#OWNER
	 */
	public String getFullName() {
		return String.format("%s#%s", this.name, getOwner().getName());
	}


	/**
	 * Gets whether the group is a faction
	 * @return true if the group is a faction
	 */
	public boolean isFaction() {
		return false;
	}


	/**
	 * Sets the name of the group
	 * @param name The new name of the group
	 */
	public void setName(String name) {
		if (name == null) {
			throw new NullArgumentException("name");
		}
		
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
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
	 * @throws Exception 
	 */
	public SabreMember addMember(SabrePlayer player, Rank rank) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		if (rank == null) {
			throw new NullArgumentException("rank");
		}
		
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
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
		if (member == null) {
			throw new NullArgumentException("member");
		}
		
		members.remove(member);
		return member;
	}


	/**
	 * Adds a new invited player
	 * @param uid The player ID to invite
	 */
	public void addInvited(UUID uid) {
		if (uid == null) {
			throw new NullArgumentException("uid");
		}
		
		invited.add(uid);
	}


	/**
	 * Adds a new invited player
	 * @param player The player to invite
	 */
	public void addInvited(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		this.addInvited(player.getID());
	}

	/**
	 * Removes an invited player
	 * @param uid The player id to un-invite
	 */
	public void removeInvited(UUID uid) {
		if (uid == null) {
			throw new NullArgumentException("uid");
		}
		
		invited.remove(uid);
	}


	/**
	 * Removes an invited player
	 * @param player The player to un-invite
	 */
	public void removeInvited(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		removeInvited(player.getID());
	}


	/**
	 * Gets whether a player has been invited or not
	 * @param player The player to check
	 * @return true if the player has been invited to join this group
	 */
	public boolean isInvited(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return invited.contains(player.getID());
	}

	
	/**
	 * Gets whether a player belongs to a group
	 * @param player The player to check
	 * @return true if the player is at least a member of the group
	 */
	public boolean isMember(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return getMember(player) != null;
	}
	
	
	/**
	 * Gets whether a player can build on a group
	 * @param player The player to check
	 * @return true if the player is at least a builder of the group
	 */
	public boolean isBuilder(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return this.isRank(player, Rank.BUILDER);
	}
	
	
	/**
	 * Gets whether a player is an officer
	 * @param player The player to check
	 * @return true if the player is at least a group officer
	 */
	public boolean isOfficer(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		return this.isRank(player, Rank.OFFICER);
	}
	
	
	/**
	 * Gets whether is a particular rank
	 * @param player The player to check
	 * @return true if the player is at least a particular rank
	 */
	public boolean isRank(SabrePlayer player, Rank rank) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		if (rank == null) {
			throw new NullArgumentException("rank");
		}
		
		for (SabreMember m : members) {
			if (m.getID().equals(player.getID())) {
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
	 * @return The group owner
	 */
	public SabrePlayer getOwner() {
		return members.stream().filter(m -> m.getRank() == Rank.OWNER).map(p -> p.getPlayer()).findFirst().orElse(null);
	}


	/**
	 * Sets the owner of the group
	 * @param player The group owner
	 */
	public void setOwner(SabrePlayer player) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
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
	
	/**
	 * Messages all the members in the group
	 * @param str The message
	 * @param isChat true if it is a chat message
	 */
	public void msgAll(String str, boolean isChat) {
		if (str == null) {
			throw new NullArgumentException("str");
		}
		
		for (SabreMember m : members) {
			SabrePlayer p = m.getPlayer();
			if (p.isOnline()) {
				if (isChat && mutedFromChat.contains(p)) {
					continue; // ignore muted players if chat
				}
				p.getPlayer().sendMessage(str);
			}
		}
	}


	public void msgAll(String str, boolean isChat, Object... args) {		
		String formatStr = SabrePlugin.instance().txt().parse(str, args);
		msgAll(formatStr, isChat);
	}

	public void msgAllBut(SabrePlayer player, String str, Object... args) {
		if (player == null) {
			throw new NullArgumentException("player");
		}
		
		if (str == null) {
			throw new NullArgumentException("str");
		}
		
		String formatStr = SabrePlugin.instance().txt().parse(str, args);

		for (SabreMember m : members) {
			SabrePlayer p = m.getPlayer();
			if (p.isOnline() && p != player) {
				p.getPlayer().sendMessage(formatStr);
			}
		}
	}


	@Override
	public void chat(SabrePlayer sender, String msg) {
		String formatStr = SabrePlugin.instance().txt().parse("<c>[%s] <reset><gold>%s:<w> %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr, true);
		SabrePlugin.log(Level.INFO, formatStr);
	}
	
	@Override
	public void chatMe(SabrePlayer sender, String msg) {		
		String formatStr = SabrePlugin.instance().txt().parse("<c>[%s] <gold><it>%s %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr, true);
		SabrePlugin.log(Level.INFO, formatStr);
	}
	
	
	/**
	 * Messages a snitch report
	 * @param str The snitch message
	 * @param args
	 */
	public void msgAllSnitch(String str, Object... args) {
		if (str == null) {
			throw new NullArgumentException("str");
		}
		
		String formatStr = SabrePlugin.instance().txt().parse(str, args);
		
		for (SabreMember m : members) {
			SabrePlayer p = m.getPlayer();
			if (p.isOnline() && !mutedFromSnitch.contains(p)) {
				p.getPlayer().sendMessage(formatStr);
			}
		}
	}
	
	
	/**
	 * Mutes the group chat for a player
	 * @param sp The player that is muting the group
	 */
	public void setChatMutedBy(SabrePlayer sp, boolean muted) {
		if (sp == null) {
			throw new NullArgumentException("sp");
		}
		
		if (muted && !mutedFromChat.contains(sp)) {
			mutedFromChat.add(sp);
		} else {
			mutedFromChat.remove(sp);
		}
	}
	
	
	/**
	 * Gets whether the group chat is muted by a player
	 * @param sp The player to check
	 * @return true if the group is muted by the player
	 */
	public boolean isChatMutedBy(SabrePlayer sp) {
		if (sp == null) {
			throw new NullArgumentException("sp");
		}
		
		return this.mutedFromChat.contains(sp);
	}
	
	
	/**
	 * Mutes the group chat for a player
	 * @param sp The player that is muting the group
	 */
	public void setSnitchMutedBy(SabrePlayer sp, boolean muted) {
		if (sp == null) {
			throw new NullArgumentException("sp");
		}
		
		if (muted && !mutedFromSnitch.contains(sp)) {
			mutedFromSnitch.add(sp);
		} else {
			mutedFromSnitch.remove(sp);
		}
	}
	
	
	/**
	 * Gets whether the group snitches are muted by a player
	 * @param sp The player to check
	 * @return true if the group snitches are muted by the player
	 */
	public boolean isSnitchMutedBy(SabrePlayer sp) {
		if (sp == null) {
			throw new NullArgumentException("sp");
		}
		
		return this.mutedFromSnitch.contains(sp);
	}
}
