package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.util.VeinMiner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

public class TileCuttingFlower extends TileFunctionFlower implements VeinMiner.ShouldAddBlock {
    VeinMiner veinMiner = null;
    boolean needToSetWorld = false;

    public TileCuttingFlower() { super(); }

    public void update() {
        super.update();
        if (veinMiner != null) {
            if (needToSetWorld) {
                needToSetWorld = false;
                veinMiner.setWorld(world);
            }
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

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        if (nbt.hasKey("veinMiner")) {
            NBTTagCompound comp = nbt.getCompoundTag("veinMiner");
            veinMiner = new VeinMiner(world, null, comp);
            veinMiner.setShouldAddBlock(this);
            needToSetWorld = true;
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

