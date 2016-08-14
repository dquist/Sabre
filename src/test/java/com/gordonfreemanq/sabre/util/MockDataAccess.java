package com.gordonfreemanq.sabre.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Chunk;

import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.data.IDataAccess;
import com.gordonfreemanq.sabre.factory.BaseFactory;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.snitch.SnitchLogEntry;

public class MockDataAccess implements IDataAccess {
	
	public boolean isConnected = false;
	public final HashSet<SabrePlayer> players = new HashSet<SabrePlayer>();
	public final HashSet<SabreGroup> groups = new HashSet<SabreGroup>();
	public final HashMap<String, HashSet<SabreBlock>> blocks = new HashMap<String, HashSet<SabreBlock>>();
	public final HashSet<PrisonPearl> pearls = new HashSet<PrisonPearl>();
	public final HashMap<UUID, ArrayList<SnitchLogEntry>> snitchEntries = new HashMap<UUID, ArrayList<SnitchLogEntry>>();
	
	
	
	public MockDataAccess() { }

	@Override
	public void connect() throws Exception {
		isConnected = true;
	}

	@Override
	public void disconect() {
		isConnected = false;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public Collection<SabrePlayer> playerGetAll() {
		return players;
	}

	@Override
	public void playerInsert(SabrePlayer p) {
		players.add(p);
	}

	@Override
	public void playerUpdateLastLogin(SabrePlayer p) { }

	@Override
	public void playerUpdateAutoJoin(SabrePlayer p) { }

	@Override
	public void playerUpdateFaction(SabrePlayer p) { }

	@Override
	public void playerUpdateName(SabrePlayer p) { }

	@Override
	public void playerUpdateBan(SabrePlayer p) { }

	@Override
	public void playerUpdatePlayTime(SabrePlayer p) { }

	@Override
	public void playerUpdateFreedOffline(SabrePlayer p) { }

	@Override
	public void playerUpdateBed(SabrePlayer p) { }

	@Override
	public void playerDelete(SabrePlayer p) {
		players.remove(p);
	}

	@Override
	public void playerAddOfflineMessage(SabrePlayer p, String message) { }

	@Override
	public void playerClearOfflineMessages(SabrePlayer p) { }

	@Override
	public Collection<SabreGroup> groupGetAll() {
		return groups;
	}

	@Override
	public void groupInsert(SabreGroup g) {
		groups.add(g);
	}

	@Override
	public void groupAddInvited(SabreGroup g, UUID id) { }

	@Override
	public void groupRemoveInvited(SabreGroup g, UUID id) { }

	@Override
	public void groupAddMember(SabreGroup g, SabreMember m) { }

	@Override
	public void groupRemoveMember(SabreGroup g, SabreMember m) { }

	@Override
	public void groupUpdateMemberRank(SabreGroup g, SabreMember m) { }

	@Override
	public void groupUpdateName(SabreGroup g, String n) { }

	@Override
	public void groupDelete(SabreGroup g) {
		groups.remove(g);
	}

	@Override
	public Collection<SabreBlock> blockGetChunkRecords(Chunk c) {
		return blocks.get(SabreUtil.formatChunkName(c));
	}

	@Override
	public Collection<SabreBlock> blockGetRunningFactories() {
		HashSet<SabreBlock> records = new HashSet<SabreBlock>();
		
		for(HashSet<SabreBlock> blockSet : blocks.values()) {
			records.addAll(blockSet.stream().filter(b -> b instanceof BaseFactory && ((BaseFactory)b).getRunning()).collect(Collectors.toSet()));
		}
		
		return records;
	}

	@Override
	public void blockInsert(SabreBlock b) {
		String chunkName = SabreUtil.formatChunkName(b.getLocation());
		HashSet<SabreBlock> records = blocks.get(chunkName);
		if (records == null) {
			records = new HashSet<SabreBlock>();
			blocks.put(chunkName, records);
		}
		records.add(b);
	}

	@Override
	public void blockRemove(SabreBlock b) {
		String chunkName = SabreUtil.formatChunkName(b.getLocation());
		blocks.get(chunkName).remove(b);

	}

	@Override
	public void blockSetReinforcement(SabreBlock b) { }

	@Override
	public void blockUpdateReinforcementStrength(SabreBlock b) { }

	@Override
	public void blockSetSettings(SabreBlock b) { }

	@Override
	public ArrayList<SnitchLogEntry> snitchGetEntries(Snitch snitch) {
		return snitchEntries.get(snitch.getID());
	}

	@Override
	public void snitchMakeLog(SnitchLogEntry e) {
		snitchEntries.get(e.snitchID).add(e);
	}

	@Override
	public void snitchClear(Snitch snitch) {
		snitchEntries.get(snitch.getID()).clear();

	}

	@Override
	public Collection<PrisonPearl> pearlGetall() {
		return pearls;
	}

	@Override
	public void pearlInsert(PrisonPearl pp) {
		pearls.add(pp);
	}

	@Override
	public void pearlUpdate(PrisonPearl pp) { }

	@Override
	public void pearlUpdateSummoned(PrisonPearl pp) { }

	@Override
	public void pearlUpdateReturnLocation(PrisonPearl pp) { }

	@Override
	public void pearlUpdateSealStrength(PrisonPearl pp) { }

	@Override
	public void pearlRemove(PrisonPearl pp) {
		pearls.remove(pp);
	}

}
