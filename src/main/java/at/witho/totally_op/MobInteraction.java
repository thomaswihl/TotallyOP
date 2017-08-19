package at.witho.totally_op;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
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
}
