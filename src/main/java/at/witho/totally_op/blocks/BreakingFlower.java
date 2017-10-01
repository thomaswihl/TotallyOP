package at.witho.totally_op.blocks;

import at.witho.totally_op.blocks.tileentity.TileBreakingFlower;
import at.witho.totally_op.blocks.tileentity.TilePlacingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BreakingFlower extends FunctionFlower {

	public BreakingFlower() {
		super("breaking_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBreakingFlower();
	}

}
