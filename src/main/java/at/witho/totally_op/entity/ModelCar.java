package at.witho.totally_op.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelCar extends ModelBase {
    public static final float width = 4;
    public static final float length = 8;
    public static final float wheelOffset = 1f;
    public ModelRenderer base;
    public ModelRenderer wheel[];

    public ModelCar() {
        super();
        base = new ModelRenderer(this);
        base.addBox(-length / 2, 1, -width / 2, (int)length, 1, (int)width);
        base.setTextureSize(64, 64);
        base.setTextureOffset(0, 0);
        wheel = new ModelRenderer[4];
        wheel[0] = new ModelRenderer(this);
        wheel[0].addBox(-length / 2 + wheelOffset, 2, -width / 2, 1, 1, 1);
        wheel[1] = new ModelRenderer(this);
        wheel[1].addBox(length / 2 - wheelOffset - 1, 2, -width / 2, 1, 1, 1);
        wheel[2] = new ModelRenderer(this);
        wheel[2].addBox(-length / 2 + wheelOffset, 2, width / 2 - 1, 1, 1, 1);
        wheel[3] = new ModelRenderer(this);
        wheel[3].addBox(length / 2 - wheelOffset - 1, 2, width / 2 - 1, 1, 1, 1);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        base.render(0.5f);
        for (int i = 0; i < 4; ++i) wheel[i].render(0.5f);
    }

}
