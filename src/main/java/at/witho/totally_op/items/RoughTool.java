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
    public void renderOutline(RenderWorldLastEvent ev) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack itemstack = player.getHeldItem(EnumHand.MAIN_HAND);
        if (!(itemstack.getItem() instanceof RoughTool)) {
            itemstack = player.getHeldItem(EnumHand.OFF_HAND);
            if (!(itemstack.getItem() instanceof RoughTool)) return;
        }
        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        if (result == null) return;
        BlockPos pos = result.getBlockPos();
        if (pos == null) return;
        IBlockState state = player.world.getBlockState(pos);
        if (state == null || state.getBlock() == Blocks.AIR) return;

        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ParameterSet p = getParameters(itemstack, pos, player);
        for (int x = p.xstart; x <= p.xstop; x++) {
            for (int y = p.ystart; y <= p.ystop; y++) {
                for (int z = p.zstart; z <= p.zstop; z++) {
                    if (p.round && (x*x + y*y + z*z > p.radius)) continue;
                    BlockPos bp = p.startPos.add(x, y, z);
                    outlineBlock(bp, ev.getPartialTicks());
                }
            }
        }

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    private void outlineBlock(BlockPos pos, double partialTicks) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.setTranslation(-d0, -d1, -d2);
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        Color color = new Color(255, 255, 255, 64);
        renderRectangle(tes, bb, aabb, color);
        //drawMask(bb, sX, sY, sZ, sX + 1, sY + 1, sZ + 1, 1, 1, 1, 1);
        bb.setTranslation(0, 0, 0);
    }

    private void renderRectangle(Tessellator tes, BufferBuilder bb, AxisAlignedBB aabb, Color color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(aabb.minX, aabb.minY, aabb.minZ);
        GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());


        double x = aabb.maxX - aabb.minX;
        double y = aabb.maxY - aabb.minY;
        double z = aabb.maxZ - aabb.minZ;

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x, y, 0).endVertex();
        bb.pos(0, y, 0).endVertex();
        bb.pos(0, y, z).endVertex();
        bb.pos(x, y, z).endVertex();
        tes.draw();
        
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x, 0, z).endVertex();
        bb.pos(0, 0, z).endVertex();
        bb.pos(0, 0, 0).endVertex();
        bb.pos(x, 0, 0).endVertex();
        tes.draw();

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x, 0, 0).endVertex();
        bb.pos(0, 0, 0).endVertex();
        bb.pos(0, y, 0).endVertex();
        bb.pos(x, y, 0).endVertex();
        tes.draw();

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x, y, z).endVertex();
        bb.pos(0, y, z).endVertex();
        bb.pos(0, 0, z).endVertex();
        bb.pos(x, 0, z).endVertex();
        tes.draw();

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(0, y, 0).endVertex();
        bb.pos(0, 0, 0).endVertex();
        bb.pos(0, 0, z).endVertex();
        bb.pos(0, y, z).endVertex();
        tes.draw();

        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        bb.pos(x, y, z).endVertex();
        bb.pos(x, 0, z).endVertex();
        bb.pos(x, 0, 0).endVertex();
        bb.pos(x, y, 0).endVertex();
        tes.draw();

        GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
        GlStateManager.popMatrix();
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

}

