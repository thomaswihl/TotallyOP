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
import net.minecraft.nbt.NBTTagCompound;
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
    boolean needToSetWorld = false;

    public TileFluidSuckingFlower() {
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
        if (veinMiner != null) {
            if (needToSetWorld) {
                needToSetWorld = false;
                veinMiner.setWorld(world);
            }
            if (fluidStack.amount < Fluid.BUCKET_VOLUME * 10) {
                if (!veinMiner.harvestBlock()) {
                    veinMiner = null;
                } else {
                    fluidStack.amount += Fluid.BUCKET_VOLUME;
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

        IBlockState state = world.getBlockState(currentPos);
        Block block = state.getBlock();
        if (state.getMaterial().isLiquid() && matchesFilter(state)) {
            Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null && block.getMetaFromState(state) == 0) {
                fluidStack = new FluidStack(fluid, Fluid.BUCKET_VOLUME);
                veinMiner = new VeinMiner(this.getWorld(), null, block, 0);
                veinMiner.addToBreak(currentPos);
            }
        }
        nextBlock();
	}
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("veinMiner")) {
            NBTTagCompound comp = nbt.getCompoundTag("veinMiner");
            veinMiner = new VeinMiner(world, null, comp);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        if (veinMiner != null) {
            NBTTagCompound comp = new NBTTagCompound();
            veinMiner.writeToNBT(comp);
            nbt.setTag("veinMiner", comp);
        }
        return nbt;
    }

}
