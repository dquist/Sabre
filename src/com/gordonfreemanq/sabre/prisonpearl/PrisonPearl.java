package com.gordonfreemanq.sabre.prisonpearl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.gordonfreemanq.sabre.PlayerManager;
import com.gordonfreemanq.sabre.SabrePlayer;
import com.gordonfreemanq.sabre.SabrePlugin;
import com.gordonfreemanq.sabre.cmd.pearl.CmdPearl;
import com.gordonfreemanq.sabre.util.TextUtil;


/**
 * An instance of a player who is imprisoned
 * @author GFQ
 */
public class PrisonPearl {

	public static final int HOLDER_COUNT = 5;
	public static String ITEM_NAME = "Prison Pearl";

	private final UUID playerId;
	private SabrePlayer player;
	private IItemHolder holder;
	private Date pearledOn;
	private LinkedBlockingQueue<IItemHolder> holders;
	private long lastMoved;
	private boolean summoned;
	private boolean canDamage;
	private Location returnLocation;

	/**
	 * Creates a new prison pearl instance
	 * @param playerId The pearled player id
	 * @param holder The holder instance
	 */
	public PrisonPearl(UUID playerId, IItemHolder holder) {
		this.playerId = playerId;
		this.pearledOn = new Date();
		this.holders = new LinkedBlockingQueue<IItemHolder>();
		this.lastMoved = pearledOn.getTime();
		this.setHolder(holder);
		this.summoned = false;
		this.canDamage = false;
	}


	/**
	 * Creates a new prison pearl instance
	 * @param playerId The pearled player id
	 * @param holder The holder instance
	 */
	public PrisonPearl(SabrePlayer player, IItemHolder holder) {
		this(player.getID(), holder);
		this.player = player;
	}
	
	/**
	 * Creates a new prison pearl instance
	 * @param playerId The pearled player id
	 * @param holder The holder instance
	 */
	public PrisonPearl(SabrePlayer player, SabrePlayer holder) {
		this(player.getID(), new PlayerHolder(holder));
		this.player = player;
	}
	
	
	/**
	 * Gets the imprisoned player ID
	 * @return The player ID
	 */
	public UUID getPlayerID() {
		return playerId;
	}


	/**
	 * Gets the imprisoned player
	 * @return The player insatnce
	 */
	public SabrePlayer getPlayer() {
		if (player == null) {
			player = PlayerManager.getInstance().getPlayerById(playerId);
		}
		return player;
	}


	/**
	 * Gets when the player was pearled
	 * @return The time the player was pearled
	 */
	public Date getPearledOn() {
		return this.pearledOn;
	}


	/**
	 * Sets when the player was pearled
	 * @param pearledOn The time the player was pearled
	 */
	public void setPearledOn(Date pearledOn) {
		this.pearledOn = pearledOn;
	}


	/**
	 * Gets the imprisoned name
	 * @return The player name
	 */
	public String getName() {
		return this.getPlayer().getName();
	}


	/**
	 * Gets the pearl holder
	 * @return The pearl holder
	 */
	public IItemHolder getHolder() {
		return this.holder;
	}


	/**
	 * Sets the pearl holder
	 * @param holder The new pearl holder
	 */
	public void setHolder(IItemHolder holder) {
		if (holder == null) {
			throw new RuntimeException("Prisonpearl holder cannot be null.");
		}

		this.holder = holder;
		this.holders.add(holder);

		if (holders.size() > HOLDER_COUNT) {
			holders.poll();
		}
	}


	/**
	 * Sets the pearl holder to a player
	 * @param holder The new pearl holder
	 */
	public void setHolder(SabrePlayer p) {
		this.setHolder(new PlayerHolder(p));
	}


	/**
	 * Sets the pearl holder to a block
	 * @param holder The new pearl block
	 */
	public void setHolder(Block b) {
		this.setHolder(new BlockHolder(b));
	}


	/**
	 * Sets the pearl holder to a location
	 * @param holder The new pearl location
	 */
	public void setHolder(Location l) {
		this.setHolder(new LocationHolder(l));
	}


	/**
	 * Gets the name of the current location
	 * @return The string of the current location
	 */
	public String getLocationName() {
		final Location loc = holder.getLocation();
		final Vector vec = loc.toVector();
		final String str = loc.getWorld().getName() + " " + vec.getBlockX() + " " + vec.getBlockY() + " " + vec.getBlockZ();
		return "held by " + holder.getName() + " at " + str;
	}
	
	
	/**
	 * Marks when the pearl was moved last
	 */
    public void markMove() {
        this.lastMoved = System.currentTimeMillis();
    }
    
    
    /**
     * Gets the summoned status
     * @return The summoned status
     */
    public boolean getSummoned() {
    	return this.summoned;
    }
    
    
    /**
     * Sets the summoned status
     * @param The summoned status
     */
    public void setSummoned(boolean summoned) {
    	this.summoned = summoned;
    }

    
    /**
     * Gets the damage status
     * @return The damage status
     */
    public boolean getCanDamage() {
    	return this.canDamage;
    }
    
    
    /**
     * Sets whether the player can damage others
     * @param The damage status
     */
    public void setCanDamage(boolean canDamage) {
    	this.canDamage = canDamage;
    }
    
    
    /**
     * Gets the return location
     * @return The return location
     */
    public Location getReturnLocation() {
    	return this.returnLocation;
    }
    
    
    /**
     * Sets the return location
     * @param returnLocation The return location
     */
    public void setReturnLocation(Location returnLocation) {
    	this.returnLocation = returnLocation;
    }
    
    

	/**
	 * Creates an item stack for the pearl
	 * @return The new item stack
	 */
	public ItemStack createItemStack() {
		List<String> lore = new ArrayList<String>();
		lore.add(parse("<l>%s", ITEM_NAME));
		lore.add(parse("<a>Player: <n>%s", this.getName()));
		lore.add(parse("<a>UUID: <n>%s", playerId.toString()));
		lore.add(parse("<a>Pearled on: <n>%s", new SimpleDateFormat("yyyy-MM-dd").format(pearledOn)));
		lore.add(parse(""));
		lore.add(parse("<l>Commands:"));
		lore.add(parse(CmdPearl.getInstance().cmdFree.getUseageTemplate(true)));
		lore.add(parse(CmdPearl.getInstance().cmdSummon.getUseageTemplate(true)));
		lore.add(parse(CmdPearl.getInstance().cmdReturn.getUseageTemplate(true)));
		lore.add(parse(CmdPearl.getInstance().cmdKill.getUseageTemplate(true)));
		

		ItemStack is = new ItemStack(Material.ENDER_PEARL, 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(this.getName());
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	private static Pattern idPattern = Pattern.compile(parse("<a>UUID: <n>(.+)"));
	
	
	/**
	 * Gets the UUID from a prison pearl
	 * @param is The item stack
	 * @return The player UUID, or null if it can't parse
	 */
	public static UUID getIDFromItemStack(ItemStack is) {
		if (is == null) {
			return null;
		}
		
		if (!is.getType().equals(Material.ENDER_PEARL)) {
			return null;
		}

		ItemMeta im = is.getItemMeta();
		if (im == null) {
			return null;
		}

		List<String> lore = im.getLore();
		if (lore == null) {
			return null;
		}

		if (lore.size() < 3) {
			return null;
		}
		
		String idLore  = lore.get(2);
		Matcher match = idPattern.matcher(idLore);
		if (match.find()) {
			UUID id = UUID.fromString(match.group(1));
			return id;
		}
		
		return null;
	}
	

	/**
	 * Validates that an item stack is the prison pearl
	 * @param is The item stack
	 * @return true if it checks out
	 */
	public boolean validateItemStack(ItemStack is) {

		UUID id = getIDFromItemStack(is);
		if (id != null && id.equals(this.playerId)) {
			return true;
		}
		
		return false;
	}


	/**
	 * Verifies the pearl location
	 * @return
	 */
	public boolean verifyLocation() {
		StringBuilder sb = new StringBuilder();

		StringBuilder verifier_log = new StringBuilder();
		StringBuilder failure_reason_log = new StringBuilder();

		for (final IItemHolder holder : this.holders) {
			HolderVerReason reason = this.verifyHolder(holder, verifier_log);
			if (reason.ordinal() > 5) {

				sb.append(String.format("PP (%s, %s) passed verification for reason %s: %s",
						playerId.toString(), this.getName(), reason.toString(), verifier_log.toString()));
				SabrePlugin.getPlugin().log(Level.INFO, sb.toString());

				return true;
			} else {
				failure_reason_log.append(reason.toString()).append(", ");
			}
			verifier_log.append(", ");
		}
		sb.append(String.format("PP (%s, %s) failed verification for reason %s: %s",
				playerId.toString(), this.getName(), failure_reason_log.toString(), verifier_log.toString()));
		SabrePlugin.getPlugin().log(Level.INFO, sb.toString());
		return false;
	}

	private enum HolderVerReason{
		DEFAULT,
		ENTITY_NOT_IN_CHUNK,
		PLAYER_NOT_ONLINE,
		BLOCK_STACK_NULL,
		NOT_BLOCK_INVENTORY,
		NO_ITEM_PLAYER_OR_LOCATION, //True after here
		ON_GROUND,
		IN_HAND,
		IN_CHEST,
		IN_VIEWER_HAND,
		TIME
	}


	/**
	 * Verifies the holder of a pearl
	 * @param holder The holder to check
	 * @param feedback The feedback string
	 * @return true if the pearl was found in a valid location
	 */
	public HolderVerReason verifyHolder(IItemHolder holder, StringBuilder feedback) {
		
		if (System.currentTimeMillis() - this.lastMoved < 2000) {
			// The pearl was recently moved. Due to a race condition, this exists to
			//  prevent players from spamming /ppl to get free when a pearl is moved.
			return HolderVerReason.TIME;
		}

		if (holder instanceof LocationHolder) {
			 // Location holder
			Chunk chunk = holder.getLocation().getChunk();
			for (Entity entity : chunk.getEntities()) {
				if (entity instanceof Item) {
					Item item = (Item)entity;
					ItemStack is = item.getItemStack();

					if (validateItemStack(is)) {
						feedback.append(String.format("Found on ground at (%d,%d,%d)",
								entity.getLocation().getBlockX(),
								entity.getLocation().getBlockY(),
								entity.getLocation().getBlockZ()));
						return HolderVerReason.ON_GROUND;
					}
				}
			}
			feedback.append("On ground not in chunk");
			return HolderVerReason.ENTITY_NOT_IN_CHUNK;
		} else if (holder instanceof PlayerHolder) {
			SabrePlayer sp = ((PlayerHolder)holder).getPlayer();
			Player p = sp.getPlayer();
			
			// Is the holder online?
			if (!p.isOnline()) {
				feedback.append(String.format("Jailor %s not online", p.getName()));
				return HolderVerReason.PLAYER_NOT_ONLINE;
			}
			
			// Is the item held?
			ItemStack cursorItem = p.getItemOnCursor();
			if (this.validateItemStack(cursorItem)) {
				return HolderVerReason.IN_HAND;
			}
			
			// In the player inventory?
			for (ItemStack item : p.getInventory().all(Material.ENDER_PEARL).values()) {
				if (this.validateItemStack(item)) {
					return HolderVerReason.IN_CHEST;
				}
			}

			// Nope, not found
			feedback.append(String.format("Not in %s's inventory", sp.getName()));
			return HolderVerReason.DEFAULT;
			
		} else { // Block holder
			Block b = holder.getLocation().getBlock();
			
			// Check the block state first
			Location bl = b.getLocation();
			BlockState bs = bl.getBlock().getState();
			if (bs == null) {
				feedback.append("BlockState is null");
				return HolderVerReason.BLOCK_STACK_NULL;
			}
			
			// Is the block an inventory block?
			Location bsLoc = bs.getLocation();
			if (!(bs instanceof InventoryHolder)) {
				feedback.append(String.format(
						"%s not inventory at (%d,%d,%d)", bs.getType().toString(),
						bsLoc.getBlockX(), bsLoc.getBlockY(), bsLoc.getBlockZ()));
				return HolderVerReason.NOT_BLOCK_INVENTORY;
			}
			
			// Is the item held?
			Inventory inv = ((InventoryHolder)bs).getInventory();
			for (HumanEntity viewer : inv.getViewers()) {
				ItemStack cursoritem = viewer.getItemOnCursor();
				if (validateItemStack(cursoritem)) {
					feedback.append(String.format("In hand of %s viewing chest at (%d,%d,%d)",
							viewer.getName(),
							holder.getLocation().getBlockX(),
							holder.getLocation().getBlockY(),
							holder.getLocation().getBlockZ()));
				return HolderVerReason.IN_VIEWER_HAND;
				}
			}
			
			// In the container inventory?
			for (ItemStack item : inv.all(Material.ENDER_PEARL).values()) {
				if (this.validateItemStack(item)) {
					return HolderVerReason.IN_CHEST;
				}
			}
			
			// Nope, not found
			return HolderVerReason.DEFAULT;
		}
	}
	
	public Location getLocation() {
		return this.holders.peek().getLocation();
	}


	protected static String parse(String str) {
		return TextUtil.txt.parse(str);
	}

	protected static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}
}
