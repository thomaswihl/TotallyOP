package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.config.Config;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TileSuckingFlower extends TileFunctionFlower {

    @Override
	public void update() {
        super.update();
        if (!shouldRun()) return;
        double y = pos.getY();
        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class,
                new AxisAlignedBB(minX, y - rangeConfig[rangeTier], minZ, maxX + 1, y + rangeConfig[rangeTier] + 1, maxZ + 1));
        if (items.isEmpty()) return;
        if (!findInventory(items)) {
            for (EntityItem item : items) {
                BlockPos p = new BlockPos(pos.getX() - facing.getFrontOffsetX(), pos.getY(), pos.getZ() - facing.getFrontOffsetZ());
                item.setPosition(p.getX(), p.getY(), p.getZ());
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
