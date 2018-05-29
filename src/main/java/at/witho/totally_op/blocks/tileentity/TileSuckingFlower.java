package at.witho.totally_op.blocks.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import java.util.Iterator;
import java.util.List;

public class TileSuckingFlower extends TileFunctionFlower {
    public TileSuckingFlower() {
        super();
    }

    @Override
	public void update() {
        super.update();
        if (!shouldRun()) return;
        double y = pos.getY();
        List<EntityItem> items = filteredInputItems();
        if (items.isEmpty()) return;
        if (!addToInventories(items)) {
            for (EntityItem item : items) {
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

    private boolean addToInventories(List<EntityItem> items) {
        List<IItemHandler> inventories = backInventories();
        for (IItemHandler inventory : inventories) {
            if (addToInventory(inventory, items)) return true;
        }
        return false;
    }

}
