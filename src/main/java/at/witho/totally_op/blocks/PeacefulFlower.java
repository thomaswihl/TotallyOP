package at.witho.totally_op.blocks;

import at.witho.totally_op.ModBlocks;
import at.witho.totally_op.ModItems;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class PeacefulFlower extends BlockBush implements IGrowable {
    public static final String NAME = "peaceful_flower";

	public PeacefulFlower() {
		super(Material.PLANTS, Material.PLANTS.getMaterialMapColor());
		setUnlocalizedName(TotallyOP.MODID + "." + NAME);
		setRegistryName(NAME);
        //this.setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        setHardness(0F);
	}
	
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    private boolean isOre(Block block, String name) {
        NonNullList<ItemStack> blocks = OreDictionary.getOres(name);
        for (ItemStack input : blocks)
        {
            if (Block.getBlockFromItem(input.getItem()) == block) return true;
        }
        return false;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.randomTick(worldIn, pos, state, rand);
        IBlockState below = worldIn.getBlockState(pos.down());
        Block block = below.getBlock();
        if (block == Blocks.COBBLESTONE) return;
        ItemStack smeltingResult = FurnaceRecipes.instance().getSmeltingResult(new ItemStack(block, 1, block.getMetaFromState(below)));
        ItemStack result = null;
        if (!smeltingResult.isEmpty()) {
            result = smeltingResult.copy();
            result.setCount(result.getCount() * 9);
        } else {
            if (isOre(block, "blockCoal") || isOre(block, "blockCharcoal")) {
                result = new ItemStack(ModItems.diamond_fragment);
            }
            else if (isOre(block, "blockLead")) {
                result = new ItemStack(Items.GOLD_INGOT);
            }
        }
        if (result != null && !result.isEmpty()) {
            worldIn.setBlockState(pos.down(), Blocks.COBBLESTONE.getDefaultState());
            worldIn.setBlockToAir(pos);
            worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), result));
        }
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
