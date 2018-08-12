package at.witho.totally_op.blocks;

import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.config.Config;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

import java.util.Random;

public class TierableBlock extends Block {
    public enum Type { Fortune, Efficency, Range };
    public static final String FORTUNE = "fortune";
    public static final String EFFICIENCY = "efficiency";
    public static final String RANGE = "range";
    public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 6);
    private ItemStack upgradeTierBlock = null;

    public TierableBlock(Type type) {
        super(Material.ROCK, Material.ROCK.getMaterialMapColor());
        String name = getName(type);
        upgradeTierBlock = getUpgradeTierBlock(type);
        this.setDefaultState(blockState.getBaseState().withProperty(TIER, 0));
        setUnlocalizedName(TotallyOP.MODID + "." + name);
        setRegistryName(name);
        setTickRandomly(false);
        this.setSoundType(SoundType.STONE);
        setHardness(2.0f);
        setResistance(30);
    }

    public static String getName(Type type) {
        String name = "Unknown";
        switch (type) {
            case Fortune:
                name = FORTUNE;
                break;
            case Efficency:
                name = EFFICIENCY;
                break;
            case Range:
                name = RANGE;
                break;
        }
        return name;
    }

    public static ItemStack getUpgradeTierBlock(Type type) {
        switch (type) {
            case Fortune:
                return new ItemStack(Config.fortunateUpgradeTierBlock);
            case Efficency:
                return new ItemStack(Config.efficiencyUpgradeTierBlock);
            case Range:
                return new ItemStack(Config.rangeUpgradeTierBlock);
        }
        return ItemStack.EMPTY;
    }

    public int getTier(IBlockState state) {
        return state.getValue(TIER).intValue();
    }

    public void setTier(World world, BlockPos pos, int tier) { world.setBlockState(pos, getDefaultState().withProperty(TIER, tier)); }

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
        ItemStack stack = playerIn.getHeldItem(hand);
        if ((stack == null || stack.isEmpty()) && hand == EnumHand.MAIN_HAND) {
            int tier = getTier(state);
            if (tier > 0) {
                stack = upgradeTierBlock.copy();
                stack.setCount(tier);
                playerIn.setHeldItem(hand, stack);
                setTier(worldIn, pos, 0);
            }
            return true;
        }
        if (stack.isItemEqual(upgradeTierBlock)) {
            int tier = getTier(state);
            if (tier < 6) {
                if (!worldIn.isRemote) {
                    tier++;
                    setTier(worldIn, pos, tier);
                    stack.setCount(stack.getCount() - 1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        Item item = Item.getItemFromBlock(this);
        if (item != Items.AIR) {
            ItemStack stack = new ItemStack(item, 1, this.damageDropped(state));
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("Tier", getTier(state));
            stack.setTagCompound(nbt);
            drops.add(stack);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt != null) {
            int tier = nbt.getInteger("Tier");
            setTier(worldIn, pos, tier);
        }
    }

}
