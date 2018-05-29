package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

public class TileCuttingFlower extends TileFunctionFlower {
    VeinMiner veinMiner = null;

    public TileCuttingFlower() { super(); }

    public void update() {
        super.update();
        if (!shouldRun()) return;
        if (currentPos == null) {
            checkForModifiers();
            resetPos();
            return;
        }
        if (veinMiner != null) {
            for (int i = 1 << efficiencyTier; i > 0; --i) {
                if (!veinMiner.harvestBlock()) {
                    veinMiner = null;
                    return;
                }
            }
            return;
        }

        IBlockState state = world.getBlockState(currentPos);
        Block block = state.getBlock();
        if (state.isFullBlock() && matchesFilter(state)) {
            veinMiner = new VeinMiner(this.getWorld(), null, block);
            veinMiner.addBlock(currentPos);
        }
        nextBlock();
    }
}

