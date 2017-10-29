package at.witho.totally_op.items;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.net.PacketHandler;
import at.witho.totally_op.net.RoughToolChange;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
import net.minecraftforge.client.event.MouseEvent;
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
    private static final String DIMENSION = "Dimension";
    private static int MAX_DIMENSION = 15;
    private static int MIN_DIMENSION = 3;
    private static final ImmutableList<Block> DELETE_BLOCKS = ImmutableList.of(
            Blocks.COBBLESTONE, Blocks.STONE, Blocks.DIRT, Blocks.GRASS, Blocks.GRAVEL, Blocks.SANDSTONE, Blocks.SAND, Blocks.CONCRETE,
            Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.END_STONE);

    private boolean active = false;


    public RoughTool() {
		super(TotallyOP.peacefulDiamondMaterial, new HashSet<>());
		setRegistryName("rough_tool");
		setUnlocalizedName(TotallyOP.MODID + "." + "rough_tool");
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
    void mouseEvent(MouseEvent event) {
        if (event.getDwheel() != 0) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.isSneaking()) {
                ItemStack item = player.getHeldItemMainhand();
                if (item.getItem() instanceof RoughTool) {
                    TotallyOP.logger.log(Level.ERROR, "Sending packet");
                    PacketHandler.INSTANCE.sendToServer(new RoughToolChange((event.getDwheel() > 0) ? 1 : -1));
                }
                event.setCanceled(true);
            }
        }
    }

    public void changeDimension(ItemStack stack, EntityPlayer player, int amount) {
        int dimension = getDimension(stack) + amount;
        if (dimension < MIN_DIMENSION) dimension = MAX_DIMENSION;
        else if (dimension > MAX_DIMENSION) dimension = MIN_DIMENSION;
        setDimension(stack, dimension);
        int width = dimension >> 1;
        boolean round = (dimension & 1) == 0;
        player.sendMessage(new TextComponentString("Dimension is now " + (width * 2 + 1) + "x" + (width * 2 + 1) + (round ? " round" : "")));
    }

    public int getDimension(ItemStack stack) {
        if (stack.hasTagCompound()) {
            NBTTagCompound compound = stack.getTagCompound();
            return compound.getInteger(DIMENSION);
        }
        return MIN_DIMENSION;
    }

    public void setDimension(ItemStack stack, int dimension) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound compound = stack.getTagCompound();
        compound.setInteger(DIMENSION, dimension);
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
            int width = getDimension(itemstack);
            boolean round = (width & 1) == 0;
            width >>= 1;
            active = true;

            EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);

            int angle = Math.round(player.rotationPitch / 22.5f);

            boolean vertical = angle > 2 || angle < -2;
            int xstart = 0, ystart = 0, zstart = 0;
            int xstop = 0, ystop = 0, zstop = 0;
            BlockPos startPos = null;
            if (!vertical) {
                BlockPos preceding = pos.offset(facing, -1);
                boolean found = false;
                for (int i = 0; i < 2 * width + 2; ++i) {
                    if (world.getBlockState(preceding).isOpaqueCube()) {
                        found = true;
                        break;
                    }
                    preceding = preceding.down();
                }
                if (found) {
                    startPos = preceding.offset(facing);
                    // So preceding is already on a solid block, so 1 too low, consider this when moving up
                    switch (angle) {
                        case -2:
                            startPos = startPos.up(width + 2);
                            break;
                        case -1: {
                            BlockPos posStep = preceding.offset(facing, -1);
                            boolean stepBefore = world.getBlockState(posStep).isOpaqueCube();
                            startPos = startPos.up(width + (stepBefore ? 2 : 1));
                        }   break;
                        case 0:
                            startPos = startPos.up(width + 1);
                            break;
                        case 1: {
                            BlockPos posStep = preceding.offset(facing, -1).up();
                            boolean stepBefore = world.getBlockState(posStep).isOpaqueCube();
                            startPos = startPos.up(width + (stepBefore ? 1 : 0));
                        }   break;
                        case 2:
                            startPos = startPos.up(width);
                            break;
                    }

                }

            }
            if (startPos == null) startPos = new BlockPos(pos);
            if (!vertical) {
                ystart = -width;
                ystop = width;
                if (facing.getAxis() == EnumFacing.Axis.X) {
                    zstart = -width;
                    zstop = width;
                } else {
                    xstart = -width;
                    xstop = width;
                }
            } else {
                xstart = -width;
                xstop = width;
                zstart = -width;
                zstop = width;
            }
            int radius = (int)((width + 0.5f)*(width + 0.5f) + 0.5f);
            for (int x = xstart; x <= xstop; x++) {
                for (int y = ystart; y <= ystop; y++) {
                    for (int z = zstart; z <= zstop; z++) {
                        if (round && (x*x + y*y + z*z > radius)) continue;
                        BlockPos p = startPos.add(x, y, z);
                        IBlockState thisState = player.world.getBlockState(p);
                        Block thisBlock = thisState.getBlock();
                        if (sound == null) sound = thisBlock.getSoundType(thisState, world, p, player);
                        if (thisBlock == Blocks.AIR) continue;
                        if (DELETE_BLOCKS.contains(thisBlock)) player.world.setBlockToAir(p);
                        else if (canHarvestBlock(thisState, itemstack)) {
                            ((EntityPlayerMP) player).interactionManager.tryHarvestBlock(p);
                        }
                    }
                }
            }
            active = false;
            magnetActive = MAGNET_ACTIVE_TIME;
        }
        if (sound != null) world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound.getBreakSound(), SoundCategory.BLOCKS, sound.volume, sound.pitch);
        return false;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (!worldIn.isRemote && magnetActive > 0) {
            --magnetActive;
            double x = entityIn.posX;
            double y = entityIn.posY;
            double z = entityIn.posZ;
            int range = getDimension(stack) + 3;
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

