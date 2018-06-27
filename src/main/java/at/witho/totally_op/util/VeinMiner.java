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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VeinMiner {
    class PosInfo {
        public BlockPos pos;
        public byte fromX, fromY, fromZ;
        public PosInfo(BlockPos pos, byte fromX, byte fromY, byte fromZ) {
            this.pos = pos;
            this.fromX = fromX;
            this.fromY = fromY;
            this.fromZ = fromZ;
        }
        public PosInfo(BlockPos pos) {
            this.pos = pos;
            fromX = fromY = fromZ = 0;
        }
    }
    private ArrayDeque<PosInfo> blockPositionsToBreak = new ArrayDeque<PosInfo>();
    private List<Block> blocks = new ArrayList<>();
    private List<Block> extraBlocks = new ArrayList<>();
    private EntityPlayerMP player = null;
    private World world = null;
    private boolean horizontalPlane = false;
    private int fortune = 0;

    public interface ShouldAddBlock {
        boolean testIsSimilar(Block block);
        boolean testIsExtra(Block block);
    }
    private ShouldAddBlock shouldAddBlock = null;

    public VeinMiner(World world, EntityPlayerMP player, Block block) {
        this.world = world;
        this.player = player;
        this.blocks.add(block);
    }

    public void setHorizontalPlane(boolean plane) { horizontalPlane = plane; }
    public void setShouldAddBlock(ShouldAddBlock sbb) { shouldAddBlock = sbb; }
    public void setFortune(int f) { fortune = f; }

    private boolean findAndAdd(Block b, List<Block> list, PosInfo pi) {
        for (Block block : list) {
            if (Helper.isSameBlock(b, block)) {
                blockPositionsToBreak.add(pi);
                return true;
            }
        }
        return false;
    }

    public void addBlock(BlockPos pos) {
        addBlock(new PosInfo(pos));
    }

    protected void addBlock(PosInfo piFrom) {
        byte x1 = -1;
        byte x2 = 1;
        byte y1 = -1;
        byte y2 = 1;
        byte z1 = -1;
        byte z2 = 1;
        if (piFrom.fromX != 0 && piFrom.fromY == 0 && piFrom.fromZ == 0) {
            x1 = x2 = (byte)-piFrom.fromX;
        }
        if (piFrom.fromX == 0 && piFrom.fromY != 0 && piFrom.fromZ == 0) {
            y1 = y2 = (byte)-piFrom.fromY;
        }
        if (piFrom.fromX == 0 && piFrom.fromY == 0 && piFrom.fromZ != 0) {
            z1 = z2 = (byte)-piFrom.fromZ;
        }
        if (horizontalPlane) {
            y1 = 0;
            y2 = 0;
        }
        BlockPos pos = piFrom.pos;
        PosInfo pi = new PosInfo(pos);
        for (byte y = y1; y <= y2; ++y) {
            for (byte x = x1; x <= x2; ++x) {
                for (byte z = z1; z <= z2; ++z) {
                    if (x != 0 || y != 0 || z != 0) {
                        BlockPos p = pos.add(x, y, z);
                        IBlockState pstate = world.getBlockState(p);
                        Block b = pstate.getBlock();
                        pi.pos = p;
                        pi.fromX = x;
                        pi.fromY = y;
                        pi.fromZ = z;
                        if (!findAndAdd(b, blocks, pi)) {
                            if (!findAndAdd(b, extraBlocks, pi)) {
                                if (shouldAddBlock != null) {
                                    if (shouldAddBlock.testIsSimilar(b)) blocks.add(b);
                                    else if (shouldAddBlock.testIsExtra(b)) extraBlocks.add(b);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean harvestBlock() {
        PosInfo pi;
        if (blockPositionsToBreak.isEmpty()) {
            return false;
        }
        while ((pi = blockPositionsToBreak.poll()) != null) {
            BlockPos p = pi.pos;
            IBlockState pstate = world.getBlockState(p);
            if (pstate.getBlock() != Blocks.AIR) {
                if (player != null) player.interactionManager.tryHarvestBlock(p);
                else {
                    Block b = pstate.getBlock();
                    world.playEvent(2001, p, Block.getStateId(pstate));
                    List<ItemStack> drops = b.getDrops(world, p, pstate, 0);
                    for (ItemStack stack : drops) {
                        for (Block block : extraBlocks) {
                            if (Helper.isSameBlock(b, block)) {
                                stack.setCount(stack.getCount() * fortune);
                                break;
                            }
                        }
                        world.spawnEntity(new EntityItem(world, p.getX(), p.getY(), p.getZ(), stack));
                    }
                    world.setBlockState(p, Blocks.AIR.getDefaultState(), 3);

                    addBlock(pi);
                }
                break;
            }
        }
        return true;
    }
}
