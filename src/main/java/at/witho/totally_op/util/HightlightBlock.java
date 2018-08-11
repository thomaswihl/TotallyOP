package at.witho.totally_op.util;

import at.witho.totally_op.items.PeacefulTool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class HightlightBlock {
    public static Color color = new Color(255, 255, 255, 32);
    public static boolean pX = false;
    public static boolean mX = false;
    public static boolean pY = false;
    public static boolean mY = false;
    public static boolean pZ = false;
    public static boolean mZ = false;
    public static EnumFacing sideHit;

    public static BlockPos getBlockPos(EntityPlayerSP player) {
        RayTraceResult result = Minecraft.getMinecraft().objectMouseOver;
        if (result == null) return null;
        sideHit = result.sideHit;
        if (sideHit == null) return null;
        BlockPos pos = result.getBlockPos();
        if (pos == null) return null;
        IBlockState state = player.world.getBlockState(pos);
        if (state == null || state.getBlock() == Blocks.AIR) return null;
        return pos;
    }

    public static void begin() {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();

        GL11.glPushAttrib(GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        pX = mX = pY = mY = pZ = mZ = false;
    }

    public static void end() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    public static void outlineBlock(BlockPos pos, double partialTicks) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();
        bb.setTranslation(-d0, -d1, -d2);
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        renderRectangle(tes, bb, aabb);
        bb.setTranslation(0, 0, 0);
    }

    private static void renderRectangle(Tessellator tes, BufferBuilder bb, AxisAlignedBB aabb) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(aabb.minX, aabb.minY, aabb.minZ);
        GL11.glColor4ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) color.getAlpha());


        double x = aabb.maxX - aabb.minX;
        double y = aabb.maxY - aabb.minY;
        double z = aabb.maxZ - aabb.minZ;

        if (pY) {
            // TOP
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, 0).endVertex();
            bb.pos(0, y, 0).endVertex();
            bb.pos(0, y, z).endVertex();
            bb.pos(x, y, z).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, 0).endVertex();
            bb.pos(x, y, z).endVertex();
            bb.pos(0, y, z).endVertex();
            bb.pos(0, y, 0).endVertex();
            tes.draw();
        }
        if (mY) {
            // BOTTOM
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, 0, z).endVertex();
            bb.pos(0, 0, z).endVertex();
            bb.pos(0, 0, 0).endVertex();
            bb.pos(x, 0, 0).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, 0, z).endVertex();
            bb.pos(x, 0, 0).endVertex();
            bb.pos(0, 0, 0).endVertex();
            bb.pos(0, 0, z).endVertex();
            tes.draw();
        }

        if (mZ) {
            // LEFT
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, 0, 0).endVertex();
            bb.pos(0, 0, 0).endVertex();
            bb.pos(0, y, 0).endVertex();
            bb.pos(x, y, 0).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, 0, 0).endVertex();
            bb.pos(x, y, 0).endVertex();
            bb.pos(0, y, 0).endVertex();
            bb.pos(0, 0, 0).endVertex();
            tes.draw();
        }
        if (pZ) {
            // RIGHT
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, z).endVertex();
            bb.pos(0, y, z).endVertex();
            bb.pos(0, 0, z).endVertex();
            bb.pos(x, 0, z).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, z).endVertex();
            bb.pos(x, 0, z).endVertex();
            bb.pos(0, 0, z).endVertex();
            bb.pos(0, y, z).endVertex();
            tes.draw();
        }

        if (mX) {
            // FRONT
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(0, y, 0).endVertex();
            bb.pos(0, 0, 0).endVertex();
            bb.pos(0, 0, z).endVertex();
            bb.pos(0, y, z).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(0, y, 0).endVertex();
            bb.pos(0, y, z).endVertex();
            bb.pos(0, 0, z).endVertex();
            bb.pos(0, 0, 0).endVertex();
            tes.draw();
        }
        if (pX) {
            // BACK
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, z).endVertex();
            bb.pos(x, 0, z).endVertex();
            bb.pos(x, 0, 0).endVertex();
            bb.pos(x, y, 0).endVertex();
            tes.draw();
            bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
            bb.pos(x, y, z).endVertex();
            bb.pos(x, y, 0).endVertex();
            bb.pos(x, 0, 0).endVertex();
            bb.pos(x, 0, z).endVertex();
            tes.draw();
        }

        GL11.glColor4ub((byte) 255, (byte) 255, (byte) 255, (byte) 255);
        GlStateManager.popMatrix();
    }
}
