package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.blocks.tileentity.TileSuckingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SuckingFlower extends FunctionFlower {

	public SuckingFlower() {
		super("sucking_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSuckingFlower();
	}

}
