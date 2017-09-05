package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.FunctionFlower;
import at.witho.totally_op.blocks.TierableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public abstract class TileFunctionFlower extends TileEntity implements ITickable {

	protected int fortune = 0;
    protected int efficiency = 0;
    protected int range = 0;

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
        BlockPos p = getPos();
        fortune = 0;
        efficiency = 0;
        for (int i = 0; i < 3; ++i) {
            p = p.down();
            IBlockState state = world.getBlockState(p);
            Block block = state.getBlock();
            if (block instanceof TierableBlock) {
                TierableBlock e = (TierableBlock)block;
                int tier = e.getTier(state);
                if (block.getRegistryName().getResourcePath() == TierableBlock.FORTUNE)
                    fortune = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.EFFICIENCY)
                    efficiency = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.RANGE)
                    range = tier;
            }
        }
    }

}
