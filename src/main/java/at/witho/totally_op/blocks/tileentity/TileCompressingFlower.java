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

public class TileCompressingFlower extends TileFunctionFlower {

    public TileCompressingFlower() {
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
                if (stack.getCount() >= 9) {
                    ItemStack output = CraftingUtils.toBlock(stack);
                    if (output != null) {
                        int count = stack.getCount() / 9;
                        output = output.copy();
                        output.setCount(count);

                        stack.setCount(stack.getCount() % 9);
                        entity.setItem(stack);
                        EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, output);
                        ei.motionX = ei.motionY = ei.motionZ = 0;
                        moveItems.add(ei);
                    }
                }
            }
        } else {
            for (IItemHandler inventory : inventories) {
                for (int i = 0; i < inventory.getSlots(); ++i) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack.getCount() >= 9) {
                        ItemStack output = CraftingUtils.toBlock(stack);
                        if (output != null) {
                            int count = stack.getCount() / 9;
                            output = output.copy();
                            output.setCount(count);
                            ItemStack move = inventory.extractItem(i, count * 9, false);
                            if (!move.isEmpty()) {
                                EntityItem ei = new EntityItem(world, outputPos.getX() + 0.5, outputPos.getY(), outputPos.getZ() + 0.5, output);
                                ei.motionX = ei.motionY = ei.motionZ = 0;
                                moveItems.add(ei);
                            }
                        }
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

    private boolean addToInventory(IItemHandler inventory, List<EntityItem> items) {
        for (int i = 0; i < inventory.getSlots(); ++i) {
            boolean allDone = true;
            for (EntityItem item : items) {
                ItemStack itemStack = item.getItem();
                if (itemStack.isEmpty()) continue;
                ItemStack remain = inventory.insertItem(i, itemStack.copy(), false);
                itemStack.setCount(remain.getCount());
                if (remain.getCount() != 0) allDone = false;
            }
            if (allDone) return true;
        }
        return false;
    }
}
