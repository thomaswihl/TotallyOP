package at.witho.totally_op.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TilePlacingFlower extends TileFunctionFlower {
    public TilePlacingFlower() {
        super();
    }

    @Override
	public void update() {
        super.update();
		if (!shouldRun()) return;
        if (currentPos == null) {
            resetPos();
            return;
        }

		IBlockState state = world.getBlockState(currentPos);
        if (state.getBlock() == Blocks.AIR) {
            IBlockState stateBelow = world.getBlockState(currentPos.down());
            Block block = stateBelow.getBlock();
            ItemStack place = findItem(block);
            if (!place.isEmpty()) {
                Block b = Block.getBlockFromItem(place.getItem());
                IBlockState s = b.getStateFromMeta(place.getMetadata());
                world.setBlockState(currentPos, s);
            }
        }
		nextBlock();
	}

    private ItemStack findItem(Block block) {
        List<IItemHandler> inventories = backInventories();
        for (IItemHandler inventory : inventories) {
            ItemStack place = extractItem(inventory, block);
            if (!place.isEmpty()) return place;
        }
        return ItemStack.EMPTY;
    }

    private ItemStack extractItem(IItemHandler inventory, Block block) {
        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack stack = inventory.extractItem(i, 1, true);
            if (!stack.isEmpty()) {
                if (!filter.isEmpty()) {
                    if (stack.isItemEqual(filter) ^ filterIsWhitelist) continue;
                }
                Block place = Block.getBlockFromItem(stack.getItem());
                if (place.canPlaceBlockAt(world, currentPos)) {
                    return inventory.extractItem(i, 1, false);
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
