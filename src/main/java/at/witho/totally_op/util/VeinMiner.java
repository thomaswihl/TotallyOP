package at.witho.totally_op.util;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

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
        public PosInfo(PosInfo other) {
            this.pos = other.pos;
            this.fromX = other.fromX;
            this.fromY = other.fromY;
            this.fromZ = other.fromZ;
        }
        public String toString() {
            return "PosInfo{" + pos.toString() + ", from{" + fromX + ", " + fromY + ", " + fromZ + "}}";
        }
    }
    private ArrayDeque<PosInfo> blockPositionsToBreak = new ArrayDeque<PosInfo>();
    private List<Block> blocks = new ArrayList<>();
    private List<Block> extraBlocks = new ArrayList<>();
    private EntityPlayerMP player = null;
    private World world = null;
    private boolean horizontalPlane = false;
    private int fortune = 0;
    private int checks = 0;
    private int metadata = -1;

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

    public VeinMiner(World world, EntityPlayerMP player, Block block, int metadata) {
        this.world = world;
        this.player = player;
        this.blocks.add(block);
        this.metadata = metadata;
    }

    public void setHorizontalPlane(boolean plane) { horizontalPlane = plane; }
    public void setShouldAddBlock(ShouldAddBlock sbb) { shouldAddBlock = sbb; }
    public void setFortune(int f) { fortune = f; }

    private boolean findAndAdd(Block b, int meta, List<Block> list, PosInfo pi) {
        for (Block block : list) {
            if (Helper.isSameBlock(b, block)) {
                if (metadata == -1 || metadata == meta) {
                    // Need to clone PosInfo when needed as it is modified outside
                    blockPositionsToBreak.add(new PosInfo(pi));
                    return true;
                }
            }
        }
        return false;
    }

    public void addToBreak(BlockPos pos) {
        blockPositionsToBreak.add(new PosInfo(pos));
    }

    public void addSurroundings(BlockPos pos) {
        addSurroundings(new PosInfo(pos));
    }

    protected void addSurroundings(PosInfo piFrom) {
        byte x1 = -1;
        byte x2 = 1;
        byte y1 = -1;
        byte y2 = 1;
        byte z1 = -1;
        byte z2 = 1;
        if (horizontalPlane) {
            y1 = 0;
            y2 = 0;
        }
        byte xp = piFrom.fromX;
        byte yp = piFrom.fromY;
        byte zp = piFrom.fromZ;
        if (xp == 0) xp = 2;
        if (yp == 0) yp = 2;
        if (zp == 0) zp = 2;
        boolean all = false;
        if (xp == 2 && yp == 2 && zp == 2) all = true;
        BlockPos pos = piFrom.pos;
        PosInfo pi = new PosInfo(pos);
        for (byte y = y1; y <= y2; ++y) {
            for (byte x = x1; x <= x2; ++x) {
                for (byte z = z1; z <= z2; ++z) {
                    if (x != 0 || y != 0 || z != 0) {
                        if (all || x == xp || y == yp || z == zp) {
                            BlockPos p = pos.add(x, y, z);
                            IBlockState pstate = world.getBlockState(p);
                            Block b = pstate.getBlock();
                            int meta = b.getMetaFromState(pstate);
                            pi.pos = p;
                            pi.fromX = x;
                            pi.fromY = y;
                            pi.fromZ = z;
                            ++checks;
                            if (!findAndAdd(b, meta, blocks, pi)) {
                                if (!findAndAdd(b, meta, extraBlocks, pi)) {
                                    if (shouldAddBlock != null) {
                                        if (shouldAddBlock.testIsSimilar(b)) {
                                            blocks.add(b);
                                            findAndAdd(b, meta, blocks, pi);
                                        }
                                        else if (shouldAddBlock.testIsExtra(b)) {
                                            extraBlocks.add(b);
                                            findAndAdd(b, meta, extraBlocks, pi);
                                        }
                                    }
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
                if (player != null) {
                    if (pstate.getMaterial().isLiquid()) continue;
                    player.interactionManager.tryHarvestBlock(p);
                }
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

                    addSurroundings(pi);
                }
                break;
            }
        }
        return true;
    }
}
