package com.gordonfreemanq.sabre.util;

import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemEnderPearl;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.StatisticList;
import net.minecraft.server.v1_8_R3.World;

@Deprecated
public class CustomNMSItemEnderPearl extends ItemEnderPearl {

	public CustomNMSItemEnderPearl() {
		super();
	}

	public ItemStack a(
			ItemStack itemstack,
			World world,
			EntityHuman entityhuman) {
		if (entityhuman.abilities.canInstantlyBuild) {
			return itemstack;
		} else if (entityhuman.vehicle != null) {
			return itemstack;
		} else {
			--itemstack.count;
			world.makeSound(
					entityhuman,
					"random.bow",
					0.5F,
					0.4F / (g.nextFloat() * 0.4F + 0.8F));
			if (!world.isClientSide) {
				double gravity = 0.060000;
				world.addEntity(new CustomNMSEntityEnderPearl(world, entityhuman, gravity));
			}
			entityhuman.b(StatisticList.USE_ITEM_COUNT[Item.getId(this)]);
			return itemstack;
		}
	}
}
