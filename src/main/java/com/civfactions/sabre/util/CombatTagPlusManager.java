package com.civfactions.sabre.util;

import java.util.UUID;

import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CombatTagPlusManager implements CombatInterface {

	private TagManager tagManager;
	private boolean combatTagEnabled_ = false;

	public CombatTagPlusManager() {
	}
	
	@Override
	public void initialize() {
		if(Bukkit.getPluginManager().getPlugin("CombatTagPlus") != null) {
			CombatTagPlus combat = (CombatTagPlus) Bukkit.getPluginManager().getPlugin("CombatTagPlus");
			tagManager = combat.getTagManager();
			combatTagEnabled_ = true;
		}
	}

	@Override
	public boolean tagPlayer(Player player) {
		if (combatTagEnabled_ && tagManager != null) {
			tagManager.tag(player, null);
			return true;
		}
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean tagPlayer(String playerName) {
		Player player = Bukkit.getServer().getPlayer(playerName);
		if (combatTagEnabled_ && tagManager != null && player!=null) {
			tagManager.tag(player, null);
			return true;
		}
		return false;
	}

	@Override
	public Integer remainingSeconds(Player player) {
		if (combatTagEnabled_
				&& tagManager != null
				&& tagManager.isTagged(player.getUniqueId())) {
			long remaining = (tagManager.getTag(player.getUniqueId()).getTagDuration() + 500) / 1000L;
			if (remaining > 0x7FFFFFFF) {
				return null;
			}
			return (int)remaining;
		}
		return null;
	}

	@Override
	public boolean isTagged(UUID player) {
		return tagManager.isTagged(player);
	}
}
