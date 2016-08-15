package com.civfactions.sabre.snitch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.civfactions.sabre.Lang;
import com.civfactions.sabre.SabrePlayer;
import com.civfactions.sabre.SabrePlugin;
import com.civfactions.sabre.blocks.Reinforcement;
import com.civfactions.sabre.customitems.SpecialBlock;
import com.civfactions.sabre.snitch.QTBox;
import com.mongodb.BasicDBObject;

@SuppressWarnings("rawtypes")
public class Snitch extends SpecialBlock implements QTBox, Comparable {

	private String name;
	private boolean notifies;
	private UUID placedById;
	private SabrePlayer placedBy;
	private Date placedOn;
	private UUID ID; 

	private int minx, maxx, miny, maxy, minz, maxz, radius;

	public static final String blockName = "Snitch";

	public Snitch(Location location, String typeName) {
		super(location, blockName);

		this.hasEffectRadius = true;

		name = "";
		notifies = true;
		placedById = null;
		placedBy = null;
		placedOn = new Date();
		radius = 11;
		calculateDimensions();
		ID = UUID.randomUUID();
	}


	/**
	 * Calculates the snitch dimensions
	 */
	public void calculateDimensions() {
		this.minx = getX() - radius;
		this.maxx = getX() + radius;
		this.minz = getZ() - radius;
		this.maxz = getZ() + radius;
		this.miny = getY() - radius;
		this.maxy = getY() + radius;
	}


	/**
	 * Gets the block X
	 * @return The block X
	 */
	public int getX() {
		return location.getBlockX();
	}


	/**
	 * Gets the block Y
	 * @return The block Y
	 */
	public int getY() {
		return location.getBlockY();
	}


	/**
	 * Gets the block Z
	 * @return The block Z
	 */
	public int getZ() {
		return location.getBlockZ();
	}

	@Override
	public String getTypeName() {
		return blockName;
	}

	@Override
	public boolean affectsLocation(Location l) {
		return false;
	}


	/**
	 * Gets the snitch name
	 * @return The snitch name
	 */
	public String getSnitchName() {
		return this.name;
	}


	/**
	 * Sets the snitch name
	 * @param name The snitch name
	 */
	public void setSnitchName(String name) {
		this.name = name;
		SabrePlugin.instance().getBlockManager().updateSettings(this);
	}
	
	
	/**
	 * Gets the snitch ID
	 * @return The snitch ID
	 */
	public UUID getID() {
		return this.ID;
	}
	
	
	/**
	 * Sets the snitch ID
	 * @param id The snitch ID
	 */
	public void setID(UUID id) {
		this.ID = id;
	}


	/**
	 * Gets the notify status
	 * @return The notify status
	 */
	public boolean getNotify() {
		return this.notifies;
	}
	
	
	/**
	 * Gets the notify string
	 * @return The notify string
	 */
	public String getNotifyString() {
		if (notifies) {
			return "On";
		}
		return "Off";
	}


	/**
	 * Gets the notify status
	 * @param notifies The notify status
	 */
	public void setNotify(boolean notifies) {
		if (notifies != this.notifies) {
			this.notifies = notifies;
			SabrePlugin.instance().getBlockManager().updateSettings(this);
		}
	}


	/**
	 * Gets the player to placed the snitch
	 * @return The player who placed it
	 */
	public SabrePlayer getPlacedBy() {
		if (placedBy == null) {
			placedBy =SabrePlugin.instance().getPlayerManager().getPlayerById(placedById);
		}
		return placedBy;
	}


	/**
	 * Gets the name of the player who placed the snitch
	 * @return The name of the player if known
	 */
	public String getPlacedByName() {
		SabrePlayer p = this.getPlacedBy();
		if (p != null) {
			return p.getName();
		}
		return "";
	}


	/**
	 * Sets the player UUID who placed the block
	 * @param placedBy Who placed it
	 */
	public void setPlacedBy(UUID placedBy) {
		this.placedById = placedBy;
		this.placedBy = null;
	}


	/**
	 * Gets when the snitch was placed
	 * @return Get date when it was placed
	 */
	public Date getPlacedOn() {
		return placedOn;
	}


	/**
	 * Sets when the snitch was placed
	 * @param placedOn The date when it was placed
	 */
	public void setPlacedOn(Date placedOn) {
		this.placedOn = placedOn;
	}


	/**
	 * Handles the stick click event
	 */
	@Override
	public void onStickInteract(PlayerInteractEvent e, SabrePlayer sp) {
		createSnitchController(sp);
	}
	
	
	/**
	 * Creates a snitch controller from the held item
	 * @param sp The player
	 */
	public void createSnitchController(SabrePlayer sp) {
		Reinforcement r = this.getReinforcement();
		if (r != null && !r.getGroup().isMember(sp)) {
			sp.msg(Lang.noPermission);
			return;
		}
		ItemStack is = (new SnitchController(this)).toItemStack();
		is.setAmount(sp.getPlayer().getItemInHand().getAmount());
		sp.getPlayer().getInventory().setItemInHand(is);
		sp.msg("<i>Created a snitch controller.");
	}


	/**
	 * Gets the settings specific to this block
	 * @return The mongodb document with the settings
	 */
	@Override
	public BasicDBObject getSettings() {
		BasicDBObject rec = new BasicDBObject("name", name)
		.append("id", ID.toString())
		.append("notifies", notifies)
		.append("placed_on", placedOn.getTime());
		
		if (placedById != null) {
			rec = rec.append("placed_by", placedById.toString());
		}
		
		return rec;
	}


	/**
	 * Loads settings from a mongodb document
	 * @param o The db document
	 */
	public void loadSettings(BasicDBObject o) {
		if (o != null) {
			this.name = o.getString("name", name);
			this.notifies = o.getBoolean("notifies", notifies);
			this.placedById = UUID.fromString(o.getString("placed_by"));
			this.placedOn = new Date(o.getLong("placed_on", placedOn.getTime()));
			this.ID = UUID.fromString(o.getString("id"));
			
		}
	}

	@Override
	public int compareTo(Object o) {
		// This assumes that only a single snitch can exist at a given (x,y,z)
		// Compare centers
		// 1. Test X relationship
		// 2. Test Z relationship
		// 3. Test Y relationship
		Snitch other = (Snitch)o;
		int tx = this.getX();
		int ty = this.getY();
		int tz = this.getZ();
		
		int ox = other.getX();
		int oy = other.getY();
		int oz = other.getZ();

		if (tx < ox) {
			return -1;
		}
		if (tx > ox) {
			return 1;
		}
		if (tz < oz) {
			return -1;
		}
		if (tz > oz) {
			return 1;
		}
		if (ty < oy) {
			return -1;
		}
		if (ty > oy) {
			return 1;
		}
		return 0;  // equal
	}

	@Override
	public int qtXMin() {
		return this.minx;
	}

	@Override
	public int qtXMid() {
        return this.getX();
	}

	@Override
	public int qtXMax() {
        return this.maxx;
	}

	@Override
	public int qtYMin() {
        return this.minz;
	}

	@Override
	public int qtYMid() {
        return this.getZ();
	}

	@Override
	public int qtYMax() {
        return this.maxz;
	}
	
	
	/**
	 * Checks whether the location is inside the cuboid
	 * @param loc The location to check
	 * @return true if it is inside the cuboid
	 */
    public boolean isWithinCuboid(Location loc) {
        return isWithinCuboid(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
    }

    
    /**
	 * Checks whether the block is inside the cuboid
	 * @param loc The block to check
	 * @return true if it is inside the cuboid
	 */
    public boolean isWithinCuboid(Block block) {
        return isWithinCuboid(new Vector(block.getX(), block.getY(), block.getZ()));
    }
    
    
    /**
	 * Checks whether the vector is inside the cuboid
	 * @param loc The vector to check
	 * @return true if it is inside the cuboid
	 */
    public boolean isWithinCuboid(Vector vec) {
        int vX = vec.getBlockX();
        int vY = vec.getBlockY();
        int vZ = vec.getBlockZ();
        if (vX >= minx && vX <= maxx && vY >= miny && vY <= maxy && vZ >= minz && vZ <= maxz) {
            return true;
        }

        return false;
    }
    
    
    /**
     * Checks if the y is within the height
     * @param y
     * @return
     */
    public boolean isWithinHeight(int y) {
        return y >= miny && y <= maxy;
    }
    
    
	/**
	 * Handles the block broken event
	 * @param p The player that broke the block
	 * @param e The event args
	 */
    @Override
	public void onBlockBroken(SabrePlayer p, BlockBreakEvent e) {
    	SabrePlayer sp = this.getPlacedBy();
    	String user = "?";
    	
    	if (sp != null) {
    		user = sp.getName();
    	}
    	p.msg(Lang.snitchNotifyWasPlaced, user,  new SimpleDateFormat("yyyy-MM-dd").format(this.getPlacedOn()));
	}
}
