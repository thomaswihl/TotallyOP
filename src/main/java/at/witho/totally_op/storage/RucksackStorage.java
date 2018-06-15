package at.witho.totally_op.storage;

import at.witho.totally_op.gui.RucksackGui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class RucksackStorage extends InventoryBasic implements IInventoryChangedListener {
    public enum Page { Smeltables, Gems, Other, Tools };
    public static final int pages = 1;//Page.values().length;
    public static final int INVENTORY_SIZE = RucksackGui.slotsX * RucksackGui.slotsY * pages;
    public final NonNullList<ItemStack> filterTrash;
    public final NonNullList<ItemStack> filterCompress;
    public boolean whitelistTrash = true;
    public boolean whitelistCompress = true;

    private ItemStack invItem;


    public RucksackStorage(ItemStack stack) {
        super("Rucksack", false, INVENTORY_SIZE);
        invItem = stack;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        readFromNBT(invItem.getTagCompound());
        addInventoryChangeListener(this);
        filterTrash = NonNullList.<ItemStack>withSize(pages * RucksackGui.slotsX, ItemStack.EMPTY);
        filterCompress = NonNullList.<ItemStack>withSize(pages * RucksackGui.slotsY, ItemStack.EMPTY);
    }

    public boolean isItemStack(ItemStack in) {
        return invItem == in;
    }


    @Override
    public void onInventoryChanged(IInventory invBasic) {
        writeToNBT(invItem.getTagCompound());
    }

    @Override
    public ItemStack addItem(ItemStack stack) {
        return super.addItem(stack);
    }

    /**
     * A custom method to read our inventory from an ItemStack's NBT compound
     */
    public void readFromNBT(NBTTagCompound compound)
    {
        NBTTagList items = compound.getTagList("ItemInventory", compound.getId());
        for (int i = 0; i < items.tagCount(); ++i)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getInteger("Slot");
            if (slot >= 0 && slot < getSizeInventory()) setInventorySlotContents(slot, new ItemStack(item));
        }

        items = compound.getTagList("TrashInventory", compound.getId());
        for (int i = 0; i < items.tagCount(); ++i)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getInteger("Slot");
            if (slot >= 0 && slot < filterTrash.size()) filterTrash.set(slot, new ItemStack(item));
        }

        items = compound.getTagList("CompressInventory", compound.getId());
        for (int i = 0; i < items.tagCount(); ++i)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getInteger("Slot");
            if (slot >= 0 && slot < filterCompress.size()) filterCompress.set(slot, new ItemStack(item));
        }

        if (compound.hasKey("whitelistTrash")) whitelistTrash = compound.getBoolean("whitelistTrash");
        if (compound.hasKey("whitelistCompress")) whitelistCompress = compound.getBoolean("whitelistCompress");

    }

    /**
     * A custom method to write our inventory to an ItemStack's NBT compound
     */
    public void writeToNBT(NBTTagCompound compound)
    {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); ++i)
        {
            if (!getStackInSlot(i).isEmpty())
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", i);
                getStackInSlot(i).writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("ItemInventory", items);

        items = new NBTTagList();
        for (int i = 0; i < filterTrash.size(); ++i)
        {
            if (!filterTrash.get(i).isEmpty())
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", i);
                filterTrash.get(i).writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("TrashInventory", items);

        items = new NBTTagList();
        for (int i = 0; i < filterCompress.size(); ++i)
        {
            if (!filterCompress.get(i).isEmpty())
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", i);
                filterCompress.get(i).writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("CompressInventory", items);

        compound.setBoolean("whitelistTrash", whitelistTrash);
        compound.setBoolean("whitelistCompress", whitelistCompress);

    }
}
