package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.Helper;
import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class TileCuttingFlower extends TileFunctionFlower implements VeinMiner.ShouldAddBlock {
    VeinMiner veinMiner = null;

    public TileCuttingFlower() { super(); }

    public void update() {
        super.update();
        if (veinMiner != null) {
            if (!veinMiner.harvestBlock()) {
                veinMiner = null;
            }
            return;
        }
        if (!shouldRun()) return;
        if (currentPos == null) {
            checkForModifiers();
            resetPos();
            return;
        }

        IBlockState state = world.getBlockState(currentPos);
        Block block = state.getBlock();
        if (state.isFullBlock() && matchesFilter(state)) {
            veinMiner = new VeinMiner(this.getWorld(), null, block);
            veinMiner.setFortune(fortune);
            veinMiner.setShouldAddBlock(this);
            veinMiner.addToBreak(currentPos);
        }
        nextBlock();
    }

    @Override
    public boolean testIsSimilar(Block block) {
        for (ItemStack item : OreDictionary.getOres("logWood")) {
            if (Helper.isSameBlock(block, Block.getBlockFromItem(item.getItem()))) return true;
        }
        return false;
    }
    @Override
    public boolean testIsExtra(Block block) {
        for (ItemStack item : OreDictionary.getOres("treeLeaves")) {
            if (Helper.isSameBlock(block, Block.getBlockFromItem(item.getItem()))) return true;
        }
        return false;
    }
}

