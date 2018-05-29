package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.BreakingFlower;
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
        if (state.getBlock() != Blocks.AIR && matchesFilter(state)) {
            world.destroyBlock(currentPos, true);
        }

		nextBlock();
	}
}
