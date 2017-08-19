package at.witho.totally_op;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class Helper {
	public static boolean hasPeacefulItem(EntityPlayer player) {
		InventoryPlayer inventory = player.inventory;
		ItemStack lookFor = new ItemStack(ModItems.peacefulTool);
        for (ItemStack item : inventory.mainInventory) {
            if (!item.isEmpty() && item.isItemEqualIgnoreDurability(lookFor)) return true;
        }
        for (ItemStack item : inventory.offHandInventory) {
            if (!item.isEmpty() && item.isItemEqualIgnoreDurability(lookFor)) return true;
        }
		return false;
	}
}
