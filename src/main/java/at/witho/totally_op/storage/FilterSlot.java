package at.witho.totally_op.storage;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.SlotItemHandler;

public class FilterSlot extends Slot {
    RucksackStorage rucksack;
    NonNullList<ItemStack> list;
    int index;
    public FilterSlot(RucksackStorage rucksack, NonNullList<ItemStack> list, int index, int xPosition, int yPosition) {
        super(null, index, xPosition, yPosition);
        this.rucksack = rucksack;
        this.list = list;
        this.index = index;
    }

    @Override
    public ItemStack getStack()
    {
        return list.get(index);
    }

    @Override
    public void putStack(ItemStack stack)
    {
        list.set(index, stack);
        rucksack.markDirty();
    }

    @Override
    public void onSlotChanged()
    {
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        list.set(index, ItemStack.EMPTY);
        rucksack.markDirty();
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn)
    {
        return false;
    }

    @Override
    public boolean isSameInventory(Slot other)
    {
        return false;
    }

}
