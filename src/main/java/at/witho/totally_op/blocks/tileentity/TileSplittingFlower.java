package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.util.CraftingUtils;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileSplittingFlower extends TileFunctionFlower {
    private int lastSlot = 0;
    private int lastInventory = 0;

    public TileSplittingFlower() {
        super();
    }

    @Override
    public void update() {
        super.update();
        if (!shouldRun()) return;
        List<IItemHandler> inventories = frontInventories();
        List<EntityItem> moveItems = new ArrayList<EntityItem>();
        BlockPos outputPos = pos.offset(facing, -1);
        if (inventories.isEmpty()) {
            double y = pos.getY();
            List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                    new AxisAlignedBB(minX, y, minZ, maxX + 1, y + 1, maxZ + 1));
            if (items.isEmpty()) return;
            if (!filter.isEmpty()) {
                for (Iterator<EntityItem> iter = items.iterator(); iter.hasNext(); ) {
                    EntityItem item = iter.next();
                    boolean match = item.getItem().isItemEqual(filter);
                    if (filterIsWhitelist != match) iter.remove();
                }
            }
            for (EntityItem entity : items) {
                ItemStack stack = entity.getItem();
                int count = 9;
                ItemStack output = CraftingUtils.toItem9(stack);
                if (output == null) {
                    output = CraftingUtils.toItem4(stack);
                    count = 4;
                }
                if (output != null) {
                    if (stack.getCount() >= count) {
                        int c = stack.getCount() / count;
                        output = output.copy();
                        output.setCount(c);

                        stack.setCount(stack.getCount() % count);
                        entity.setItem(stack);
                        EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, output);
                        ei.motionX = ei.motionY = ei.motionZ = 0;
                        moveItems.add(ei);
                    }
                }
            }
        } else {
            for (IItemHandler inventory : inventories) {
                ItemStack inputItem = null;
                ItemStack outputItem = null;
                int inputMultiple = 0;
                int inputAmount = 0;
                for (int i = lastSlot; i < inventory.getSlots(); ++i) {
                    ItemStack thisItem = inventory.getStackInSlot(i);
                    if (inputItem == null && thisItem != null && thisItem.getCount() > 0) {
                        inputItem = thisItem.copy();
                        inputMultiple = 9;
                        outputItem = CraftingUtils.toItem9(inputItem);
                        if (outputItem == null) {
                            outputItem = CraftingUtils.toItem4(inputItem);
                            inputMultiple = 4;
                            if (outputItem == null) inputItem = null;
                        }
                        if (inputItem != null) lastSlot = i;
                    }
                    if (outputItem != null && thisItem.isItemEqual(inputItem)) {
                        ItemStack move = inventory.extractItem(i, thisItem.getCount(), false);
                        inputAmount += move.getCount();
                    }
                }
                if (inputMultiple > 0 && inputAmount > 0) {
                    int c = inputAmount * inputMultiple;
                    outputItem = outputItem.copy();
                    outputItem.setCount(c);
                    EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, outputItem);
                    ei.motionX = ei.motionY = ei.motionZ = 0;
                    moveItems.add(ei);
                } else {
                    lastSlot = inventory.getSlots();

                    if (lastSlot >= inventory.getSlots()) {
                        lastSlot = 0;
                        lastInventory++;
                        if (lastInventory >= inventories.size()) lastInventory = 0;
                    }
                }

            }
        }
        if (!findInventory(moveItems)) {
            for (EntityItem item : moveItems) {
                world.spawnEntity(item);
            }
        }
    }

    @Override
    protected void initLimits(int range) {
        super.initLimits(1);
    }

    private boolean findInventory(List<EntityItem> items) {
        List<IItemHandler> inventories = backInventories();
        for (IItemHandler inventory : inventories) {
            if (addToInventory(inventory, items)) return true;
        }
        return false;
    }

}
