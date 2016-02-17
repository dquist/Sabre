package com.gordonfreemanq.sabre.util;

import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.Bukkit;

@Deprecated
public class CustomNMSEntityEnderPearl extends EntityEnderPearl {
  private long tick_ = 0L;
  private double startDeltaX_ = 0.0D;
  private double startDeltaY_ = 0.0D;
  private double startDeltaZ_ = 0.0D;

  private double startX_ = 0.0D;
  private double startY_ = 0.0D;
  private double startZ_ = 0.0D;
  public double y_adjust_;

  public CustomNMSEntityEnderPearl(World world) {
    super(world);
    y_adjust_ = 0.030000F;
  }

  public CustomNMSEntityEnderPearl(World world, EntityLiving living, double gravity) {
    super(world, living);
    y_adjust_ = gravity;
  }

  protected float m() {
    return (float)y_adjust_;
  }

  public void t_() {
    super.t_();
    if (tick_==0) {
      startDeltaX_ = motX;
      startDeltaY_ = motY;
      startDeltaZ_ = motZ;

      startX_ = locX;
      startY_ = locY;
      startZ_ = locZ;
      return;
    }


    double x = startDeltaX_ * tick_ + startX_;
    double y = j() / -2.0F * (float)tick_ * (float)tick_ + startDeltaY_ * tick_ + startY_;
    double z = startDeltaZ_ * tick_ + startZ_;

    motX = startDeltaX_;
    motY = (-j() * (float)tick_ + startDeltaY_);
    motZ = startDeltaZ_;

    super.setPosition(x, y, z);
    tick_ += 1L;
  }

  public long getTick() {
    return tick_;
  }

  public void b(NBTTagCompound nbttagcompound) {
    EnderPearlUnloadEvent event = new EnderPearlUnloadEvent(getBukkitEntity());
    Bukkit.getServer().getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      getBukkitEntity().remove();
      return;
    }

    super.b(nbttagcompound);

    nbttagcompound.setDouble("HumbugGravity", y_adjust_);
    nbttagcompound.setLong("HumbugTick", tick_);

    nbttagcompound.setDouble("HumbugStartX", startX_);
    nbttagcompound.setDouble("HumbugStartY", startY_);
    nbttagcompound.setDouble("HumbugStartZ", startZ_);

    nbttagcompound.setDouble("HumbugStartDeltaX", startDeltaX_);
    nbttagcompound.setDouble("HumbugStartDeltaY", startDeltaY_);
    nbttagcompound.setDouble("HumbugStartDeltaZ", startDeltaZ_);
  }

  public void a(NBTTagCompound nbttagcompound) {
    EnderPearlLoadEvent event = new EnderPearlLoadEvent(getBukkitEntity());
    Bukkit.getServer().getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      getBukkitEntity().remove();
      return;
    }
    super.a(nbttagcompound);

    if (!nbttagcompound.hasKey("HumbugTick")) {
      tick_ = 0L;
      return;
    }

    y_adjust_ = nbttagcompound.getDouble("HumbugGravity");
    tick_ = nbttagcompound.getLong("HumbugTick");

    startX_ = nbttagcompound.getDouble("HumbugStartX");
    startY_ = nbttagcompound.getDouble("HumbugStartY");
    startZ_ = nbttagcompound.getDouble("HumbugStartZ");

    startDeltaX_ = nbttagcompound.getDouble("HumbugStartDeltaX");
    startDeltaY_ = nbttagcompound.getDouble("HumbugStartDeltaY");
    startDeltaZ_ = nbttagcompound.getDouble("HumbugStartDeltaZ");
  }
}
