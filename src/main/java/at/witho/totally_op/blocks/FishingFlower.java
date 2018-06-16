package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileFishingFlower;
import at.witho.totally_op.blocks.tileentity.TilePlacingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class FishingFlower extends FunctionFlower {

	public FishingFlower() {
		super("fishing_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFishingFlower();
	}

}
