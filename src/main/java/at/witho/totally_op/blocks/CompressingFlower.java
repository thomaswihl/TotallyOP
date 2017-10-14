package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileCompressingFlower;
import at.witho.totally_op.blocks.tileentity.TileSuckingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class CompressingFlower extends FunctionFlower {

	public CompressingFlower() {
		super("compressing_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCompressingFlower();
	}

}
