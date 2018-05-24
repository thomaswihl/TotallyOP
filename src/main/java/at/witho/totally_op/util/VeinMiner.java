package at.witho.totally_op.util;

import at.witho.totally_op.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.ConcurrentLinkedQueue;

public class VeinMiner {
    private ConcurrentLinkedQueue<BlockPos> blockPositionsToBreak = new ConcurrentLinkedQueue<BlockPos>();
    private Block block = null;
    private EntityPlayerMP player = null;
    private World world = null;
    private boolean horizontalPlane = false;

    public VeinMiner(World world, EntityPlayerMP player, Block block) {
        this.world = world;
        this.player = player;
        this.block = block;
    }

    public void setHorizontalPlane(boolean plane) { horizontalPlane = plane; }

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
                        if (Helper.isSameBlock(pstate.getBlock(), block)) blockPositionsToBreak.add(p);
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
                    world.destroyBlock(p, true);
                    addBlock(p);
                }
                break;
            }
        }
        return true;
    }
}
