package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileCuttingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CuttingFlower extends FunctionFlower {
    public CuttingFlower() {
        super("cutting_flower");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileCuttingFlower();
    }
}
