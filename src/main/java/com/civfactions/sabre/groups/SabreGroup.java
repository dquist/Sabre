package com.civfactions.sabre.groups;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.chat.IChatChannel;
import com.civfactions.sabre.util.Guard;
import com.civfactions.sabre.util.INamed;

/**
 * Representation of a Sabre group
 * @author GFQ
 */
public class SabreGroup implements INamed, IChatChannel {

	protected final SabrePlugin plugin;
	
	protected final UUID uid;
	protected String name;
	protected final HashSet<SabreMember> members;
	
	// Players who have been invited to the group
	protected final HashSet<UUID> invited;
	
	// Players who have muted chat from this group - non-persistent 
	protected final HashSet<IPlayer> mutedFromChat;

	// Players who have muted snitch reports from this group - non-persistent 
	protected final HashSet<IPlayer> mutedFromSnitch;
	
	private boolean isLocked;

	/**
	 * Constructor
	 * @param name The group name
	 * @param owner The group owner
	 */
	public SabreGroup(SabrePlugin plugin, UUID uid, String name) {
		Guard.ArgumentNotNull(plugin, "plugin");
		Guard.ArgumentNotNull(uid, "uid");
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
		this.plugin = plugin;
		this.uid = uid;
		this.name = name;
		this.members = new HashSet<SabreMember>();

		this.invited = new HashSet<UUID>();
		this.mutedFromChat = new HashSet<IPlayer>();
		this.mutedFromSnitch = new HashSet<IPlayer>();
		this.isLocked = true;
	}


	/**
	 * Gets the group ID
	 * @return The group ID
	 */
	public UUID getID() {
		return this.uid;
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
		Guard.ArgumentNotNullOrEmpty(name, "name");
		
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
	public SabreMember getMember(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
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
	public SabreMember addMember(IPlayer player, Rank rank) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(rank, "rank");
		
		if (this.isMember(player)) {
			throw new RuntimeException(String.format("%s is already a member of %s.", player.getName(), name));
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
	public SabreMember removePlayer(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
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
		Guard.ArgumentNotNull(member, "member");
		
		members.remove(member);
		return member;
	}


	/**
	 * Adds a new invited player
	 * @param uid The player ID to invite
	 */
	public void addInvited(UUID uid) {
		Guard.ArgumentNotNull(uid, "uid");
		
		invited.add(uid);
	}


	/**
	 * Adds a new invited player
	 * @param player The player to invite
	 */
	public void addInvited(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		this.addInvited(player.getID());
	}

	/**
	 * Removes an invited player
	 * @param uid The player id to un-invite
	 */
	public void removeInvited(UUID uid) {
		Guard.ArgumentNotNull(uid, "uid");
		
		invited.remove(uid);
	}


	/**
	 * Removes an invited player
	 * @param player The player to un-invite
	 */
	public void removeInvited(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		removeInvited(player.getID());
	}


	/**
	 * Gets whether a player has been invited or not
	 * @param player The player to check
	 * @return true if the player has been invited to join this group
	 */
	public boolean isInvited(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return invited.contains(player.getID());
	}

	
	/**
	 * Gets whether a player belongs to a group
	 * @param player The player to check
	 * @return true if the player is at least a member of the group
	 */
	public boolean isMember(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return getMember(player) != null;
	}
	
	
	/**
	 * Gets whether a player can build on a group
	 * @param player The player to check
	 * @return true if the player is at least a builder of the group
	 */
	public boolean isBuilder(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return this.isRank(player, Rank.BUILDER);
	}
	
	
	/**
	 * Gets whether a player is an officer
	 * @param player The player to check
	 * @return true if the player is at least a group officer
	 */
	public boolean isOfficer(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		return this.isRank(player, Rank.OFFICER);
	}
	
	
	/**
	 * Gets whether is a particular rank
	 * @param player The player to check
	 * @return true if the player is at least a particular rank
	 */
	public boolean isRank(IPlayer player, Rank rank) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(rank, "rank");
		
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
	public SabreMember getOwner() {
		SabreMember owner = members.stream().filter(m -> m.getRank() == Rank.OWNER).findFirst().orElse(null);
		
		// This is bad...
		if (owner == null) {
			plugin.logger().log(Level.SEVERE, String.format("Failed to find owner for group %s - %s", name, uid.toString()));
		}
		
		return owner;
	}


	/**
	 * Transfers the group to a new owner
	 * @param player The group owner
	 */
	public void transferTo(IPlayer player) {
		Guard.ArgumentNotNull(player, "player");
		
		SabreMember owner = this.getOwner();
		
		if (owner != null && owner.getPlayer() == player) {
			throw new RuntimeException(String.format("%s is already the owner of %s.", player.getName(), name));
		}

		// Get the instance of the other member or create it
		SabreMember member = this.getMember(player);
		if (member == null) {
			member = this.addMember(player, Rank.MEMBER);
		}

		// Out with the old, in with the new
		if (owner != null) {
			isLocked = false; // Allows the group owner to be demoted
			owner.setRank(Rank.ADMIN);
			isLocked = true;
		}
		member.setRank(Rank.OWNER);
	}
	
	/**
	 * Messages all the members in the group
	 * @param str The message
	 * @param isChat true if it is a chat message
	 */
	public void msgAll(String str, boolean isChat) {
		Guard.ArgumentNotNull(str, "str");
		
		for (SabreMember m : members) {
			IPlayer p = m.getPlayer();
			if (!p.isOnline() || (isChat && mutedFromChat.contains(p))) {
				continue; // ignore muted players if chat
			}
			p.msg(str);
		}
	}


	public void msgAll(String str, boolean isChat, Object... args) {		
		String formatStr = plugin.txt().parse(str, args);
		msgAll(formatStr, isChat);
	}

	public void msgAllBut(IPlayer player, String str, Object... args) {
		Guard.ArgumentNotNull(player, "player");
		Guard.ArgumentNotNull(str, "str");

		for (SabreMember m : members) {
			IPlayer p = m.getPlayer();
			if (p.isOnline() && p != player) {
				p.msg(str, args);
			}
		}
	}


	@Override
	public void chat(IPlayer sender, String msg) {
		String formatStr = plugin.txt().parse("<c>[%s] <reset><gold>%s:<w> %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr, true);
		plugin.logger().log(Level.INFO, formatStr);
	}
	
	@Override
	public void chatMe(IPlayer sender, String msg) {		
		String formatStr = plugin.txt().parse("<c>[%s] <gold><it>%s %s", this.getName(), sender.getName(), msg);
		this.msgAll(formatStr, true);
		plugin.logger().log(Level.INFO, formatStr);
	}
	
	
	/**
	 * Messages a snitch report
	 * @param str The snitch message
	 * @param args
	 */
	public void msgAllSnitch(String str, Object... args) {
		Guard.ArgumentNotNull(str, "str");
		
		for (SabreMember m : members) {
			IPlayer p = m.getPlayer();
			if (p.isOnline() && !mutedFromSnitch.contains(p)) {
				p.msg(str, args);
			}
		}
	}
	
	
	/**
	 * Mutes the group chat for a player
	 * @param sp The player that is muting the group
	 */
	public void setChatMutedBy(IPlayer sp, boolean muted) {
		Guard.ArgumentNotNull(sp, "sp");
		
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
	public boolean isChatMutedBy(IPlayer sp) {
		Guard.ArgumentNotNull(sp, "sp");
		
		return this.mutedFromChat.contains(sp);
	}
	
	
	/**
	 * Mutes the group chat for a player
	 * @param sp The player that is muting the group
	 */
	public void setSnitchMutedBy(IPlayer sp, boolean muted) {
		Guard.ArgumentNotNull(sp, "sp");
		
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
	public boolean isSnitchMutedBy(IPlayer sp) {
		Guard.ArgumentNotNull(sp, "sp");
		
		return this.mutedFromSnitch.contains(sp);
	}
	
	/**
	 * Gets the lock for certain modifications
	 * @return true if the group is locked
	 */
	public boolean isLocked() {
		return isLocked;
	}
}
