package at.witho.totally_op.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HarvestHelper {
    @SubscribeEvent
    static void onHarvest(BlockEvent.HarvestDropsEvent event) {
        event.getWorld().spawnEntity(new EntityXPOrb(event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 5));
    }
}
