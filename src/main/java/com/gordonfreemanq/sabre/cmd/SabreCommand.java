package com.gordonfreemanq.sabre.cmd;

import java.util.Collection;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.chat.IChatChannel;
import com.gordonfreemanq.sabre.core.SabreBaseCommand;
import com.gordonfreemanq.sabre.groups.GroupManager;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.prisonpearl.PearlManager;
import com.gordonfreemanq.sabre.util.TextUtil;

/**
 * Abstract class for a Groups command
 * @author GFQ
 *
 */
public abstract class SabreCommand extends SabreBaseCommand<SabrePlugin>
{
	protected final GroupManager gm;
	protected final BlockManager bm;
	protected final PearlManager pearls;
	protected final SabreConfig config;
	protected SabreGroup curGroup;
	protected IChatChannel gc;

	/**
	 * @brief Constructor
	 */
	public SabreCommand()
	{
		super(SabrePlugin.instance());
		
		this.gm = GroupManager.instance();
		this.bm = BlockManager.instance();
		
		this.gc = plugin.getGlobalChat();
		this.config = plugin.getSabreConfig();
		this.curGroup = null;
		this.pearls = PearlManager.getInstance();
	}



	// PLAYER ======================
	public SabrePlayer strAsPlayer(String name)
	{
		SabrePlayer ret = null;

		if (name != null)
		{
			// Try to get exact player
			ret = pm.getPlayerByName(name);
			
			if (ret == null) {
				
				// Try to get a match from the players in the current command group
				if (curGroup != null) {
					SabreMember m = TextUtil.getBestNamedMatch(curGroup.getMembers(), name, me.getName());
					if (m != null) {
						ret = m.getPlayer();
					}
				}
				
				// If still no match, check online players
				if (ret == null) {
					ret = TextUtil.getBestNamedMatch(pm.getOnlinePlayers(), name, me.getName());
				}
				
				// If still no match, check all players
				if (ret == null) {
					ret = TextUtil.getBestNamedMatch(pm.getPlayers(), name, me.getName());
				}
			}
		}

		return ret;
	}

	public SabrePlayer argAsPlayer(int idx)
	{
		return this.strAsPlayer(argAsString(idx));
	}
	
	/**
	 * Checks if a group exists
	 * @param owner The group owner
	 * @param name The group name
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup checkGroupExists(SabrePlayer owner, String searchName, boolean searchSimilar) {

		String groupName = searchName;
		
		// If the search name contains the '#' character, split up the group name and owner name
		if (searchName.contains("#")) {
			String[] split = searchName.split("#");
			if (split.length > 0) {
				groupName = split[0];
			}
			if (split.length > 1) {
				SabrePlayer searchOwner = pm.getPlayerByName(split[1]);
				if (searchOwner != null) {
					owner = searchOwner;
				}
			}
		}
		
		final String queryName = groupName;
		
		// First try to find the group by owner and name
		curGroup = gm.getGroupByName(owner, groupName);
		
		// If no match, search all the groups that this player is a member of
		if (curGroup == null) {
			Collection<SabreGroup> playerGroups = gm.getPlayerGroups(me);
			curGroup = playerGroups.stream().filter(g -> g.getName().equalsIgnoreCase(queryName)).findFirst().orElse(null);

			// If no exact hit, try to find the best match group that the player is a member of
			if (curGroup == null && searchSimilar) {
				curGroup = TextUtil.getBestNamedMatch(playerGroups, groupName, "");
			}
		}

		// No group found
		if (curGroup == null) {
			msg(Lang.groupNotExist, searchName);
		}
		
		return curGroup;
		
	}

	/**
	 * Checks if a group exists
	 * @param name The group name
	 * @return The group instance if it exists, otherwise null
	 */
	public SabreGroup checkGroupExists(String name, boolean searchSimilar) {
		return checkGroupExists(me, name, searchSimilar);
	}
}
