package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileCompressingFlower;
import at.witho.totally_op.blocks.tileentity.TileSplittingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SplittingFlower extends FunctionFlower {

	public SplittingFlower() {
		super("splitting_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSplittingFlower();
	}

}
