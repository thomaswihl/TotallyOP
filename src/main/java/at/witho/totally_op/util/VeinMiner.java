package at.witho.totally_op.util;

import at.witho.totally_op.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VeinMiner {
    private ConcurrentLinkedQueue<BlockPos> blockPositionsToBreak = new ConcurrentLinkedQueue<BlockPos>();
    private Block block = null;
    private List<Block> extraBlocks = new ArrayList<>();
    private EntityPlayerMP player = null;
    private World world = null;
    private boolean horizontalPlane = false;
    private int fortune = 0;

    public interface ShouldBreakBlock {
        boolean test(Block block);
    }
    private ShouldBreakBlock shouldBreakBlock = null;

    public VeinMiner(World world, EntityPlayerMP player, Block block) {
        this.world = world;
        this.player = player;
        this.block = block;
    }

    public void setHorizontalPlane(boolean plane) { horizontalPlane = plane; }
    public void setShouldBreakBlock(ShouldBreakBlock sbb) { shouldBreakBlock = sbb; }
    public void setFortune(int f) { fortune = f; }

    public void addBlock(BlockPos pos) {
        int y1 = -1;
        int y2 = 2;
        if (horizontalPlane) {
            y1 = 0;
            y2 = 1;
        }
        for (int y = y1; y < y2; ++y) {
            for (int x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    if (x != 0 || y != 0 || z != 0) {
                        BlockPos p = pos.add(x, y, z);
                        IBlockState pstate = world.getBlockState(p);
                        Block b = pstate.getBlock();
                        if (Helper.isSameBlock(b, block)) blockPositionsToBreak.add(p);
                        else {
                            boolean found = false;
                            for (Block extra : extraBlocks) {
                                if (Helper.isSameBlock(b, extra)) {
                                    blockPositionsToBreak.add(p);
                                    found = true;
                                    break;
                                }
                            }
                            if (!found && shouldBreakBlock != null && shouldBreakBlock.test(b)) {
                                extraBlocks.add(b);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean harvestBlock() {
        BlockPos p;
        if (blockPositionsToBreak.isEmpty()) {
            return false;
        }
        while ((p = blockPositionsToBreak.poll()) != null) {
            IBlockState pstate = world.getBlockState(p);
            if (pstate.getBlock() != Blocks.AIR) {
                if (player != null) player.interactionManager.tryHarvestBlock(p);
                else {
                    Block b = pstate.getBlock();
                    world.playEvent(2001, p, Block.getStateId(pstate));
                    List<ItemStack> drops = b.getDrops(world, p, pstate, 0);
                    for (ItemStack stack : drops) {
                        if (!Helper.isSameBlock(b, block)) stack.setCount(stack.getCount() * fortune);
                        world.spawnEntity(new EntityItem(world, p.getX(), p.getY(), p.getZ(), stack));
                    }
                    world.setBlockState(p, Blocks.AIR.getDefaultState(), 3);

                    addBlock(p);
                }
                break;
            }
        }
        return true;
    }
}
