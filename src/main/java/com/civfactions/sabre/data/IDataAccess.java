package com.civfactions.sabre.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Chunk;

import com.civfactions.sabre.IPlayer;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.blocks.SabreBlock;
import com.civfactions.sabre.groups.SabreGroup;
import com.civfactions.sabre.groups.SabreMember;
import com.civfactions.sabre.prisonpearl.PrisonPearl;
import com.civfactions.sabre.snitch.Snitch;
import com.civfactions.sabre.snitch.SnitchLogEntry;

public interface IDataAccess {
	public void connect() throws Exception;
	public void disconect();
	public boolean isConnected();
	
	// Players
	public Collection<SabrePlayer> playerGetAll();
	public void playerInsert(IPlayer p);
	public void playerUpdateLastLogin(IPlayer p);
	public void playerUpdateAutoJoin(IPlayer p);
	public void playerUpdateFaction(IPlayer p);
	public void playerUpdateName(IPlayer p);
	public void playerUpdateBan(IPlayer p);
	public void playerUpdatePlayTime(IPlayer p);
	public void playerUpdateFreedOffline(IPlayer p);
	public void playerUpdateBed(IPlayer p);
	public void playerDelete(IPlayer p);
	public void playerAddOfflineMessage(IPlayer p, String message);
	public void playerClearOfflineMessages(IPlayer p);
	
	
	// Groups
	public Collection<SabreGroup> groupGetAll();
	public void groupInsert(SabreGroup g);
	public void groupAddInvited(SabreGroup g, UUID id);
	public void groupRemoveInvited(SabreGroup g, UUID id);
	public void groupAddMember(SabreGroup g, SabreMember m);
	public void groupRemoveMember(SabreGroup g, SabreMember m);
	public void groupUpdateMemberRank(SabreGroup g, SabreMember m);
	public void groupUpdateName(SabreGroup g, String n);
	public void groupDelete(SabreGroup g);
	
	
	// Blocks
	public Collection<SabreBlock> blockGetChunkRecords(Chunk c);
	public Collection<SabreBlock> blockGetRunningFactories();
	public void blockInsert(SabreBlock b);
	public void blockRemove(SabreBlock b);
	public void blockSetReinforcement(SabreBlock b);
	public void blockUpdateReinforcementStrength(SabreBlock b);
	public void blockSetSettings(SabreBlock b);
	
	
	// Snitches
	public ArrayList<SnitchLogEntry> snitchGetEntries(Snitch snitch);
	public void snitchMakeLog(SnitchLogEntry e);
	public void snitchClear(Snitch snitch);
	
	
	// Pearls
	public Collection<PrisonPearl> pearlGetall();
	public void pearlInsert(PrisonPearl pp);
	public void pearlUpdate(PrisonPearl pp);
	public void pearlUpdateSummoned(PrisonPearl pp);
	public void pearlUpdateReturnLocation(PrisonPearl pp);
	public void pearlUpdateSealStrength(PrisonPearl pp);
	public void pearlRemove(PrisonPearl pp);
	
	
}
