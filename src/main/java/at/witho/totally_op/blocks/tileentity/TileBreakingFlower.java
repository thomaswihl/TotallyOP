package at.witho.totally_op.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileBreakingFlower extends TileFunctionFlower {
    public TileBreakingFlower() {
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
        if (state.getBlock() != Blocks.AIR) {
            boolean doBreak = true;
            if (!filter.isEmpty()) {
                Block filterBlock = Block.getBlockFromItem(filter.getItem());
                IBlockState filterState = filterBlock.getStateFromMeta(filter.getMetadata());
                doBreak = !(state.equals(filterState) ^ filterIsWhitelist);
            }
            if (doBreak) {
                world.destroyBlock(currentPos, true);
            }
        }

		nextBlock();
	}



}
