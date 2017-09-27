package at.witho.totally_op.items;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoughTool extends ItemTool {
    private static final int MAGNET_ACTIVE_TIME = 20;
    private int magnetActive = 0;

    private boolean active = false;
    private int width = 2;


    public RoughTool() {
		super(TotallyOP.peacefulDiamondMaterial, new HashSet<>());
		setRegistryName("rough_tool");
		setUnlocalizedName(TotallyOP.MODID + "." + "rough_tool");
	}

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState blockState)
	{
		return canHarvestBlock(blockState, stack) ? efficiencyOnProperMaterial : 1.0F;
	}

	@Override
	public boolean canHarvestBlock(IBlockState state, ItemStack stack)
	{
        return state.getBlock() != Blocks.BEDROCK;
	}

	@Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player)
    {
        if (active) return false;
        SoundType sound = null;
        World world = player.world;
        if (!player.world.isRemote) {
            active = true;
            int vertical = Math.round(player.rotationPitch / 90.0f);
            int horizontal = Math.round(player.rotationYaw * 4.0F / 360.0F) & 1;
            int xstart = 0, ystart = 0, zstart = 0;
            int xstop = 0, ystop = 0, zstop = 0;
            BlockPos startPos = new BlockPos(pos);
            if (vertical == 0) {
                startPos = startPos.up(width - 1);
                ystart = -width;
                ystop = width;
                if (horizontal == 0) {
                    xstart = -width;
                    xstop = width;
                } else {
                    zstart = -width;
                    zstop = width;
                }
            } else {
                xstart = -width;
                xstop = width;
                zstart = -width;
                zstop = width;
            }

            for (int x = xstart; x <= xstop; x++) {
                for (int y = ystart; y <= ystop; y++) {
                    for (int z = zstart; z <= zstop; z++) {
                        BlockPos p = startPos.add(x, y, z);
                        IBlockState thisState = player.world.getBlockState(p);
                        Block thisBlock = thisState.getBlock();
                        if (sound == null) sound = thisBlock.getSoundType(thisState, world, p, player);
                        if (thisBlock == Blocks.AIR) continue;
                        if (thisBlock == Blocks.STONE || thisBlock == Blocks.DIRT || thisBlock == Blocks.GRAVEL) player.world.setBlockToAir(p);
                        else if (canHarvestBlock(thisState, itemstack)) {
                            ((EntityPlayerMP) player).interactionManager.tryHarvestBlock(p);
                        }
                    }
                }
            }
            active = false;
            magnetActive = MAGNET_ACTIVE_TIME;
        }
        if (sound != null) world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), sound.getBreakSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
        return true;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!worldIn.isRemote && magnetActive > 0) {
            --magnetActive;
            double x = entityIn.posX;
            double y = entityIn.posY;
            double z = entityIn.posZ;
            int range = width + 3;
            List<EntityItem> items = worldIn.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(x - range, y - range, z - range, x + range + 1, y + range + 1, z + range + 1));
            for(EntityItem item : items) {
                item.setPosition(x,  y,  z);
            }
            List<EntityXPOrb> xpOrbs = worldIn.getEntitiesWithinAABB(EntityXPOrb.class, new AxisAlignedBB(x - range, y - range, z - range, x + range + 1, y + range + 1, z + range + 1));
            for(EntityXPOrb item : xpOrbs) {
                item.setPosition(x,  y,  z);
            }
        }
    }

}

