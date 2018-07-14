package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileFluidSuckingFlower;
import at.witho.totally_op.blocks.tileentity.TileSuckingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class FluidSuckingFlower extends FunctionFlower {

	public FluidSuckingFlower() {
		super("fluid_sucking_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFluidSuckingFlower();
	}

}
