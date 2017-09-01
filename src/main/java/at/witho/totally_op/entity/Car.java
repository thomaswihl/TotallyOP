package at.witho.totally_op.entity;

import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

public class Car extends EntityHorse {
    public Car(World world) {
        super(world);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.setHorseSaddled(true);
        this.setHorseTamed(true);
    }

    @Override
    protected void initEntityAI() {
    }

    @Override
    public boolean canRiderInteract()
    {
        return true;
    }
}
