package at.witho.totally_op;

import at.witho.totally_op.blocks.PeacefulFlower;
import at.witho.totally_op.config.Config;
import at.witho.totally_op.entity.TotallyOpDamage;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobInteraction {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingSetAttackTarget(LivingSetAttackTargetEvent e) {
	    if (!Config.friendlyMobs) return;
		if (e != null && e.getTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.getTarget();
			if (e.getEntity() instanceof EntityLiving && Helper.hasPeacefulItem(player)) {
				EntityLiving entity = (EntityLiving)e.getEntity();
				entity.setAttackTarget(null);
				entity.setRevengeTarget(null);
			}
		}
			
	}

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void playerInteract(PlayerInteractEvent.EntityInteractSpecific e) {
	    Entity ent = e.getTarget();
	    if (ent == null) return;
	    if (ent instanceof MultiPartEntityPart) {
            IEntityMultiPart mp = ((MultiPartEntityPart) ent).parent;
            if (mp instanceof EntityLiving) ent = (EntityLiving)mp;
        }
        if (ent instanceof EntityLiving) {
            ItemStack stack = e.getItemStack();
            if (!stack.isEmpty() && stack.isItemEqual(new ItemStack(ModBlocks.peaceful_flower))) {
                EntityLiving entity = (EntityLiving)ent;
                if (e.getWorld().isRemote) {
                    BlockPos pos = entity.getPosition();
                    e.getWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 1.0D, 0.0D);
                } else {
                    TotallyOpDamage damage = new TotallyOpDamage(e.getEntityPlayer());
                    entity.attackEntityFrom(damage, 20);
                    entity.onKillCommand();
                    if (entity instanceof EntityWitherSkeleton) {
                        entity.entityDropItem(new ItemStack(Items.SKULL, 1, 1), 0.0F);;
                    }
                    else if (entity instanceof EntityEnderman) {
                        entity.entityDropItem(new ItemStack(Items.ENDER_PEARL), 0.0F);

                    }
                    stack.setCount(stack.getCount() - 1);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void lootingLevel(LootingLevelEvent e) {
	    if (e.getDamageSource() instanceof TotallyOpDamage) {
	        e.setLootingLevel(10);
        }
    }

}
