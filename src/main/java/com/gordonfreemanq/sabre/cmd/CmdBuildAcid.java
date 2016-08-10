package com.gordonfreemanq.sabre.cmd;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockIterator;

import com.gordonfreemanq.sabre.Lang;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.customitems.AcidBlock;


public class CmdBuildAcid extends SabreCommand {

	public CmdBuildAcid()
	{
		super();
		this.aliases.add("acid");

		this.setHelpShort("Activates acid blocks");

		senderMustBePlayer = true;
	}

	@Override
	public void perform() 
	{
		SabreBlock sb = null;
		SabreBlock topSb = null;
		Reinforcement r = null;
		Reinforcement topR = null;
		AcidBlock acid = null;
		boolean found = false;
		
		Iterator<Block> itr = new BlockIterator(me.getPlayer(), 40); // Within 2.5 chunks
		while (itr.hasNext()) {
			Block b = itr.next();
			if (b.getType() != AcidBlock.MATERIAL) {
				continue;
			}
			
			sb = bm.getBlockAt(b.getLocation());
			if (sb == null) {
				continue;
			}
			
			if (!(sb instanceof AcidBlock)) {
				me.msg(Lang.acidNotAcidBlock);
				return;
			}
			
			r = sb.getReinforcement();
			if (r == null) {
				me.msg(Lang.acidNotReinforced);
				return;
			}
			
			if (!sb.canPlayerModify(me)) {
				me.msg(Lang.blockNoPermission);
				return;
			}
			
			acid = (AcidBlock)sb;
			if (!acid.isMature()) {
				me.msg(Lang.acidNotMature);
				return;
			}
			
			Block topFace = b.getRelative(BlockFace.UP);
			if (topFace.getType() == Material.AIR) {
				me.msg(Lang.acidNoBlockAbove);
				return;
			}
			
			topSb = bm.getBlockAt(b.getLocation());
			if (topSb == null || topSb.getReinforcement() == null) {
				me.msg(Lang.acidNoReinforcementAbove);
				return;
			}
			
			topR = topSb.getReinforcement();
			if (topR.getGroup().equals(r.getGroup())) {
				me.msg(Lang.acidSameGroup);
				return;
			}
			
			if (r.getStartStrength() > topR.getStartStrength()) {
				me.msg(Lang.acidTooWeak);
				return;
			}
			
			// Good to go, break the block
			bm.removeBlock(topSb);
			topFace.setType(Material.AIR);
			
			bm.removeBlock(sb);
			sb.dropNaturally();
			found = true;
		}
		
		if (!found) {
			me.msg(Lang.acidNotFound);
		}
	}
}
