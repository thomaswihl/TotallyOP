package at.witho.totally_op.storage;

import at.witho.totally_op.gui.RucksackGui;
import com.sun.xml.internal.ws.api.server.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;

public class RucksackStorage extends InventoryBasic implements IInventoryChangedListener {
    public static final int INVENTORY_SIZE = RucksackGui.slotsX * RucksackGui.slotsY;
    private ItemStack invItem;

    public RucksackStorage(ItemStack stack) {
        super("Rucksack", false, INVENTORY_SIZE);
        invItem = stack;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        readFromNBT(invItem.getTagCompound());
        addInventoryChangeListener(this);
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        writeToNBT(invItem.getTagCompound());
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

            // Just double-checking that the saved slot index is within our inventory array bounds
            if (slot >= 0 && slot < getSizeInventory()) {
                setInventorySlotContents(slot, new ItemStack(item));
            }
        }
    }

    /**
     * A custom method to write our inventory to an ItemStack's NBT compound
     */
    public void writeToNBT(NBTTagCompound compound)
    {
        // Create a new NBT Tag List to store itemstacks as NBT Tags
        NBTTagList items = new NBTTagList();

        for (int i = 0; i < getSizeInventory(); ++i)
        {
            // Only write stacks that contain items
            if (!getStackInSlot(i).isEmpty())
            {
                // Make a new NBT Tag Compound to write the itemstack and slot index to
                NBTTagCompound item = new NBTTagCompound();
                item.setInteger("Slot", i);
                // Writes the itemstack in slot(i) to the Tag Compound we just made
                getStackInSlot(i).writeToNBT(item);

                // add the tag compound to our tag list
                items.appendTag(item);
            }
        }
        // Add the TagList to the ItemStack's Tag Compound with the name "ItemInventory"
        compound.setTag("ItemInventory", items);
    }
}
