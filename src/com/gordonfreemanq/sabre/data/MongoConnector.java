package com.gordonfreemanq.sabre.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.InventoryHolder;

import com.gordonfreemanq.sabre.SabreConfig;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.blocks.BlockManager;
import com.gordonfreemanq.sabre.blocks.Reinforcement;
import com.gordonfreemanq.sabre.blocks.SabreBlock;
import com.gordonfreemanq.sabre.core.ISabreLog;
import com.gordonfreemanq.sabre.groups.SabreMember;
import com.gordonfreemanq.sabre.groups.Rank;
import com.gordonfreemanq.sabre.groups.SabreGroup;
import com.gordonfreemanq.sabre.prisonpearl.BlockHolder;
import com.gordonfreemanq.sabre.prisonpearl.IItemHolder;
import com.gordonfreemanq.sabre.prisonpearl.LocationHolder;
import com.gordonfreemanq.sabre.prisonpearl.PrisonPearl;
import com.gordonfreemanq.sabre.snitch.Snitch;
import com.gordonfreemanq.sabre.snitch.SnitchAction;
import com.gordonfreemanq.sabre.snitch.SnitchLogEntry;
import com.gordonfreemanq.sabre.util.SabreUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoConnector implements IDataAccess {

	private static final String COL_PLAYERS = "players";
	private static final String COL_GROUPS = "groups";
	private static final String COL_BLOCKS = "blocks";
	private static final String COL_SNITCHES = "snitch_log";
	private static final String COL_PEARLS = "pearls";

	private final ISabreLog logger;
	private final SabreConfig config;

	private boolean connected;
	private MongoClient mongoClient;
	private DB db;
	private DBCollection colPlayers;
	private DBCollection colGroups;
	private DBCollection colBlocks;
	private DBCollection colSnitches;
	private DBCollection colPearls;

	private HashSet<SabrePlayer> loadedPlayers;

	private long timeStart;


	/**
	 * Creates a new MongoConnector instance
	 * @param logger The logging instance
	 * @param hostAddress The database host address
	 * @param hostPort The database host port
	 */
	public MongoConnector(ISabreLog logger, SabreConfig config) {
		this.logger = logger;
		this.config = config;
		this.connected = false;
	}


	/**
	 * Connects to the database
	 */
	@Override
	public void connect() throws Exception {
		if (config.DbUser.isEmpty()) {
			mongoClient = new MongoClient(new ServerAddress(config.DbAddress, config.DbPort));
		} else {
			MongoCredential credential = MongoCredential.createCredential(config.DbUser, config.DbName, config.DbPassword.toCharArray());
			mongoClient = new MongoClient(new ServerAddress(config.DbAddress, config.DbPort), Arrays.asList(credential));
		}

		// All writes return immediately
		mongoClient.setWriteConcern(WriteConcern.UNACKNOWLEDGED);

		db = mongoClient.getDB(config.DbName);
		colPlayers = db.getCollection(COL_PLAYERS);
		colGroups = db.getCollection(COL_GROUPS);
		colBlocks = db.getCollection(COL_BLOCKS);
		colSnitches = db.getCollection(COL_SNITCHES);
		colPearls = db.getCollection(COL_PEARLS);

		//colGroups.drop();
		//colGroups = db.getCollection(COL_GROUPS);
		
		//colBlocks.drop();
		//colBlocks = db.getCollection(COL_GROUPS);

		// Creates an index on the name field and forces it to be unique
		colGroups.createIndex(new BasicDBObject("name", 1), new BasicDBObject("unique", true).append("dropDups",  true));
		
		// Auto-drops any records more than 30 days old
		colSnitches.createIndex(new BasicDBObject("time", 1), new BasicDBObject("expireAfterSeconds", 2592000));

		connected = true;
	}


	/**
	 * Disconnects the client 
	 */
	public void disconect() {
		if (mongoClient != null) {
			mongoClient.close();
			mongoClient = null;
		}
	}


	/**
	 * Checks of the database is connected
	 * @return true if it is connected
	 */
	@Override
	public boolean isConnected() {
		return this.connected;
	}


	/**
	 * Gets the number of ms elapsed since the time was marked
	 * @return The number of ms elapsed
	 */
	private long getMsElapsed() {
		return System.currentTimeMillis() - timeStart;
	}


	/**
	 * Gets all the player records from the database
	 */
	@Override
	public Collection<SabrePlayer> playerGetAll() {

		// Mark start time
		timeStart = System.currentTimeMillis();

		loadedPlayers = new HashSet<SabrePlayer>();
		SabrePlayer p = null;

		DBCursor cursor = colPlayers.find();
		try {
			while(cursor.hasNext()) {
				BasicDBObject o = (BasicDBObject)cursor.next();

				try {
					boolean update = false;
					UUID id = UUID.fromString(o.get("_id").toString());
					String name = o.get("name").toString();

					p = new SabrePlayer(id, name);

					Date firstLogin = o.getDate("first_login");
					Date lastLogin = o.getDate("last_login");
					long playTime = o.getLong("play_time", 0);
					boolean autoJoin = o.getBoolean("auto_join", false);
					boolean freedOffline = o.getBoolean("freed_offline", false);
					boolean banned = o.getBoolean("banned", false);
					String banMessage = o.getString("ban_message", "");

					if (o.containsField("messages")) {
						Object msgObject = o.get("messages");
						if (msgObject instanceof BasicDBList) {
							for (Object msg : (BasicDBList)msgObject) {
								p.addOfflineMessage(msg.toString());
							}
						}
					}
					
					Location bed = null;
					if (o.containsField("bed")) {
						bed = SabreUtil.deserializeLocation(o.get("bed"));
					}

					p.setFirstLogin(firstLogin);
					p.setLastLogin(lastLogin);
					p.setPlaytime(playTime);
					p.setAutoJoin(autoJoin);
					p.setBanned(banned);
					p.setBanMessage(banMessage);
					p.setFreedOffline(freedOffline);
					p.setBedLocation(bed);
					loadedPlayers.add(p);

					// Update the record if it was missing a field
					if (update) {
						playerInsert(p);
					}
				} catch(Exception ex) {
					logger.log(Level.WARNING, "Failed to read player record %s", o.toString());
				}
			}
		} finally {
			cursor.close();
		}

		logger.log(Level.INFO, "Loaded %d player records in %dms.", loadedPlayers.size(), getMsElapsed());
		return loadedPlayers;
	}


	/**
	 * Inserts a player record to the database
	 * @param p The player to insert
	 */
	@Override
	public void playerInsert(SabrePlayer p) {
		BasicDBObject doc = new BasicDBObject()
		.append("_id", p.getID().toString())
		.append("name", p.getName())
		.append("first_login", p.getFirstLogin())
		.append("last_login", p.getLastLogin())
		.append("play_time", p.getPlaytime())
		.append("auto_join", p.getAutoJoin());

		colPlayers.insert(doc);
	}


	/**
	 * Updates a player's last login time
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateLastLogin(SabrePlayer p) {
		updatePlayerRecordValue(p, "last_login", p.getLastLogin());
	}


	/**
	 * Updates a player's auto-join status
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateAutoJoin(SabrePlayer p) {
		updatePlayerRecordValue(p, "auto_join", p.getAutoJoin());
	}


	/**
	 * Updates a player's display name
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateName(SabrePlayer p) {
		updatePlayerRecordValue(p, "name", p.getName());
	}


	/**
	 * Updates a player's ban status
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateBan(SabrePlayer p) {
		updatePlayerRecordValue(p, "banned", p.getBanned());
		updatePlayerRecordValue(p, "ban_message", p.getBanMessage());
	}
	
	/**
	 * Updates a player's play time
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdatePlayTime(SabrePlayer p) {
		updatePlayerRecordValue(p, "play_time", p.getPlaytime());
	}
	

	/**
	 * Updates the freed offline state
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateFreedOffline(SabrePlayer p) {
		updatePlayerRecordValue(p, "freed_offline", p.getFreedOffline());
	}
	

	/**
	 * Updates the bed location
	 * @param p The player instance to update
	 */
	@Override
	public void playerUpdateBed(SabrePlayer p) {
		Location l = p.getBedLocation();
		if (l != null) {
			updatePlayerRecordValue(p, "bed", SabreUtil.serializeLocation(l));
		}
	}

	/**
	 * Deletes a player record
	 * @param p The player instance to delete
	 */
	@Override
	public void playerDelete(SabrePlayer p) {
		BasicDBObject where = new BasicDBObject();
		where.append("_id", p.getID().toString());

		colPlayers.remove(where);
	}



	/**
	 * Adds an offline message for the player
	 * @param The player to update
	 * @message The message to add
	 */
	public void playerAddOfflineMessage(SabrePlayer p, String message) {
		BasicDBObject doc = new BasicDBObject()
		.append("$push", new BasicDBObject("messages", message));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", p.getID().toString());

		colPlayers.update(where,  doc);
	}


	/**
	 * Clears all offline messages for the player
	 * @param The player to update
	 */
	public void playerClearOfflineMessages(SabrePlayer p) {
		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject("messages", new BasicDBObject()));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", p.getID().toString());

		colPlayers.update(where, doc, false, true);
	}


	/**
	 * Updates a player record that matches the ID
	 * @param p The player to search for
	 * @param doc The document values to update
	 */
	private void updatePlayerRecordValue(SabrePlayer p, String key, Object val) {
		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject().append(key, val));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", p.getID().toString());

		colPlayers.update(where,  doc);
	}


	private SabrePlayer getLoadedPlayerByID(UUID id) {
		for (SabrePlayer p : loadedPlayers) {
			if (p.getID().equals(id)) {
				return p;
			}
		}

		return null;
	}


	/**
	 * Gets all the group records from the database
	 * @return The collection of group records
	 */
	@Override
	public Collection<SabreGroup> groupGetAll() {

		// Mark start time
		timeStart = System.currentTimeMillis();

		HashSet<SabreGroup> records = new HashSet<SabreGroup>();
		SabreGroup g = null;

		DBCursor cursor = colGroups.find();
		try {
			while(cursor.hasNext()) {
				DBObject o = cursor.next();

				//logger.log(Level.INFO, "Loaded group: %s", o.toString());

				try {
					boolean update = false;
					UUID id = UUID.fromString(o.get("_id").toString());
					String name = o.get("name").toString();
					BasicDBList memberList = (BasicDBList)o.get("members");

					// The group instance
					g = new SabreGroup(id, name);

					// Load the members for this group
					if (memberList.size() > 0) {
						for (Object mo : memberList) {
							DBObject m = (DBObject)mo;
							UUID memberId = UUID.fromString(m.get("id").toString());
							Rank rank = Rank.valueOf(m.get("rank").toString());

							// Get the player instance from the stored UUID
							SabrePlayer memberPlayer = getLoadedPlayerByID(memberId);
							if (memberPlayer != null) {
								g.addMember(memberPlayer, rank);
							} else {
								logger.log(Level.WARNING, "Loaded group member %s with no matching player ID.", id.toString());
							}
						}
					} else {
						logger.log(Level.WARNING, "Loaded group %s with no members.", g.getName());
					}

					// Load invited players for this group 
					if (o.containsField("invited")) {
						BasicDBList invitedList = (BasicDBList)o.get("invited");
						for (Object invited : invitedList) {
							g.addInvited(UUID.fromString(invited.toString()));
						}
					}

					records.add(g);

					// Update the record if it was missing a field
					if (update) {
						groupInsert(g);
					}
				} catch(Exception ex) {
					logger.log(Level.WARNING, "Failed to read group record %s", o.toString());
				}
			}
		} finally {
			cursor.close();
		}

		logger.log(Level.INFO, "Loaded %d group records in %dms.", records.size(), getMsElapsed());
		return records;
	}


	/**
	 * Inserts a group record to the database
	 * @param g The group to insert
	 */
	public void groupInsert(SabreGroup g) {

		ArrayList<BasicDBObject> memberList = new ArrayList<BasicDBObject>();
		for(SabreMember m : g.getMembers()) {
			memberList.add(new BasicDBObject()
			.append("id", m.getID().toString())
			.append("rank", m.getRank().toString()));
		}

		BasicDBObject doc = new BasicDBObject()
		.append("_id", g.getID().toString())
		.append("name", g.getName())
		.append("members", memberList)
		.append("invited", new ArrayList<String>());

		colGroups.insert(doc);
	}


	/**
	 * Adds an invited player to a group
	 * @param g The referenced group
	 * @param id The ID of the player to invite
	 */
	@Override
	public void groupAddInvited(SabreGroup g, UUID id) {
		BasicDBObject doc = new BasicDBObject()
		.append("$push", new BasicDBObject("invited", id.toString()));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.update(where,  doc);
	}


	/**
	 * Removes an invited player from a group
	 * @param g The referenced group
	 * @param id The ID of the player to remove
	 */
	@Override
	public void groupRemoveInvited(SabreGroup g, UUID id) {
		BasicDBObject doc = new BasicDBObject()
		.append("$pull", new BasicDBObject("invited", id.toString()));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.update(where, doc, false, true);
	}


	/**
	 * Adds a new group member to a group
	 * @param g The group to modify
	 * @param m The member to add
	 */
	@Override
	public void groupAddMember(SabreGroup g, SabreMember m) {
		BasicDBObject doc = new BasicDBObject()
		.append("$push", new BasicDBObject("members", new BasicDBObject()
		.append("id", m.getID().toString())
		.append("rank", m.getRank().toString())));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.update(where,  doc);
	}


	/**
	 * Removes a member from a group
	 * @param g The group to modify
	 * @param m The member to remove
	 */
	@Override
	public void groupRemoveMember(SabreGroup g, SabreMember m) {
		BasicDBObject doc = new BasicDBObject()
		.append("$pull", new BasicDBObject("members",  new BasicDBObject("id", m.getID().toString())));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.update(where, doc, false, true);
	}


	/**
	 * Updates a group member record
	 * @param g The group to modify
	 * @param m The member to update
	 */
	@Override
	public void groupUpdateMemberRank(SabreGroup g, SabreMember m) {
		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject("members.$.rank", m.getRank().toString()));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString()).append("members.id", m.getID().toString());

		colGroups.update(where, doc, false, true);
	}


	/**
	 * Updates the name of a group
	 * @param The group to modify
	 * @param name The new name
	 */
	@Override
	public void groupUpdateName(SabreGroup g, String n) {
		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject().append("name", n));

		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.update(where,  doc);
	}
	
	
	/**
	 * Deletes a group instance
	 * @param g The group to delete
	 */
	@Override
	public void groupDelete(SabreGroup g) {
		BasicDBObject where = new BasicDBObject();
		where.append("_id", g.getID().toString());

		colGroups.remove(where);
	}


	@Override
	public Collection<SabreBlock> blockGetChunkRecords(Chunk c) {
		
		HashMap<Location, SabreBlock> records = new HashMap<Location, SabreBlock>();
		
		BasicDBObject where = new BasicDBObject();
		where.append("chunk", formatChunk(c));
		
		DBCursor cursor = colBlocks.find(where);
		
		while (cursor.hasNext()) { 
			BasicDBObject o = (BasicDBObject)cursor.next();

			//logger.log(Level.INFO, "Loaded block: %s", o.toString());

			try {
				SabreBlock b = readBlockRecord(o);
				records.put(b.getLocation(), b);
			} catch(Exception ex) {
				logger.log(Level.WARNING, "Failed to read block record %s", o.toString());
			}
		}
		
		return records.values();
	}
	
	@Override
	public Collection<SabreBlock> blockGetRunningFactories() {
		HashMap<Location, SabreBlock> records = new HashMap<Location, SabreBlock>();
		
		BasicDBObject where = new BasicDBObject()
				.append("settings.factory", true)
				.append("settings.running", true);
		
		DBCursor cursor = colBlocks.find(where);
		
		while (cursor.hasNext()) { 
			BasicDBObject o = (BasicDBObject)cursor.next();

			//logger.log(Level.INFO, "Loaded block: %s", o.toString());

			try {
				SabreBlock b = readBlockRecord(o);
				records.put(b.getLocation(), b);
			} catch(Exception ex) {
				logger.log(Level.WARNING, "Failed to read block record %s", o.toString());
			}
		}
		
		return records.values();
	}
	
	
	/**
	 * Reads a block record
	 * @param o The DB object
	 * @return The sabre block instance
	 */
	private SabreBlock readBlockRecord(BasicDBObject o) {
		SabreBlock b = null;
		
		String chunkStr = o.getString("chunk");
		int x = o.getInt("x");
		int y = o.getInt("y");
		int z = o.getInt("z");
		String name = o.getString("name", "");
		String type = o.getString("type", "");
		
		Location l = parseBlockLocation(chunkStr, x, y, z);
		
		// Create the block instance from the factory
		b = BlockManager.blockFactory(l, type);
		b.setDisplayName(name);
		
		if (o.containsField("reinforcement")){
			BasicDBObject rein = (BasicDBObject)o.get("reinforcement");
			UUID groupID = UUID.fromString(rein.getString("group", ""));
			Material mat = Material.matchMaterial(rein.getString("material", ""));
			int strength = rein.getInt("strength", 0);
			long createdOn = rein.getLong("created_on", 0);
			int startStrength = config.getReinforcementStrength(mat, (short)0);
			boolean isPublic = rein.getBoolean("public", false);
			boolean isInsecure = rein.getBoolean("insecure", false);
			boolean isAdmin = rein.getBoolean("admin", false);
		
			Reinforcement r = new Reinforcement(l, groupID, mat, startStrength, createdOn, isAdmin);
			r.setStrength(strength);
			r.setPublic(isPublic);
			r.setInsecure(isInsecure);
			b.setReinforcement(r);
		}
		
		// Load the settings specific to this block
		if (o.containsField("settings")) {
			b.loadSettings((BasicDBObject)o.get("settings"));
		}
		
		return b;
	}


	/**
	 * Formats the chunk string for a location
	 * DON'T USE getChunk() because it loads the chunk
	 * @param l The location
	 * @return The chunk string
	 */
	private String formatChunk(Location l) {
		return String.format("%s,%s,%s", l.getWorld().getName(), l.getBlockX() >> 4, l.getBlockZ() >> 4);
	}
	
	private String formatChunk(Chunk c) {
		return String.format("%s,%s,%s", c.getWorld().getName(), c.getX(), c.getZ());
	}
	
	
	private Location parseBlockLocation(String chunkStr, int x, int y, int z) {
		String[] strings = chunkStr.split(",");
		String worldName = strings[0];
		World world = Bukkit.getWorld(worldName);
		return new Location(world, x, y, z);
	}


	@Override
	public void blockInsert(SabreBlock b) {
		Location l = b.getLocation();
		BasicDBObject blockRecord = new BasicDBObject()
		.append("chunk", formatChunk(l))
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ())
		.append("type", b.getTypeName())
		.append("name", b.getDisplayName());

		colBlocks.insert(blockRecord);

		Reinforcement r = b.getReinforcement();
		if (r != null) {
			blockSetReinforcement(b);
		}
		
		blockSetSettings(b);
	}
	
	
	@Override
	public void blockRemove(SabreBlock b) {
		Location l = b.getLocation();
		BasicDBObject where = new BasicDBObject()
		.append("chunk", formatChunk(b.getLocation()))
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ());
		
		colBlocks.remove(where);
	}


	@Override
	public void blockSetReinforcement(SabreBlock b) {
		
		Reinforcement r = b.getReinforcement();
		BasicDBObject rRecord = new BasicDBObject()
		.append("group", r.getGroup().getID().toString())
		.append("material", r.getMaterial().toString())
		.append("strength", r.getStrength());
		
		if (r.getPublic()) {
			rRecord.append("public", true);
		}
		
		if (r.getInsecure()) {
			rRecord.append("insecure", true);
		}
		
		if (r.isAdmin()) {
			rRecord.append("admin", true);
		}

		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject("reinforcement", rRecord));

		updateBlock(b, doc);
	}
	

	@Override
	public void blockUpdateReinforcementStrength(SabreBlock b) {

		Reinforcement r = b.getReinforcement();

		BasicDBObject doc = new BasicDBObject()
		.append("$set", new BasicDBObject("reinforcement.strength", r.getStrength()));

		updateBlock(b, doc);
	}
	
	
	/**
	 * Sets the block settings document
	 * @param b The block to update
	 */
	@Override
	public void blockSetSettings(SabreBlock b) {
		
		BasicDBObject settings = b.getSettings();
		if (settings != null) {			
			BasicDBObject doc = new BasicDBObject()
			.append("$set", new BasicDBObject("settings", settings));
			
			updateBlock(b, doc);
		}
	}
	
	
	/**
	 * Updates a block
	 * @param b The block to update
	 * @param doc The update document
	 */
	private void updateBlock(SabreBlock b, BasicDBObject doc) {
		
		Location l = b.getLocation();
		BasicDBObject where = new BasicDBObject()
		.append("chunk", formatChunk(b.getLocation()))
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ());
		
		colBlocks.update(where, doc);
	}


	/**
	 * Makes a snitch log entry
	 * @param e The log entry
	 */
	@Override
	public void snitchMakeLog(SnitchLogEntry e) {
		
		BasicDBObject record = new BasicDBObject("snitch", e.snitchID.toString())
			.append("player", e.player.toString())
			.append("action", e.action.ordinal())
			.append("time", e.time);
		
		if (e.material != null) {
			record = record.append("material", e.material.toString());
		}
		
		if (e.loc != null) {
			record = record.append("world", e.loc.getWorld().getName())
					.append("x", e.loc.getBlockX())
					.append("y", e.loc.getBlockY())
					.append("z", e.loc.getBlockZ());
		}
		
		if (e.victim != null) {
			record = record.append("victim", e.victim.toString());
		}
		
		if (e.entity != null) {
			record = record.append("entity", e.entity);
		}
	
		colSnitches.insert(record);
		
	}


	/**
	 * Gets all the log entries for a snitch
	 */
	@Override
	public ArrayList<SnitchLogEntry> snitchGetEntries(Snitch snitch) {
		ArrayList<SnitchLogEntry> records = new ArrayList<SnitchLogEntry>();
		SnitchLogEntry e = null;
		
		BasicDBObject where = new BasicDBObject();
		where.append("snitch", snitch.getID().toString());
		
		DBCursor cursor = colSnitches.find(where).sort(new BasicDBObject("date", -1));
		
		while (cursor.hasNext()) { 
			BasicDBObject o = (BasicDBObject)cursor.next();
			try {
				UUID snitchID = UUID.fromString(o.getString("snitch"));
				UUID player = UUID.fromString(o.getString("player"));
				SnitchAction action = SnitchAction.fromInt(o.getInt("action"));
				Date time = o.getDate("time");
				
				e = new SnitchLogEntry(snitchID, player, action, time);
				e.material = Material.getMaterial(o.getString("material"));
				e.entity = o.getString("entity");
				if (o.containsField("victim")) {
					e.victim = UUID.fromString(o.getString("victim"));
				}
				
				if (o.containsField("world")) {
					String worldName = o.getString("world");
					int x = o.getInt("x");
					int y = o.getInt("y");
					int z = o.getInt("z");
					e.loc = new Location(Bukkit.getWorld(worldName), x, y, z);
				}
				
				records.add(e);
				
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Failed to read snitch record %s", o.toString());
			}
		}
		
		return records;
	}
	
	
	/**
	 * Clears snitch entries
	 * @param The snitch to clear
	 */
	@Override
	public void snitchClear(Snitch snitch) {
		BasicDBObject where = new BasicDBObject();
		where.append("snitch", snitch.getID().toString());
		
		colSnitches.remove(where);
	}
	
	
	/**
	 * Gets all the prison pearl records
	 */
	@Override
	public Collection<PrisonPearl> pearlGetall() {
		ArrayList<PrisonPearl> records = new ArrayList<PrisonPearl>();
		
		DBCursor cursor = colPearls.find();
		
		while (cursor.hasNext()) { 
			BasicDBObject o = (BasicDBObject)cursor.next();
			try {
				UUID playerID = UUID.fromString(o.getString("_id"));
				Date pearledOn = o.getDate("pearled_on");
				String worldName = o.getString("world");
				int x = o.getInt("x");
				int y = o.getInt("y");
				int z = o.getInt("z");
				Location l = new Location(Bukkit.getWorld(worldName), x, y, z);
				boolean summoned = o.getBoolean("summoned");
				boolean canDamage = o.getBoolean("can_damage");
				int sealStrength = o.getInt("seal_strength", 0);
				Location returnLocation = null;
				
				if (o.containsField("return_location")) {
					returnLocation = SabreUtil.deserializeLocation(o.get("return_location"));
				}
				
				// Is it held in a block or just a location?
				IItemHolder holder;
				if (l.getBlock().getState() instanceof InventoryHolder) {
					holder = new BlockHolder(l.getBlock());
				} else {
					holder = new LocationHolder(l);
				}
				
				PrisonPearl p = new PrisonPearl(playerID, holder);
				p.setPearledOn(pearledOn);
				p.setSummoned(summoned);
				p.setCanDamage(canDamage);
				p.setReturnLocation(returnLocation);
				p.setSealStrength(sealStrength);
				
				records.add(p);
				
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Failed to read pearl record %s", o.toString());
			}
		}
		
		return records;
	}
	

	/**
	 * Inserts a pearl record
	 * @param pp The pearl to insert
	 */
	@Override
	public void pearlInsert(PrisonPearl pp) {
		
		Location l = pp.getHolder().getLocation();
		BasicDBObject doc = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString())
		.append("pearled_on", pp.getPearledOn())
		.append("world", l.getWorld().getName())
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ())
		.append("summoned", pp.getSummoned())
		.append("can_damage", pp.getCanDamage())
		.append("seal_strength", pp.getSealStrength());

		colPearls.insert(doc);
	}
	

	/**
	 * Updates a pearl record
	 * @param pp The pearl to update
	 */
	@Override
	public void pearlUpdate(PrisonPearl pp) {
		
		Location l = pp.getHolder().getLocation();
		BasicDBObject doc = new BasicDBObject("$set", new BasicDBObject()
		.append("_id", pp.getPlayerID().toString())
		.append("pearled_on", pp.getPearledOn())
		.append("world", l.getWorld().getName())
		.append("x", l.getBlockX())
		.append("y", l.getBlockY())
		.append("z", l.getBlockZ())
		.append("summoned", pp.getSummoned())
		.append("can_damage", pp.getCanDamage())
		.append("seal_strength", pp.getSealStrength()));
		
		BasicDBObject where = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString());

		colPearls.update(where, doc);
		
		pearlUpdateReturnLocation(pp);
	}
	

	/**
	 * Updates the summon status of the pearl
	 * @param pp The pearl to update
	 */
	@Override
	public void pearlUpdateSummoned(PrisonPearl pp) {
		BasicDBObject doc = new BasicDBObject("$set", new BasicDBObject()
		.append("summoned", pp.getSummoned()));
		
		BasicDBObject where = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString());

		colPearls.update(where, doc);
	}
	
	
	/**
	 * Updates the summon return location
	 * @param pp The pearl to update
	 */
	@Override
	public void pearlUpdateReturnLocation(PrisonPearl pp) {
		Location l = pp.getReturnLocation();
		if (l == null) {
			return;
		}
		
		BasicDBObject doc = new BasicDBObject("$set", new BasicDBObject()
		.append("return_location", SabreUtil.serializeLocation(l)));
		
		BasicDBObject where = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString());

		colPearls.update(where, doc);
	}
	
	
	/**
	 * Updates the strength of a prison pearl
	 * @param pp The pearl to update
	 */
	@Override
	public void pearlUpdateSealStrength(PrisonPearl pp) {		
		BasicDBObject doc = new BasicDBObject("$set", new BasicDBObject()
		.append("seal_strength", pp.getSealStrength()));
		
		BasicDBObject where = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString());

		colPearls.update(where, doc);
	}
	

	/**
	 * Deletes a pearl record
	 * @param pp The pearl to remove
	 */
	@Override
	public void pearlRemove(PrisonPearl pp) {
		BasicDBObject where = new BasicDBObject()
		.append("_id", pp.getPlayerID().toString());
		
		colPearls.remove(where);
	}
}
