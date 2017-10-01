package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.blocks.tileentity.TileFunctionFlower;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
	
	FunctionFlower(String name) {
		super(Material.PLANTS, Material.PLANTS.getMaterialMapColor());
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
		setUnlocalizedName(TotallyOP.MODID + "." + name);
		setRegistryName(name);
        setTickRandomly(false);
        this.setSoundType(SoundType.PLANT);
        setHardness(0F);
	}

	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

	@Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }
	
	@Override
	public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }

	@Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
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
