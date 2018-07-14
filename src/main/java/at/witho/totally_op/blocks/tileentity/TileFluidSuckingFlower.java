package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.config.Config;
import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;


public class TileFluidSuckingFlower extends TileFunctionFlower {
    VeinMiner veinMiner = null;
    FluidStack fluidStack = null;

    public TileFluidSuckingFlower() {
        super();
    }

    @Override
	public void update() {
        if (veinMiner != null) {
            if (fluidStack.amount < 10000) {
                if (!veinMiner.harvestBlock()) {
                    veinMiner = null;
                } else {
                    fluidStack.amount += 1000;
                    List<IFluidHandler> inventories = backFluidInventories();
                    for (IFluidHandler inventory : inventories) {
                        int amount = inventory.fill(fluidStack, true);
                        fluidStack.amount -= amount;
                        if (amount == 0) break;
                    }
                }
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
        if (state.getMaterial().isLiquid() && matchesFilter(state)) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null) {
                fluidStack = new FluidStack(fluid, 1000);
                veinMiner = new VeinMiner(this.getWorld(), null, block);
                veinMiner.addToBreak(currentPos);
            }
        }
        nextBlock();
	}

}
