package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.blocks.tileentity.TileFunctionFlower;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class FunctionFlower extends BlockBush implements ITileEntityProvider {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool POWERED = PropertyBool.create("powered");


    FunctionFlower(String name) {
		super(Material.PLANTS, Material.PLANTS.getMaterialMapColor());
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, false));
		setUnlocalizedName(TotallyOP.MODID + "." + name);
		setRegistryName(name);
        setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        setHardness(0F);
	}

	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomStateMapper(this, new StateMap.Builder().ignore(POWERED).build());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        boolean next = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
        boolean current = ((Boolean)state.getValue(POWERED)).booleanValue();

        if (next && !current)
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 4);
        }
        else if (!next && current)
        {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 4);
        }
    }

    public static boolean isPowered(int meta) {
        return (meta & 4) != 0;
    }

	@Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(POWERED, Boolean.valueOf(worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up())));
    }
	
	@Override
	public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING, POWERED});
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(POWERED, Boolean.valueOf((meta & 4) != 0));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        int i = state.getValue(POWERED) ? 4 : 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        return i;
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (hand == EnumHand.MAIN_HAND && tile != null && tile instanceof TileFunctionFlower) {
            if (!worldIn.isRemote) {
                TileFunctionFlower flower = (TileFunctionFlower) tile;
                if (flower.getFilter().isItemEqual(playerIn.getHeldItem(hand))) {
                    flower.setFilterIsWhitelist(!flower.getFilterIsWhitelist());
                } else {
                    flower.setFilter(playerIn.getHeldItem(hand));
                    flower.setFilterIsWhitelist(true);
                }
                String info = "Fortune multiplier is " + flower.getFortune() + ", delay is " + flower.getEfficiency() + " and range is " + flower.getRange() + " filter block is " + (flower.getFilterIsWhitelist() ? "" : "all but ") + flower.getFilter().getItem().getItemStackDisplayName(flower.getFilter());
                playerIn.sendMessage(new TextComponentString(info));
            }
            return true;
        }
        return false;
    }
}
