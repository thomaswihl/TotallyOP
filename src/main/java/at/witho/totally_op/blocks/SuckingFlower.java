package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.blocks.tileentity.TileSuckingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class SuckingFlower extends FunctionFlower {
	public static final String NAME = "sucking_flower";

	public SuckingFlower() {
		super(NAME);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSuckingFlower();
	}

}
