package at.witho.totally_op.storage;

import at.witho.totally_op.items.Rucksack;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class RucksackSlot extends Slot {

    RucksackSlot(RucksackStorage inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    public boolean isItemValid(ItemStack stack)
    {
        if (stack.getItem() instanceof Rucksack) return false;
        return true;
    }
}
