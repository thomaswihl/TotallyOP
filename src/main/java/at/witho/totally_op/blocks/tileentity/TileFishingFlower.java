package at.witho.totally_op.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class TileFishingFlower extends TileFunctionFlower {
    public TileFishingFlower() {
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
            if (stateBelow.getBlock() == Blocks.WATER) {
                LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer)world);
                lootcontext$builder.withLuck(fortune);
                List<ItemStack> items = this.world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(world.rand, lootcontext$builder.build());
                if (!addToInventories(items)) {
                    for (ItemStack item : items) {
                        BlockPos p = pos.offset(facing, -1);
                        EntityItem entityItem = new EntityItem(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, item);
                        world.spawnEntity(entityItem);
                    }
                }
            }
        }
		nextBlock();
	}

    private boolean addToInventories(List<ItemStack> items) {
        List<IItemHandler> inventories = backInventories();
        for (IItemHandler inventory : inventories) {
            if (addItemToInventory(inventory, items)) return true;
        }
        return false;
    }

}
