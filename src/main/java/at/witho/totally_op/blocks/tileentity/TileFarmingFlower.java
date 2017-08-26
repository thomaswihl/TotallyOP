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

public class TileFarmingFlower extends TileFunctionFlower {
    protected BlockPos currentPos = null;
    private int range = 9;

    protected int[] fortuneMultiplier;
    protected int[] efficiencyDelay;
    protected int delay = 0;

    @Override
	public void update() {
		if (!shouldRun()) return;
        if (currentPos == null) {
            initLimits(range);
            resetPos();
        }

		IBlockState state = world.getBlockState(currentPos);
		Block block = state.getBlock();
		if (block instanceof BlockCrops) {
			BlockCrops crop = (BlockCrops)block;
			if (crop.isMaxAge(state)) {
				NonNullList<ItemStack> drops = NonNullList.create();
				world.setBlockToAir(currentPos);
				boolean planted = false;
				block.getDrops(drops, world, currentPos, state, 0);
				for (ItemStack stack : drops) {
					world.spawnEntity(new EntityItem(world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack));
					if (planted) continue;
					Item item = stack.getItem();
					if (item instanceof IPlantable) {
						IPlantable seed = (IPlantable)item;
						world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
						stack.setCount(stack.getCount() - 1);
						planted = true;
					} else {
						stack.setCount(stack.getCount() * fortuneMultiplier[fortune]);
					}
				}
				/* We didn't find a seed try 3 more times (in case the god of RNG hates us) with fortune 10 (in case he really hates us) to get one */
				for (int i = 0; i < 3 && !planted; ++i) {
					block.getDrops(drops, world, currentPos, state, 10);
					for (ItemStack stack : drops) {
						Item item = stack.getItem();
						if (item instanceof IPlantable) {
							IPlantable seed = (IPlantable)item;
							world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
							planted = true;
							break;
						}
					}
				}
			}
		}

		nextBlock();
	}

    protected boolean shouldRun() {
        if (world.isRemote) return false;
        if (efficiencyDelay != null && delay < efficiencyDelay[efficiency]) {
            ++delay;
            return false;
        }
        delay = 0;
        return true;
    }

    protected void resetPos() {
        checkForModifiers();
        currentPos = new BlockPos(minX, pos.getY(), minZ);
        efficiencyDelay = Config.intArray(Config.efficiencyDelay.getStringList());
        fortuneMultiplier = Config.intArray(Config.fortuneMultiplier.getStringList());
    }

    protected void nextBlock() {
        currentPos = currentPos.east();
        if (currentPos.getX() > maxX) {
            currentPos = currentPos.add(-range, 0, 1);
            if (currentPos.getZ() > maxZ) resetPos();
        }
    }


}
