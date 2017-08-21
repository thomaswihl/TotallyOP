package at.witho.totally_op.blocks.tileentity;

import at.witho.totally_op.blocks.FunctionFlower;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;

public class TileFarmingFlower extends TileEntity implements ITickable {

	protected int fortune = 0;
	protected BlockPos currentPos = null;
	protected int range = 9;
	protected int minX = 0;
	protected int maxX = 0;
	protected int minZ = 0;
	protected int maxZ = 0;

	@Override
	public void update() {
		if (world.isRemote) return;
		if (currentPos == null) {
			initLimits();
			resetPos();
		}
		IBlockState state = world.getBlockState(currentPos);
		Block block = state.getBlock();
		if (block instanceof BlockCrops) {
			BlockCrops crop = (BlockCrops)block;
			int age = ((Integer)state.getValue(BlockCrops.AGE)).intValue();
			if (age == crop.getMaxAge()) {
				NonNullList<ItemStack> drops = NonNullList.create();
				block.getDrops(drops, world, currentPos, state, fortune);
				world.setBlockToAir(currentPos);
				boolean planted = false;
				for (ItemStack stack : drops) {
					world.spawnEntity(new EntityItem(world, currentPos.getX(), currentPos.getY(), currentPos.getZ(), stack));
					if (planted) continue;
					Item item = stack.getItem();
					if (item instanceof IPlantable) {
						IPlantable seed = (IPlantable)item;
						world.setBlockState(currentPos, seed.getPlant(world, currentPos), 3);
						stack.setCount(stack.getCount() - 1);
						planted = true;
					}
				}
			}
		}
		currentPos = currentPos.east();
		if (currentPos.getX() > maxX) {
			currentPos = currentPos.add(-range, 0, 1);
			if (currentPos.getZ() > maxZ) resetPos();
		}
	}

	protected void initLimits() {
		minX = maxX = pos.getX();
		minZ = maxZ = pos.getZ();
		EnumFacing facing = (EnumFacing) world.getBlockState(pos).getValue(FunctionFlower.FACING);
		switch (facing) {
		case EAST: // +x
			minX++;
			maxX += range;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case WEST: // -x
			minX -= range;
			maxX--;
			minZ -= range / 2;
			maxZ += range / 2;
			break;
		case NORTH: // -z
			minX -= range / 2;
			maxX += range / 2;
			minZ -= range;
			maxZ--;
			break;
		case SOUTH: // +z
			minX -= range / 2;
			maxX += range / 2;
			minZ++;
			maxZ += range;
			break;
		default:
			break;
		}
	}

	protected void resetPos() {
		currentPos = new BlockPos(minX, pos.getY(), minZ);
	}
}
