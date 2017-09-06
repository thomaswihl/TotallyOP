package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.FunctionFlower;
import at.witho.totally_op.blocks.TierableBlock;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public abstract class TileFunctionFlower extends TileEntity implements ITickable {
    public static final int[] DEFAULT_FORTUNE_CONFIG = {1, 2, 3, 4, 5, 6, 7};
    public static final int[] DEFAULT_EFFICIENCY_CONFIG = {80, 40, 20, 8, 4, 2, 1};
    public static final int[] DEFAULT_RANGE_CONFIG = {1, 3, 5, 7, 9, 11, 15};

    protected int[] fortuneConfig = DEFAULT_FORTUNE_CONFIG;
    protected int[] efficiencyConfig = DEFAULT_EFFICIENCY_CONFIG;
    protected int[] rangeConfig = DEFAULT_RANGE_CONFIG;

    protected int fortuneTier = 0;
    protected int efficiencyTier = 0;
    protected int rangeTier = 0;

    protected int fortune = 1;
    protected int efficiency = 1;
    protected int range = 0;
    protected EnumFacing facing = EnumFacing.NORTH;

    protected int minX = 0;
	protected int maxX = 0;
	protected int minZ = 0;
	protected int maxZ = 0;

	public TileFunctionFlower() {
        fortuneConfig = Config.intArray(Config.fortuneMultiplier.getStringList());
        efficiencyConfig = Config.intArray(Config.efficiencyDelay.getStringList());
        rangeConfig = Config.intArray(Config.farmingRange.getStringList());
        initLimits(range);
    }

    public int getFortune() {
        return fortune;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public int getRange() {
        return range;
    }

    protected void initLimits(int range) {
		minX = maxX = pos.getX();
		minZ = maxZ = pos.getZ();
		if (world != null) facing = world.getBlockState(pos).getValue(FunctionFlower.FACING);
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
        fortuneTier = 0;
        efficiencyTier = 0;
        rangeTier = 0;
        for (int i = 0; i < 4; ++i) {
            p = p.down();
            IBlockState state = world.getBlockState(p);
            Block block = state.getBlock();
            if (block instanceof TierableBlock) {
                TierableBlock e = (TierableBlock)block;
                int tier = e.getTier(state);
                if (block.getRegistryName().getResourcePath() == TierableBlock.FORTUNE)
                    fortuneTier = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.EFFICIENCY)
                    efficiencyTier = tier;
                else if (block.getRegistryName().getResourcePath() == TierableBlock.RANGE)
                    rangeTier = tier;
            }
        }
        int newRange = rangeConfig[rangeTier];
        if (range != newRange) {
            range = newRange;
            initLimits(range);
        }
        efficiency = efficiencyConfig[efficiencyTier];
        fortune = fortuneConfig[fortuneTier];
    }

}
