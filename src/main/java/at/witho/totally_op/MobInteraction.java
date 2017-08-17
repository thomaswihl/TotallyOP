package at.witho.totally_op;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MobInteraction {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void livingSetAttackTarget(LivingSetAttackTargetEvent e) {
		if (e.getTarget() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)e.getTarget();
			if (player.inventory.hasItemStack(new ItemStack(ModItems.peacefulTool))) {
				EntityLiving entity = (EntityLiving)e.getEntity();
				entity.setAttackTarget(null);
				entity.setRevengeTarget(null);
			}
		}
			
	}

}
