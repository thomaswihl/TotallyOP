package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.util.CraftingUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.Level;

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
        double y = pos.getY();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(minX, y - rangeConfig[rangeTier], minZ, maxX + 1, y + rangeConfig[rangeTier] + 1, maxZ + 1));
        if (items.isEmpty()) return;
        if (!filter.isEmpty()) {
            for (Iterator<EntityItem> iter = items.iterator(); iter.hasNext();) {
                EntityItem item = iter.next();
                boolean match = item.getItem().isItemEqual(filter);
                if (filterIsWhitelist != match) iter.remove();
            }
        }
        List<EntityItem> moveItems = new ArrayList<EntityItem>();
        for (EntityItem entity : items) {
            ItemStack item = entity.getItem();
            Block block = CraftingUtils.itemToBlock.get(item.getItem());
            if (block != null && item.getCount() >= 9) {
                int blockCount = item.getCount() / 9;
                item.setCount(item.getCount() % 9);
                entity.setItem(item);
                EntityItem ei = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(block, blockCount));
                world.spawnEntity(ei);
                moveItems.add(ei);
            }
        }
        if (!findInventory(moveItems)) {
            for (EntityItem item : moveItems) {
                BlockPos p = pos.offset(facing, -1);
                item.setPosition(p.getX() + 0.5f, p.getY(), p.getZ() + 0.5f);
            }
        }
    }


    @Override
    protected void initLimits(int range) {
        super.initLimits(range);
        --minX;
        --minZ;
        ++maxX;
        ++maxZ;
    }

    private boolean findInventory(List<EntityItem> items) {
        int r = 1;
        for (BlockPos pos : BlockPos.getAllInBox(pos.add(-r, -r, -r), pos.add(r, r, r))) {
            TileEntity e = world.getTileEntity(pos);
            if (e != null) {
                IItemHandler inventory = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null && addToInventory(inventory, items)) return true;
            }
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
