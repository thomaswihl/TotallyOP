package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.Efficiency;
import at.witho.totally_op.blocks.Fortune;
import at.witho.totally_op.blocks.FunctionFlower;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
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
        for(EntityItem item : items) {
            item.setPosition(pos.getX(), pos.getY(), pos.getZ());
        }
	}
}
