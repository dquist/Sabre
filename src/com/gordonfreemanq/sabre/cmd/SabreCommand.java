package com.gordonfreemanq.sabre.cmd;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.chat.GlobalChat;
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
	protected GroupManager gm;
	protected SabreGroup curGroup;
	protected GlobalChat gc;
	protected BlockManager bm = BlockManager.getInstance();
	protected PearlManager pearls;
	protected SabreConfig config;

	/**
	 * @brief Constructor
	 */
	public SabreCommand()
	{
		super(SabrePlugin.getPlugin());

		this.pm = PlayerManager.getInstance();
		this.gm = GroupManager.getInstance();
		this.bm = BlockManager.getInstance();
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


	public SabreGroup checkGroupExists(String name) {
		
		curGroup = gm.getGroupByName(name);
		
		// Try to find best batch
		if (curGroup == null) {
			curGroup = TextUtil.getBestNamedMatch(gm.getGroups(), name, "");
		}

		// Does the group exist?
		if (curGroup == null) {
			msg(Lang.groupNotExist, name);
		}
		
		return curGroup;
	}
}
