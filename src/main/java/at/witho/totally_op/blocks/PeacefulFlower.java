package at.witho.totally_op.blocks;

import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PeacefulFlower extends BlockBush implements IGrowable {
	
	public PeacefulFlower() {
		super(Material.PLANTS, Material.PLANTS.getMaterialMapColor());
		setUnlocalizedName(TotallyOP.MODID + ".peaceful_flower");
		setRegistryName("peaceful_flower");
        this.setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        setHardness(0F);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
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


    @Override
    public boolean canGrow(World world, BlockPos blockPos, IBlockState iBlockState, boolean b) {
        return world.isAirBlock(blockPos.up());
    }

    @Override
    public boolean canUseBonemeal(World world, Random random, BlockPos blockPos, IBlockState iBlockState) {
        return canGrow(world, blockPos, iBlockState, false);
    }

    @Override
    public void grow(World world, Random random, BlockPos blockPos, IBlockState iBlockState) {
        world.setBlockState(blockPos, ModBlocks.peaceful_double_flower.getDefaultState().withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER));
        world.setBlockState(blockPos.up(), ModBlocks.peaceful_double_flower.getDefaultState().withProperty(BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER));
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FULL_BLOCK_AABB;
    }

}
