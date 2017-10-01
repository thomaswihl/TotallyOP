package at.witho.totally_op.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TilePlacingFlower extends TileFunctionFlower {


    protected BlockPos currentPos = null;

    protected int delay = 1;

    public TilePlacingFlower() {
        super();
    }

    @Override
	public void update() {
		if (!shouldRun()) return;
        if (currentPos == null) {
            checkForModifiers();
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
        int r = 1;
        for (BlockPos pos : BlockPos.getAllInBox(pos.add(-r, -r, -r), pos.add(r, r, r))) {
            TileEntity e = world.getTileEntity(pos);
            if (e != null) {
                IItemHandler inventory = e.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null) {
                    ItemStack place = extractItem(inventory, block);
                    if (!place.isEmpty()) return place;
                }
            }
        }
        return null;
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
        return null;
    }
}
