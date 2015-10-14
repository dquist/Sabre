package com.gordonfreemanq.sabre.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Chunk;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.snitch.SnitchLogEntry;

public interface IDataAccess {
	public void connect() throws Exception;
	public void disconect();
	public boolean isConnected();
	
	// Players
	public Collection<SabrePlayer> playerGetAll();
	public void playerInsert(SabrePlayer p);
	public void playerUpdateLastLogin(SabrePlayer p);
	public void playerUpdateAutoJoin(SabrePlayer p);
	public void playerUpdateName(SabrePlayer p);
	public void playerUpdateBan(SabrePlayer p);
	public void playerUpdatePlayTime(SabrePlayer p);
	public void playerUpdateFreedOffline(SabrePlayer p);
	public void playerUpdateBed(SabrePlayer p);
	public void playerDelete(SabrePlayer p);
	public void playerAddOfflineMessage(SabrePlayer p, String message);
	public void playerClearOfflineMessages(SabrePlayer p);
	
	
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
