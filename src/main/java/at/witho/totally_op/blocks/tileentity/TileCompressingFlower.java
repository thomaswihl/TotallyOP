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

public class TileCompressingFlower extends TileFunctionFlower implements TileFunctionFlower.Transform {

    public TileCompressingFlower() {
        super();
    }

    @Override
    public void update() {
        super.update();
        if (!shouldRun()) return;
        transformItems(this);
    }

    @Override
    protected void initLimits(int range) {
        super.initLimits(1);
    }

    @Override
    public ItemStack transform(ItemStack from) {
        return CraftingUtils.toBlock(from);
    }

    @Override
    public boolean canTransform(ItemStack from) {
        return CraftingUtils.canToBlock(from);
    }

}
