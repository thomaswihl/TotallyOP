package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.blocks.FarmingFlower;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.util.PrintClassHierarchy;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import org.apache.logging.log4j.Level;

import java.util.List;
import java.util.Random;

import static sun.security.krb5.Confounder.intValue;

public class TileFarmingFlower extends TileFunctionFlower {
    public static final int[] DEFAULT_GROW_PROBABILITY_CONFIG = {0, 2, 4, 8, 16, 32, 64 };
    protected int[] growProbabilityConfig = DEFAULT_GROW_PROBABILITY_CONFIG;
    private int growProbability = 0;

    public TileFarmingFlower() {
        super();
        growProbabilityConfig = Config.intArray(Config.growProbability.getStringList());
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
            Block block = state.getBlock();
            NonNullList<ItemStack> drops = NonNullList.create();
            // New way isn't supported by all
            // block.getDrops(drops, world, info.harvestPos, state, 0);
            // so use the old way:
            List<ItemStack> list = block.getDrops(world, currentPos, state, 0);
            if (list != null) {
                drops.addAll(list);
                world.setBlockToAir(info.harvestPos);
                boolean allIsSeed = info.seed != null;
                boolean seedRemoved = false;
                for (ItemStack stack : drops) {
                    Item item = stack.getItem();
                    if (allIsSeed && !stack.isItemEqual(info.seed)) allIsSeed = false;
                    if ((info.seed == null || !stack.isItemEqual(info.seed)) && !info.ignoreFortune) {
                        stack.setCount(stack.getCount() * fortune);
                    }
                    if (info.seed != null && !seedRemoved && stack.isItemEqual(info.seed)) {
                        stack.setCount(stack.getCount() - 1);
                        seedRemoved = true;
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
                        IPlantable seed = (IPlantable) item;
                        world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
                    } else {
                        Block b = Block.getBlockFromItem(item);
                        if (b instanceof BlockBush) {
                            BlockBush bush = (BlockBush)b;
                            if (bush.canPlaceBlockAt(world, currentPos)) {
                                world.setBlockState(currentPos, bush.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
		}
		else {
            growProbability = growProbabilityConfig[efficiencyTier];
            if (growProbability > 0 && world.rand.nextInt(1000) < growProbability) {
                Block block = state.getBlock();
                if (block instanceof IGrowable) {
                    IGrowable crop = (IGrowable) block;
                    crop.grow(world, world.rand, currentPos, state);
                } else if (block == Blocks.NETHER_WART) {
                    int i = state.getValue(BlockNetherWart.AGE).intValue();
                    if (i < 3) {
                        IBlockState newState = state.withProperty(BlockNetherWart.AGE, Integer.valueOf(i + 1));
                        world.setBlockState(currentPos, newState, 2);
                    }
                }
            }
        }

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
        } else if (block instanceof IGrowable) {
            // cocoa beans
            IGrowable growable = (IGrowable)block;
            if (growable.canGrow(world, pos, state, false)) return null;
            // No melon or pumpkin stems
            if (block instanceof BlockStem) return null;
            HarvestInfo info = new HarvestInfo();
            info.seed = new ItemStack(block.getItemDropped(state, world.rand, 0));
            return info;
        } else if (block instanceof IPlantable) {
            // cactus and sugar cane
            int size = 0;
            BlockPos pos = currentPos;
            do {
                pos = pos.up();
                size++;
            } while (world.getBlockState(pos).getBlock() == block);
            if (size > 1) {
                HarvestInfo info = new HarvestInfo();
                info.harvestPos = pos.down();
                info.ignoreFortune = true;
                return info;
            }
        } else if (block.equals(Blocks.NETHER_WART)) {
            BlockNetherWart nw = (BlockNetherWart)block;
            if (state.getValue(BlockNetherWart.AGE).intValue() >= 3) {
                HarvestInfo info = new HarvestInfo();
                info.seed = new ItemStack(block.getItemDropped(state, world.rand, 0));
                return info;
            }
        }

        return null;
    }
}
