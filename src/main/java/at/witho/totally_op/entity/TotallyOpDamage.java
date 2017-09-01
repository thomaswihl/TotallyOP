package at.witho.totally_op.entity;

import at.witho.totally_op.TotallyOP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class TotallyOpDamage extends DamageSource {
    private EntityPlayer mPlayer;

    public TotallyOpDamage(EntityPlayer player) {
        super(TotallyOP.MODID);
        setDamageBypassesArmor();
        setMagicDamage();
        setDamageAllowedInCreativeMode();
        mPlayer = player;
    }

    public Entity getTrueSource() {
        return mPlayer;
    }

}
