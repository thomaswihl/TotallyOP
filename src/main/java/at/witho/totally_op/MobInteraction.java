package at.witho.totally_op;

import at.witho.totally_op.blocks.PeacefulFlower;
import at.witho.totally_op.entity.TotallyOpDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobInteraction {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingSetAttackTarget(LivingSetAttackTargetEvent e) {
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
	    if (e.getWorld().isRemote) return;
	    Entity ent = e.getTarget();
        if (e != null && ent != null && ent instanceof EntityLiving) {
            ItemStack stack = e.getItemStack();
            if (!stack.isEmpty() && stack.isItemEqual(new ItemStack(ModBlocks.peaceful_flower))) {
                EntityLiving entity = (EntityLiving)ent;
                TotallyOpDamage damage = new TotallyOpDamage(e.getEntityPlayer());
                entity.attackEntityFrom(damage, 0);
                entity.onDeath(damage);
                stack.setCount(stack.getCount() - 1);
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
