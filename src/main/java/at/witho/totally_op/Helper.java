package at.witho.totally_op;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class Helper {
//	public static ItemStack peacefulWoodTool = new ItemStack(ModItems.peacefulWoodTool);
//	public static ItemStack peacefulIronTool = new ItemStack(ModItems.peacefulIronTool);
	
	public static boolean isPeacefulItem(ItemStack item) {
		return !item.isEmpty() && (item.getItem() == ModItems.peaceful_wood_tool ||
                item.getItem() == ModItems.peaceful_iron_tool ||
                item.getItem() == ModItems.peaceful_diamond_tool);
	}
	
	public static boolean hasPeacefulItem(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
        for (ItemStack item : inventory.mainInventory) {
            if (isPeacefulItem(item)) return true;
        }
        for (ItemStack item : inventory.offHandInventory) {
            if (isPeacefulItem(item)) return true;
        }
		return false;
	}

	public static boolean isSameBlock(Block a, Block b) {
		if (a == b) return true;
		if (a.getUnlocalizedName().equals(b.getUnlocalizedName())) return true;
		return false;
	}
}
