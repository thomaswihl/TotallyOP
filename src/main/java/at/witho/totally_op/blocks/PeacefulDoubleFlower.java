package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PeacefulDoubleFlower extends BlockDoublePlant {

	public PeacefulDoubleFlower() {
	    super();
		setUnlocalizedName(TotallyOP.MODID + ".peaceful_double_flower");
		setRegistryName("peaceful_double_flower");
        this.setTickRandomly(false);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(BlockDoublePlant.VARIANT, BlockDoublePlant.FACING).build());
    }

	@Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());
        return soil.getBlock() != Blocks.AIR;
    }

	@Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		return canPlaceBlockAt(worldIn, pos);
	}

}
