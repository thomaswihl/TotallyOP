package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TierableBlock extends Block {
    public static final String FORTUNE = "fortune";
    public static final String EFFICIENCY = "efficiency";
    public static final String RANGE = "range";
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 6);

    public TierableBlock(String name) {
        super(Material.ROCK, Material.ROCK.getMaterialMapColor());
        this.setDefaultState(blockState.getBaseState().withProperty(TIER, 0));
        setUnlocalizedName(TotallyOP.MODID + "." + name);
        setRegistryName(name);
        setTickRandomly(false);
        this.setSoundType(SoundType.STONE);
        setHardness(2.0f);
        setResistance(30);
    }

    public int getTier(IBlockState state) {
        return state.getValue(TIER).intValue();
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TIER, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return getTier(state);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TIER);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        //if (worldIn.isRemote) return false;
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack == null || stack.isEmpty()) return false;
        if (stack.isItemEqual(new ItemStack(Blocks.GOLD_BLOCK))) {
            int tier = getTier(state);
            if (tier < 6) {
                tier++;
                if (!worldIn.isRemote) {
                    worldIn.setBlockState(pos, getDefaultState().withProperty(TIER, tier));
                    stack.setCount(stack.getCount() - 1);
                }
                return true;
            }
        }
        return false;
    }

}
