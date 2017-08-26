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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public abstract class TileFunctionFlower extends TileEntity implements ITickable {

	protected int fortune = 0;
    protected int efficiency = 0;

	protected int minX = 0;
	protected int maxX = 0;
	protected int minZ = 0;
	protected int maxZ = 0;


	protected void initLimits(int range) {
		minX = maxX = pos.getX();
		minZ = maxZ = pos.getZ();
		EnumFacing facing = world.getBlockState(pos).getValue(FunctionFlower.FACING);
		switch (facing) {
		case EAST: // +x
			minX++;
			maxX += range;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case WEST: // -x
			minX -= range;
			maxX--;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case NORTH: // -z
			minX -= range / 2;
			maxX += range / 2;
			minZ -= range;
			maxZ--;
			break;
		case SOUTH: // +z
			minX -= range / 2;
			maxX += range / 2;
			minZ++;
			maxZ += range;
			break;
		default:
			break;
		}
	}

    protected void checkForModifiers() {
        BlockPos p = getPos().down();
        fortune = 0;
        efficiency = 0;
        for (int i = 0; i < 2; ++i) {
            IBlockState state = world.getBlockState(p);
            Block block = state.getBlock();
            if (block instanceof Efficiency) {
                Efficiency e = (Efficiency)block;
                efficiency = e.getTier();
            }
            else if (block instanceof Fortune) {
                Fortune f = (Fortune)block;
                fortune = f.getTier();
            }
        }
    }

}
