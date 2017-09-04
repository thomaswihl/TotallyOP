package at.witho.totally_op.entity;

import at.witho.totally_op.TotallyOP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBoat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RenderCar extends Render<Car> {
    private ResourceLocation texture = new ResourceLocation(TotallyOP.MODID + ":textures/entity/car.png");
    protected ModelBase modelBoat = new ModelCar();

    public static final Factory FACTORY = new Factory();

    protected RenderCar(RenderManager renderManager) {
        super(renderManager);
    }

    public void doRender(Car entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        this.modelBoat.render(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
    @Override
    @Nonnull
    protected ResourceLocation getEntityTexture(@Nonnull Car car) {
        return texture;
    }
    public static class Factory implements IRenderFactory<Car> {
        @Override
        public Render<? super Car> createRenderFor(RenderManager manager) {
            return new RenderCar(manager);
        }

    }
}
