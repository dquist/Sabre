package com.gordonfreemanq.sabre.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;

public class CombatTagManager {
	private CombatTagApi combatTagApi_;
    private boolean combatTagEnabled_ = false;

    public CombatTagManager() {
        if(Bukkit.getPluginManager().getPlugin("CombatTag") != null) {
            combatTagApi_ = new CombatTagApi((CombatTag)Bukkit.getPluginManager().getPlugin("CombatTag"));
            combatTagEnabled_ = true;
        }
    }

    public boolean tagPlayer(Player player) {
        if (combatTagEnabled_ && combatTagApi_ != null) {
            combatTagApi_.tagPlayer(player);
            return true;
        }
        return false;
    }

    public Integer remainingSeconds(Player player) {
        if (combatTagEnabled_
                && combatTagApi_ != null
                && combatTagApi_.isInCombat(player.getName())) {
            long remaining = (combatTagApi_.getRemainingTagTime(player.getName()) + 500) / 1000L;
            if (remaining > 0x7FFFFFFF) {
                return null;
            }
            return (int)remaining;
        }
        return null;
    }
}
