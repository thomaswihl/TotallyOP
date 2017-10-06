package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.config.Config;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class TileFarmingFlower extends TileFunctionFlower {
    public TileFarmingFlower() {
        super();
    }

    @Override
	public void update() {
        super.update();
        if (!shouldRun()) return;
        if (currentPos == null) {
            checkForModifiers();
            resetPos();
            return;
        }

		IBlockState state = world.getBlockState(currentPos);
        HarvestInfo info = canHarvest(state);
        if (info != null) {
            if (info.harvestPos == null) info.harvestPos = currentPos;
            NonNullList<ItemStack> drops = NonNullList.create();
            Block block = state.getBlock();
            block.getDrops(drops, world, info.harvestPos, state, 0);
            world.setBlockToAir(info.harvestPos);
            boolean allIsSeed = info.seed != null;
            for (ItemStack stack : drops) {
                Item item = stack.getItem();
                if (allIsSeed && !stack.isItemEqual(info.seed)) allIsSeed = false;
                if ((info.seed == null || !stack.isItemEqual(info.seed)) && !info.ignoreFortune) {
                    stack.setCount(stack.getCount() * fortune);
                }
                world.spawnEntity(new EntityItem(world, info.harvestPos.getX(), info.harvestPos.getY(), info.harvestPos.getZ(), stack));
            }
            if (allIsSeed) {
                for (ItemStack stack : drops) {
                    stack.setCount(stack.getCount() * fortune - 1);
                    world.spawnEntity(new EntityItem(world, info.harvestPos.getX(), info.harvestPos.getY(), info.harvestPos.getZ(), stack));
                }
            }
            if (info.seed != null) {
                Item item = info.seed.getItem();
                if (item instanceof IPlantable) {
                    IPlantable seed = (IPlantable)item;
                    world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
                }
            }
		}
//		else {
//            Block block = state.getBlock();
//            if (block instanceof IGrowable) {
//                IGrowable crop = (IGrowable)block;
//                crop.grow(world, world.rand, currentPos, state);
//            }
//        }

		nextBlock();
	}

	class HarvestInfo {
        public ItemStack seed = null;
        public BlockPos harvestPos = null;
        public boolean ignoreFortune = false;
    }

	protected HarvestInfo canHarvest(IBlockState state) {
        NonNullList<ItemStack> drops = NonNullList.create();
        Block block = state.getBlock();
        if (block instanceof BlockCrops) {
            BlockCrops crop = (BlockCrops)block;
            if (crop.isMaxAge(state)) {
                HarvestInfo info = new HarvestInfo();
                info.seed = crop.getItem(world, pos, state);
                return info;
            }
        } else if (block instanceof BlockMelon || block instanceof BlockPumpkin) {
            return new HarvestInfo();
        } else if (block instanceof IPlantable) {
            // cactus and sugar cane
            int size = 0;
            BlockPos pos = currentPos;
            do {
                pos = pos.up();
                size++;
            }   while (world.getBlockState(pos).getBlock() == block);
            if (size > 1) {
                HarvestInfo info = new HarvestInfo();
                info.harvestPos = pos.down();
                info.ignoreFortune = true;
                return info;
            }
        } else if (block instanceof IGrowable) {
            // cocoa beans
            IGrowable growable = (IGrowable)block;
            if (growable.canGrow(world, pos, state, false)) return null;
            return new HarvestInfo();
        }

        return null;
    }
}
