package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.Level;

public class TileFarmingFlower extends TileFunctionFlower {
    protected BlockPos currentPos = null;

    protected int[] fortuneMultiplier;
    protected int[] efficiencyDelay;
    protected int[] efficiencyRange;
    protected int delay = 0;
    protected int range = 0;

    @Override
	public void update() {
		if (!shouldRun()) return;
        if (currentPos == null) {
            resetPos();
        }

		IBlockState state = world.getBlockState(currentPos);
        if (canHarvest(state)) {
            NonNullList<ItemStack> drops = NonNullList.create();
            Block block = state.getBlock();
            block.getDrops(drops, world, currentPos, state, 0);
            world.setBlockToAir(currentPos);
            boolean planted = false;
            for (ItemStack stack : drops) {
                Item item = stack.getItem();
                if (!planted && item instanceof IPlantable) {
                    IPlantable seed = (IPlantable)item;
                    world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
                    stack.setCount(stack.getCount() - 1);
                    planted = true;
                } else {
                    stack.setCount(stack.getCount() * fortuneMultiplier[fortune]);
                }
                world.spawnEntity(new EntityItem(world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack));
            }
            /* We didn't find a seed try to get one from the block */
            if (!planted && block instanceof BlockCrops) {
                BlockCrops crop = (BlockCrops)block;
                ItemStack stack = crop.getItem(world, currentPos, state);
                Item item = stack.getItem();
                if (item instanceof IPlantable) {
                    IPlantable seed = (IPlantable)item;
                    world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
                }
            }
		} else {
            Block block = state.getBlock();
            if (block instanceof BlockCrops) {
                BlockCrops crop = (BlockCrops) block;
                crop.grow(world, currentPos, state);
            }
        }

		nextBlock();
	}

	protected boolean canHarvest(IBlockState state){
        NonNullList<ItemStack> drops = NonNullList.create();
        Block block = state.getBlock();
        if (block instanceof BlockCrops) {
            BlockCrops crop = (BlockCrops)block;
            if (crop.isMaxAge(state)) return true;
        } else if (block instanceof BlockMelon || block instanceof BlockPumpkin) {
            return true;
        }
        return false;
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
        efficiencyDelay = Config.intArray(Config.efficiencyDelay.getStringList());
        fortuneMultiplier = Config.intArray(Config.fortuneMultiplier.getStringList());
        efficiencyRange = Config.intArray(Config.farmingRange.getStringList());
        range = efficiencyRange[efficiency];
        initLimits(range);
        currentPos = new BlockPos(minX, pos.getY(), minZ);
        TotallyOP.logger.log(Level.ERROR, "range = " + range + ", delay = " + efficiencyDelay[efficiency] + ", multi = " + fortuneMultiplier[fortune]);
    }

    protected void nextBlock() {
        currentPos = currentPos.east();
        if (currentPos.getX() > maxX) {
            currentPos = currentPos.add(-range, 0, 1);
            if (currentPos.getZ() > maxZ) resetPos();
        }
    }


}
