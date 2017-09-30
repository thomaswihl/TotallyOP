package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import at.witho.totally_op.blocks.tileentity.TilePlacingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PlacingFlower extends FunctionFlower {

	public PlacingFlower() {
		super("placing_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePlacingFlower();
	}

}
