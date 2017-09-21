package at.witho.totally_op.storage;

import at.witho.totally_op.gui.RucksackGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class RucksackContainer extends Container {
    public RucksackContainer(InventoryPlayer inventory, RucksackStorage rucksack) {
        for(int y = 0; y < RucksackGui.slotsY; y++) {
            for(int x = 0; x < RucksackGui.slotsX; x++) {
                addSlotToContainer(new Slot(rucksack,
                        x + y * RucksackGui.slotsX,
                        RucksackGui.firstItemX + x * 18,
                        RucksackGui.firstItemY + y * 18));
            }
        }
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inventory,
                        x + y * 9,
                        RucksackGui.playerFirstItemX + x * 18,
                        ((y == 0) ? RucksackGui.hotbarFirstItemY : (RucksackGui.playerFirstItemY - 18)) + y * 18));
            }
        }
    }

    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < RucksackGui.slotsY * RucksackGui.slotsX)
            {
                if (!this.mergeItemStack(itemstack1, RucksackGui.slotsY * RucksackGui.slotsX, this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, RucksackGui.slotsY * RucksackGui.slotsX, false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
