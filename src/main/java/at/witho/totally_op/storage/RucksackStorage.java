package at.witho.totally_op.storage;

import com.sun.xml.internal.ws.api.server.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public class RucksackStorage extends InventoryBasic {
    public static final int INVENTORY_SIZE = 36;
    private ItemStack invItem;

    public RucksackStorage(ItemStack stack) {
        super("Rucksack", false, INVENTORY_SIZE);
        invItem = stack;
    }

}
