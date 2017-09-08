package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.blocks.tileentity.TileFarmingFlower;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class FarmingFlower extends FunctionFlower {

	public FarmingFlower() {
		super("farming_flower");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFarmingFlower();
	}

}
