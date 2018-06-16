package at.witho.totally_op.storage;

import at.witho.totally_op.gui.RucksackGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import invtweaks.api.container.ChestContainer;

@ChestContainer
public class RucksackContainer extends Container {
    public RucksackStorage rucksack;

    public RucksackContainer(InventoryPlayer inventory, RucksackStorage rucksack) {
        this.rucksack = rucksack;
        for (int y = 0; y < RucksackGui.slotsY; y++) {
            for (int x = 0; x < RucksackGui.slotsX; x++) {
                addSlotToContainer(new RucksackSlot(rucksack,
                        x + y * RucksackGui.slotsX,
                        RucksackGui.firstItemX + x * RucksackGui.slotWidth,
                        RucksackGui.firstItemY + y * RucksackGui.slotHeight));
            }
        }
        for (int i = 0; i < RucksackGui.slotsX; i++) {
            addSlotToContainer(new FilterSlot(rucksack, rucksack.filterTrash,
                    i,
                    RucksackGui.trashX + i * RucksackGui.slotWidth,
                    RucksackGui.trashY));
        }
        for (int i = 0; i < RucksackGui.slotsY; i++) {
            addSlotToContainer(new FilterSlot(rucksack, rucksack.filterCompress,
                    i,
                    RucksackGui.compressX,
                    RucksackGui.compressY + i * RucksackGui.slotHeight));
        }
        addSlotToContainer(new Slot(inventory,
                40,
                RucksackGui.offhandItemX,
                RucksackGui.playerFirstItemY));
        for(int y = 0; y < 4; y++) {
            for(int x = 0; x < 9; x++) {
                addSlotToContainer(new Slot(inventory,
                        x + y * 9,
                        RucksackGui.playerFirstItemX + x * RucksackGui.slotWidth,
                        ((y == 0) ? RucksackGui.hotbarFirstItemY : (RucksackGui.playerFirstItemY - RucksackGui.slotWidth)) + y * RucksackGui.slotHeight));
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

    @ChestContainer.RowSizeCallback
    public int getRowSize() { return RucksackGui.slotsX; }
    @ChestContainer.IsLargeCallback
    boolean isLargeChest()  { return true; }

}
