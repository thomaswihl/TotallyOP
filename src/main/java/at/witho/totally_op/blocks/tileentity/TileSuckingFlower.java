package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.Efficiency;
import at.witho.totally_op.blocks.Fortune;
import at.witho.totally_op.blocks.FunctionFlower;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraftforge.common.IPlantable;

import java.util.List;

public class TileSuckingFlower extends TileFunctionFlower {

    private int counter = 0;
    private int efficiencyOld = -1;
    private int[] range = null;

    @Override
	public void update() {
        if (world.isRemote) return;
        if (counter < 20) {
            ++counter;
            return;
        }
        counter = 0;
        checkForModifiers();
        if (efficiencyOld != efficiency) {
            efficiencyOld = efficiency;
            if (range == null) {
                range = Config.intArray(Config.suckingRange.getStringList());
            }
            initLimits(range[efficiency]);
            --minX;
            --minZ;
            ++maxX;
            ++maxZ;
        }
        double y = pos.getY();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(minX, y - range[efficiency], minZ, maxX + 1, y + range[efficiency] + 1, maxZ + 1));
        if (items.isEmpty()) return;
        if (!findInventory(items)) {
            for (EntityItem item : items) {
                item.setPosition(pos.getX(), pos.getY(), pos.getZ());
            }
        }
    }

    private boolean findInventory(List<EntityItem> items) {
        int r = 1;
        for(BlockPos pos : BlockPos.getAllInBox(pos.add(-r, -r, -r), pos.add(r, r, r))) {
            TileEntity entity = world.getTileEntity(pos);
            if (entity == null || !(entity instanceof IInventory)) continue;
            IInventory inventory = (IInventory)entity;
            if (addToInventory(inventory, items)) return true;
        }
        return false;
    }

    private boolean addToInventory(IInventory inventory, List<EntityItem> items) {
        for (int i = 0; i < inventory.getSizeInventory(); ++i) {
            boolean allDone = true;
            for (EntityItem item : items) {
                ItemStack itemStack = item.getItem();
                if (itemStack.isEmpty()) continue;
                ItemStack invStack = inventory.getStackInSlot(i);
                if (invStack.isEmpty()) {
                    inventory.setInventorySlotContents(i, itemStack.copy());
                    itemStack.setCount(0);
                }
                else if (invStack.isItemEqual(itemStack)) {
                    int used = invStack.getCount();
                    int free = invStack.getMaxStackSize() - used;
                    if (free > 0) {
                        int add = Math.min(itemStack.getCount(), free);
                        invStack.setCount(invStack.getCount() + add);
                        itemStack.setCount(itemStack.getCount() - add);
                        if (!itemStack.isEmpty()) allDone = false;
                    }
                    else {
                        allDone = false;
                    }
                }
                else {
                    allDone = false;
                }
            }
            if (allDone) return true;
        }
        return false;
    }
}