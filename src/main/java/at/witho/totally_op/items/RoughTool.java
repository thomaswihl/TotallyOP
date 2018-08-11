package at.witho.totally_op.items;

import at.witho.totally_op.Helper;
import at.witho.totally_op.TotallyOP;
import at.witho.totally_op.net.PacketHandler;
import at.witho.totally_op.net.RoughToolChange;
import at.witho.totally_op.util.HightlightBlock;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector3d;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
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
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;
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

    private class ParameterSet {
        public int xstart = 0, xstop = 0, ystart = 0, ystop = 0, zstart = 0, zstop = 0;
        public int radius = 0;
        public boolean round = false;
        public BlockPos startPos = null;
        public EnumFacing playerFacing = null;
    }

    private boolean active = false;

    private static int count = 0;


    public RoughTool() {
		super(TotallyOP.peacefulDiamondMaterial, new HashSet<>());
		setRegistryName("rough_tool");
		setUnlocalizedName(TotallyOP.MODID + "." + "rough_tool");
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
    @SideOnly(Side.CLIENT)
    void mouseEvent(MouseEvent event) {
        if (event.getDwheel() != 0) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player.isSneaking()) {
                ItemStack item = player.getHeldItemMainhand();
                if (item.getItem() instanceof RoughTool) {
                    PacketHandler.INSTANCE.sendToServer(new RoughToolChange((event.getDwheel() > 0) ? 1 : -1));
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void renderOutline(RenderWorldLastEvent ev) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (!(itemstack.getItem() instanceof RoughTool)) {
            itemstack = player.getHeldItem(EnumHand.OFF_HAND);
            if (!(itemstack.getItem() instanceof RoughTool)) return;
        }
        BlockPos pos = HightlightBlock.getBlockPos(player);
        if (pos == null) return;

        HightlightBlock.begin();
        ParameterSet p = getParameters(itemstack, pos, player);
        HightlightBlock.color = new Color(255, 255, 255, 32);
        if (p.xstart == p.xstop) {
            if (p.playerFacing == EnumFacing.EAST) HightlightBlock.mX = true;
            else HightlightBlock.pX = true;
        }
        if (p.ystart == p.ystop) {
            if (p.playerFacing == EnumFacing.DOWN)  HightlightBlock.mY = true;
            else HightlightBlock.pY = true;
        }
        if (p.zstart == p.zstop) {
            if (p.playerFacing == EnumFacing.SOUTH) HightlightBlock.mZ = true;
            else HightlightBlock.pZ = true;
        }

        for (int x = p.xstart; x <= p.xstop; x++) {
            for (int y = p.ystart; y <= p.ystop; y++) {
                for (int z = p.zstart; z <= p.zstop; z++) {
                    if (p.round && (x*x + y*y + z*z > p.radius)) continue;
                    BlockPos bp = p.startPos.add(x, y, z);
                    HightlightBlock.outlineBlock(bp, ev.getPartialTicks());
                }
            }
        }
        HightlightBlock.end();
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
	public float getDestroySpeed(ItemStack stack, IBlockState blockState)
	{
		return canHarvestBlock(blockState, stack) ? efficiency : 1.0F;
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
            ParameterSet p = getParameters(itemstack, pos, player);
            for (int x = p.xstart; x <= p.xstop; x++) {
                for (int y = p.ystart; y <= p.ystop; y++) {
                    for (int z = p.zstart; z <= p.zstop; z++) {
                        if (p.round && (x*x + y*y + z*z > p.radius)) continue;
                        BlockPos bp = p.startPos.add(x, y, z);
                        IBlockState thisState = player.world.getBlockState(bp);
                        Block thisBlock = thisState.getBlock();
                        if (sound == null) sound = thisBlock.getSoundType(thisState, world, bp, player);
                        if (thisBlock == Blocks.AIR) continue;
                        if (DELETE_BLOCKS.contains(thisBlock)) player.world.setBlockToAir(bp);
                        else if (canHarvestBlock(thisState, itemstack)) {
                            ((EntityPlayerMP) player).interactionManager.tryHarvestBlock(bp);
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

    private ParameterSet getParameters(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        int width = getDimension(itemstack);
        ParameterSet p = new ParameterSet();
        p.round = (width & 1) == 0;
        width >>= 1;

        EnumFacing facing = EnumFacing.getHorizontal(MathHelper.floor((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        p.playerFacing = facing;

        int angle = Math.round(player.rotationPitch / 22.5f);

        boolean vertical = angle > 2 || angle < -2;
        if (!vertical) {
            BlockPos preceding = pos.offset(facing, -1);
            boolean found = false;
            for (int i = 0; i < 2 * width + 2; ++i) {
                if (player.world.getBlockState(preceding).isOpaqueCube()) {
                    found = true;
                    break;
                }
                preceding = preceding.down();
            }
            if (found) {
                p.startPos = preceding.offset(facing);
                // So preceding is already on a solid block, so 1 too low, consider this when moving up
                switch (angle) {
                    case -2:
                        p.startPos = p.startPos.up(width + 2);
                        break;
                    case -1: {
                        BlockPos posStep = preceding.offset(facing, -1);
                        boolean stepBefore = player.world.getBlockState(posStep).isOpaqueCube();
                        p.startPos = p.startPos.up(width + (stepBefore ? 2 : 1));
                    }   break;
                    case 0:
                        p.startPos = p.startPos.up(width + 1);
                        break;
                    case 1: {
                        BlockPos posStep = preceding.offset(facing, -1).up();
                        boolean stepBefore = player.world.getBlockState(posStep).isOpaqueCube();
                        p.startPos = p.startPos.up(width + (stepBefore ? 1 : 0));
                    }   break;
                    case 2:
                        p.startPos = p.startPos.up(width);
                        break;
                }

            }

        } else {
            if (angle < -2) p.playerFacing = EnumFacing.DOWN;
            else p.playerFacing = EnumFacing.UP;
        }
        if (p.startPos == null) p.startPos = new BlockPos(pos);
        if (!vertical) {
            p.ystart = -width;
            p.ystop = width;
            if (facing.getAxis() == EnumFacing.Axis.X) {
                p.zstart = -width;
                p.zstop = width;
            } else {
                p.xstart = -width;
                p.xstop = width;
            }
        } else {
            p.xstart = -width;
            p.xstop = width;
            p.zstart = -width;
            p.zstop = width;
        }
        p.radius = (int)((width + 0.5f)*(width + 0.5f) + 0.5f);
        return p;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        ++count;
        if (count > 50) count = 0;
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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (playerIn.isSneaking()) {
            if (!worldIn.isRemote) {
                printIEChunk(playerIn, worldIn, (int)playerIn.posX, (int)playerIn.posZ);
            }
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
    }

    public void printIEChunk(EntityPlayer player, World world, int x, int z) {
        try {
            Class<?> ExcavatorHandler = Class.forName("blusunrize.immersiveengineering.api.tool.ExcavatorHandler");
            TotallyOP.logger.log(Level.ERROR, "Found class");
            java.lang.reflect.Method getMineralWorldInfo = ExcavatorHandler.getMethod("getMineralWorldInfo", World.class, int.class, int.class);
            TotallyOP.logger.log(Level.ERROR, "Found method");
            Object o = getMineralWorldInfo.invoke(null, world, x >> 4, z >> 4);
            TotallyOP.logger.log(Level.ERROR, o.toString());
            Field fieldMineral = o.getClass().getDeclaredField("mineral");
            Object mineral = fieldMineral.get(o);
            if (mineral == null) return;
            TotallyOP.logger.log(Level.ERROR, mineral.toString());
            Field fieldOres = mineral.getClass().getDeclaredField("ores");
            String[] ores = (String[])fieldOres.get(mineral);
            Field fieldChances = mineral.getClass().getDeclaredField("chances");
            float[] chances = (float[])fieldChances.get(mineral);
            Field fieldName = mineral.getClass().getDeclaredField("name");
            String name = (String)fieldName.get(mineral);
            if (ores != null && chances != null && name != null) {
                String summary = name + " [";
                for (int i = 0; i < ores.length && i < chances.length; ++i) {
                    if (i != 0) summary += ", ";
                    summary += ((int)(chances[i] * 100)) + "% " + ores[i];
                }
                summary += "]";
                player.sendMessage(new TextComponentString(summary));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

