package com.gordonfreemanq.sabre.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.gordonfreemanq.sabre.blocks.SabreItemStack;


@SuppressWarnings("serial")
public class ItemList<E extends SabreItemStack> extends ArrayList<E> {
	public boolean exactlyIn(Inventory inventory)
	{
		boolean returnValue=true;
		//Checks that the ItemList ItemStacks are contained in the inventory
		for(SabreItemStack itemStack : this)
		{
			returnValue=returnValue&&(amountAvailable(inventory, itemStack) == itemStack.getAmount());
		}
		//Checks that inventory has not ItemStacks in addition to the ones in the itemList
		for(ItemStack invItemStack:inventory.getContents())
		{
			if(invItemStack!=null)
			{
				boolean itemPresent=false;
				for(ItemStack itemStack:this)
				{
					if(itemStack.isSimilar(invItemStack))
					{
						itemPresent=true;
					}
				}
				returnValue=returnValue&&itemPresent;
			}
		}
		return returnValue;
	}
	public boolean oneIn(Inventory inventory)
	{
		if(this.isEmpty())
		{
			return true;
		}
		else
		{
			for(SabreItemStack itemStack : this)
			{
				if (amountAvailable(inventory, itemStack) >= itemStack.getAmount())
				{
					return true;
				}
			}
			return false;
		}
	}
	public boolean allIn(Inventory inventory)
	{
		for(SabreItemStack itemStack : this)
		{
			if (amountAvailable(inventory, itemStack) < itemStack.getAmount())
			{
				return false;
			}
		}
		return true;
	}

	public boolean removeFrom(Inventory inventory)
	{
		boolean returnValue=true;
		if(allIn(inventory))
		{
			for(ItemStack itemStack:this)
			{
				returnValue=returnValue&&removeItemStack(inventory,itemStack);
			}
		}
		else
		{
			returnValue=false;
		}
		return returnValue;
	}
	public int removeMaxFrom(Inventory inventory,int maxAmount)
	{
		int amountRemoved=0;
		while(size()!=0&&allIn(inventory)&&amountRemoved<=maxAmount)
		{
			if(removeFrom(inventory))
			{
				amountRemoved++;
			}
		}
		return amountRemoved;
	}
	
	
	
	public ItemList<SabreItemStack> removeOneFrom(Inventory inventory)
	{
		ItemList<SabreItemStack> itemList = new ItemList<SabreItemStack>();
		for(SabreItemStack itemStack : this)
		{
			if(removeItemStack(inventory, itemStack))
			{
				itemList.add(itemStack.clone());
				break;
			}
		}
		return itemList;
	}
	
	
	
	public ItemList<SabreItemStack> getDifference(Inventory inventory)
	{
		ItemList<SabreItemStack> missingItems = new ItemList<SabreItemStack>();
		for(SabreItemStack itemStack : this)
		{
			int difference=itemStack.getAmount()-amountAvailable(inventory, itemStack);
			if (difference>0)
			{
				SabreItemStack clonedItemStack = itemStack.clone();
				clonedItemStack.setAmount(difference);
				missingItems.add(clonedItemStack);
			}
		}
		return missingItems;
	}
	
	
	public int amountAvailable(Inventory inventory)
	{
		int amountAvailable=0;
		for(SabreItemStack itemStack : this)
		{
			int currentAmountAvailable = amountAvailable(inventory,itemStack);
			amountAvailable=amountAvailable>currentAmountAvailable ? amountAvailable : currentAmountAvailable;
		}
		return amountAvailable;
	}
	
	
	public void putIn(Inventory inventory)
	{
		putIn(inventory, new ArrayList<ProbabilisticEnchantment>());
	}
	
	
	public void putIn(Inventory inventory,List<ProbabilisticEnchantment> probabilisticEnchaments)
	{
		for(ItemStack itemStack:this)
		{
			int maxStackSize=itemStack.getMaxStackSize();
			int amount=itemStack.getAmount();
			while(amount>maxStackSize)
			{
				ItemStack itemClone=itemStack.clone();
				Map<Enchantment,Integer> enchantments=getEnchantments(probabilisticEnchaments);
				for(Enchantment enchantment:enchantments.keySet())
				{
					if(enchantment.canEnchantItem(itemStack))
					{
						itemClone.addUnsafeEnchantment(enchantment,enchantments.get(enchantment));
					}
				}
				itemClone.setAmount(maxStackSize);
				inventory.addItem(itemClone);
				amount-=maxStackSize;
			}
			ItemStack itemClone=itemStack.clone();
			Map<Enchantment,Integer> enchantments = getEnchantments(probabilisticEnchaments);
			for(Enchantment enchantment:enchantments.keySet())
			{
				if(enchantment.canEnchantItem(itemStack))
				{
					itemClone.addUnsafeEnchantment(enchantment,enchantments.get(enchantment));
				}
			}
			itemClone.setAmount(amount);
			inventory.addItem(itemClone);
		}
	}
	
	public HashMap<Enchantment, Integer> getEnchantments(List<ProbabilisticEnchantment> probabilisticEnchaments)
	{
		HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
		Random rand = new Random();
		for(int i = 0; i < probabilisticEnchaments.size(); i++)
		{
			if(probabilisticEnchaments.get(i).getProbability() >= rand.nextDouble())
			{
				enchantments.put(probabilisticEnchaments.get(i).getEnchantment(),probabilisticEnchaments.get(i).getLevel());
			}
		}
		
		return enchantments;
	}
	
	public String toString()
	{
		String returnString="";
		for(int i = 0; i <size(); i++)
		{
			String name=get(i).getItemMeta().hasDisplayName() ? get(i).getItemMeta().getDisplayName() : get(i).getCommonName();
			returnString+=String.valueOf(get(i).getAmount())+" "+name;
			if(i<size()-1)
			{
				returnString+=", ";
			}
		}
		return returnString;
	}
	
	
	/**
	 * Returns the number of multiples of an ItemStack that are available
	 * @param inventory The inventory to check
	 * @param itemStack The item stack to compare
	 * @return The number of items available
	 */
	private int amountAvailable(Inventory inventory, SabreItemStack itemStack)
	{
		int totalMaterial = 0;
		for(ItemStack currentItemStack : inventory.all(itemStack.getType()).values())
		{
			if(currentItemStack != null)
			{	
				if (itemStack.isSimilar(currentItemStack)) {
					totalMaterial += currentItemStack.getAmount();
				}
			}
		}
		
		for(SabreItemStack substitute : itemStack.getSubstitutes()) {
			for(ItemStack currentItemStack : inventory.all(substitute.getType()).values())
			{
				if(currentItemStack != null)
				{	
					if (substitute.isSimilar(currentItemStack)) {
						totalMaterial += currentItemStack.getAmount();
					}
				}
			}
		}
		
		return totalMaterial;
	}
	//Removes an itemstacks worth of material from an inventory
	private boolean removeItemStack(Inventory inventory,ItemStack itemStack)
	{		
		int materialsToRemove = itemStack.getAmount();
		ListIterator<ItemStack> iterator = inventory.iterator();
		while(iterator.hasNext())
		{
			ItemStack currentItemStack = iterator.next();
			if (itemStack.isSimilar(currentItemStack))
			{
				if (materialsToRemove <= 0)
				{
					break;
				}
				if(currentItemStack.getAmount() == materialsToRemove)
				{
					iterator.set(new ItemStack(Material.AIR, 0));
					materialsToRemove = 0;
				}
				else if(currentItemStack.getAmount() > materialsToRemove)
				{
					ItemStack temp = currentItemStack.clone();
					temp.setAmount(currentItemStack.getAmount() - materialsToRemove);
					iterator.set(temp);
					materialsToRemove = 0;
				}
				else
				{
					int inStack = currentItemStack.getAmount();
					iterator.set(new ItemStack(Material.AIR, 0));
					materialsToRemove -= inStack;
				}
			}
		}				
		return materialsToRemove == 0;
	}
	
	
	
	public ItemList<SabreItemStack> getMultiple(int multiplier)
	{
		ItemList<SabreItemStack> multipliedItemList=new ItemList<SabreItemStack>();
		for (SabreItemStack itemStack : this)
		{
			SabreItemStack itemStackClone = itemStack.clone();
			itemStackClone.setAmount(itemStack.getAmount()*multiplier);
			multipliedItemList.add(itemStackClone);
		}
		return multipliedItemList;
	}
	
	
	
	public ItemList<SabreItemStack> getMultiple(double multiplier) 
	{
		ItemList<SabreItemStack> multipliedItemList=new ItemList<SabreItemStack>();
		for (SabreItemStack itemStack : this)
		{
			SabreItemStack itemStackClone = itemStack.clone();
			long newAmount = (long) Math.round(itemStackClone.getAmount()*multiplier);
			if (newAmount > 64)
			{
				for (;newAmount > 64; newAmount = newAmount-64)
				{
					SabreItemStack newItemStack = itemStack.clone();
					newItemStack.setAmount(64);
					multipliedItemList.add(newItemStack);
				}
			}
			itemStackClone.setAmount((int) newAmount);
			multipliedItemList.add(itemStackClone);
		}
		return multipliedItemList;
	}
}
